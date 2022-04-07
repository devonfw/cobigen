package com.devonfw.cobigen.impl.generator;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.constants.MavenConstants;
import com.devonfw.cobigen.api.exception.CobiGenCancellationException;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.exception.PluginNotAvailableException;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TextTemplateEngine;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.api.to.GenerableArtifact;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.api.util.MavenUtil;
import com.devonfw.cobigen.api.util.SystemUtil;
import com.devonfw.cobigen.impl.config.ConfigurationHolder;
import com.devonfw.cobigen.impl.config.TemplatesConfiguration;
import com.devonfw.cobigen.impl.config.entity.Template;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.Variables;
import com.devonfw.cobigen.impl.config.resolver.PathExpressionResolver;
import com.devonfw.cobigen.impl.exceptions.PluginProcessingException;
import com.devonfw.cobigen.impl.exceptions.UnknownTemplateException;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.extension.TemplateEngineRegistry;
import com.devonfw.cobigen.impl.generator.api.GenerationProcessor;
import com.devonfw.cobigen.impl.generator.api.InputResolver;
import com.devonfw.cobigen.impl.model.ModelBuilderImpl;
import com.devonfw.cobigen.impl.util.ConfigurationClassLoaderUtil;
import com.devonfw.cobigen.impl.util.FileSystemUtil;
import com.devonfw.cobigen.impl.validator.InputValidator;
import com.google.common.collect.Maps;

/**
 * Generation processor. Caches calculations and thus should be newly created on each request.
 */
public class GenerationProcessorImpl implements GenerationProcessor {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(GenerationProcessorImpl.class);

  /** {@link ConfigurationHolder} for configuration caching purposes */
  private ConfigurationHolder configurationHolder;

  /** States, whether existing contents should be overwritten by generation */
  private boolean forceOverride;

  /** Input to process generation for */
  private Object input;

  /** Singletons of the Java classes to be served by the model implementing template logic */
  private Map<String, Object> logicClassesModel;

  /** Externally provided model to be used for generation */
  private Map<String, Object> rawModel;

  /** Report to be returned after generation processing */
  private GenerationReportTo generationReport;

  /** Temporary target root path to resolve dependent templates' destination path with */
  private Path tmpTargetRootPath;

  /** Target root path to resolve dependent templates' destination path with */
  private Path targetRootPath;

  /** {@link InputResolver} instance */
  private InputResolver inputResolver;

  /**
   * Creates a new generation processor. This instance should be used once per generate call as of the internal state
   * cannot be reused.
   *
   * @param configurationHolder {@link ConfigurationHolder} instance
   * @param inputResolver {@link InputResolver} instance
   */
  public GenerationProcessorImpl(ConfigurationHolder configurationHolder, InputResolver inputResolver) {

    this.configurationHolder = configurationHolder;
    this.inputResolver = inputResolver;
  }

  /**
   * Loads the logic classes passed to assure a singleton instance for the complete generation. Mapping from simple type
   * to instance.
   *
   * @param progressCallback callback for tracking the progress
   * @param logicClasses logic classes to instantiate.
   */
  private void loadLogicClasses(BiConsumer<String, Integer> progressCallback, List<Class<?>> logicClasses) {

    this.logicClassesModel = Maps.newHashMap();
    for (Class<?> logicClass : logicClasses) {
      try {
        progressCallback.accept("load class " + logicClass.getCanonicalName(), 100 / logicClasses.size());
        if (logicClass.isEnum()) {
          this.logicClassesModel.put(logicClass.getSimpleName(), logicClass.getEnumConstants());
        } else {
          this.logicClassesModel.put(logicClass.getSimpleName(), logicClass.newInstance());
        }
      } catch (InstantiationException | IllegalAccessException e) {
        LOG.warn(
            "The Java class '{}' could not been instantiated for template processing and thus will be missing in the model.",
            logicClass.getCanonicalName());
      }
    }
  }

  @Override
  public GenerationReportTo generate(Object input, List<? extends GenerableArtifact> generableArtifacts,
      Path targetRootPath, boolean forceOverride, Map<String, Object> rawModel,
      BiConsumer<String, Integer> progressCallback) {

    InputValidator.validateInputsUnequalNull(input, generableArtifacts);

    List<Class<?>> logicClasses = null;

    // only implicit dependency to javaplugin to lower classloader complexity
    ClassLoader inputProjectClassLoader = null;
    if (input instanceof Class) {
      inputProjectClassLoader = ((Class<?>) input).getClassLoader();
    } else if (input instanceof Object[]) {
      for (Object obj : (Object[]) input) {
        if (obj instanceof Class) {
          inputProjectClassLoader = ((Class<?>) obj).getClassLoader();
        }
      }
    }

    progressCallback.accept("Prepend Templates Classloader", 10);
    inputProjectClassLoader = prependTemplatesClassloader(inputProjectClassLoader);
    if (inputProjectClassLoader != null) {
      try {
        logicClasses = ConfigurationClassLoaderUtil.resolveUtilClasses(this.configurationHolder,
            inputProjectClassLoader);
      } catch (IOException e) {
        LOG.error("An IOException occured while resolving utility classes!", e);
      }
    }

    // initialize
    this.forceOverride = forceOverride;
    this.input = input;
    if (logicClasses != null) {
      progressCallback.accept("Load Template logic classes", 20);
      loadLogicClasses(progressCallback, logicClasses);
    }

    progressCallback.accept("Create Temporary Target Directory", 40);
    this.rawModel = rawModel;
    try {
      this.tmpTargetRootPath = Files.createTempDirectory("cobigen-");
      LOG.info("Temporary working directory: {}", this.tmpTargetRootPath);
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Could not create temporary folder.", e);
    }
    this.targetRootPath = targetRootPath;
    this.generationReport = new GenerationReportTo();

    progressCallback.accept("Load templates", 50);
    LOG.debug("Collecting templates");
    Collection<TemplateTo> templatesToBeGenerated = flatten(generableArtifacts);

    // generate
    Map<File, File> origToTmpFileTrace = Maps.newHashMap();
    try {
      LOG.debug("Generating {} templates", templatesToBeGenerated.size());
      for (TemplateTo template : templatesToBeGenerated) {
        try {
          Trigger trigger = this.configurationHolder.readContextConfiguration().getTrigger(template.getTriggerId());
          TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
          InputValidator.validateTriggerInterpreter(triggerInterpreter, trigger);
          progressCallback.accept("Generating " + template.getId(),
              Math.round(1 / (float) templatesToBeGenerated.size() * 800));
          generate(template, triggerInterpreter, origToTmpFileTrace, progressCallback);
        } catch (CobiGenCancellationException e) {
          throw (e);
        } catch (CobiGenRuntimeException e) {
          this.generationReport.setTemporaryWorkingDirectory(this.tmpTargetRootPath);
          this.generationReport.addError(e);
        } catch (Throwable e) {
          this.generationReport.setTemporaryWorkingDirectory(this.tmpTargetRootPath);
          this.generationReport.addError(new CobiGenRuntimeException(
              "Something unexpected happened" + ((e.getMessage() != null) ? ": " + e.getMessage() : "!"), e));
        }
      }
    } catch (CobiGenCancellationException e) {
      LOG.error("the Generation has been Canceled.", e);
      this.generationReport.setCancelled(true);
    }
    if (this.generationReport.isCancelled()) {
      this.generationReport.setTemporaryWorkingDirectory(this.tmpTargetRootPath);
      this.tmpTargetRootPath.toFile().deleteOnExit();
      // do nothing if cancelled
    } else if (this.generationReport.isSuccessful()) {
      try {
        for (Entry<File, File> origToTmpFile : origToTmpFileTrace.entrySet()) {
          Files.createDirectories(origToTmpFile.getKey().toPath().getParent());
          Files.copy(origToTmpFile.getValue().toPath(), origToTmpFile.getKey().toPath(),
              StandardCopyOption.REPLACE_EXISTING);
          this.generationReport.addGeneratedFile(origToTmpFile.getKey().toPath());
        }
        this.tmpTargetRootPath.toFile().deleteOnExit();
      } catch (IOException e) {
        this.generationReport.setTemporaryWorkingDirectory(this.tmpTargetRootPath);
        throw new CobiGenRuntimeException("Could not copy generated files to target location!", e);
      }
    } else {
      this.generationReport.setTemporaryWorkingDirectory(this.tmpTargetRootPath);
      LOG.warn("Generation finished non-successful. Generated contents can be reviewed in "
          + this.tmpTargetRootPath.toUri());
    }

    return this.generationReport;
  }

  /**
   * Prepend the classloader to get from the template folder to the classloader passed or create a new one. This method
   * will even make sure the code is compiled, if the templateFolder does not point to a jar, but maven project
   *
   * @param configLocation the template folder path or jar
   * @param inputProjectClassLoader an existing classloader or null
   * @return the combined classloader for the templates with classLoader argument as parent or null if both arguments
   *         passed as null
   */
  private ClassLoader prependTemplatesClassloader(ClassLoader inputProjectClassLoader) {

    Path configLocation = Paths.get(this.configurationHolder.getConfigurationLocation());
    ClassLoader combinedClassLoader = inputProjectClassLoader != null ? inputProjectClassLoader
        : Thread.currentThread().getContextClassLoader();

    if (configLocation != null && this.configurationHolder.getUtilsLocation() != null) {

      Path utilsLocation = this.configurationHolder.getUtilsLocation();
      Path pomFile;
      if (FileSystemUtil.isZipFile(utilsLocation.toUri())) {
        Path utilsPath = FileSystemUtil.createFileSystemDependentPath(utilsLocation.toUri());
        pomFile = utilsPath.resolve("pom.xml");
      } else {
        pomFile = utilsLocation.resolve("pom.xml");
      }

      Path cpCacheFile = null;
      try {
        if (Files.exists(pomFile)) {
          LOG.debug("Found templates to be configured by maven.");

          String pomFileHash = MavenUtil.generatePomFileHash(pomFile);

          if (this.configurationHolder.isJarConfig()) {
            cpCacheFile = configLocation
                .resolveSibling(String.format(MavenConstants.CLASSPATH_CACHE_FILE, pomFileHash));
          } else {
            cpCacheFile = configLocation.resolve(String.format(MavenConstants.CLASSPATH_CACHE_FILE, pomFileHash));
          }

          combinedClassLoader = MavenUtil.addURLsFromCachedClassPathsFile(cpCacheFile, pomFile, combinedClassLoader);
        }

        // prepend jar/compiled resources as well
        URL[] urls;
        if (Files.isDirectory(utilsLocation) && Files.exists(pomFile)) {
          compileTemplateUtils(utilsLocation);
          urls = new URL[] { utilsLocation.resolve("target").resolve("classes").toUri().toURL() };
        } else {
          urls = new URL[] { utilsLocation.toUri().toURL() };
        }

        combinedClassLoader = new URLClassLoader(urls, combinedClassLoader);
        return combinedClassLoader;
      } catch (MalformedURLException e) {
        throw new CobiGenRuntimeException("Invalid Path", e);
      } catch (IOException e) {
        throw new CobiGenRuntimeException("Unable to read " + cpCacheFile, e);
      }
    } else {
      combinedClassLoader = inputProjectClassLoader;
    }
    return combinedClassLoader;
  }

  /**
   * Compile a template folder by executing MVN
   *
   * @param templateFolder the cobigen template folder
   */
  private void compileTemplateUtils(Path templateFolder) {

    LOG.debug("Build templates folder {}", templateFolder);
    try {
      StartedProcess process = new ProcessExecutor().destroyOnExit().directory(templateFolder.toFile())
          .command(SystemUtil.determineMvnPath().toString(), "compile",
              // https://stackoverflow.com/a/66801171
              "-Djansi.force=true", "-Djansi.passthrough=true", "-B",
              "-Dorg.slf4j.simpleLogger.defaultLogLevel=" + (LOG.isDebugEnabled() ? "DEBUG" : "INFO"),
              "-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=WARN", "-q")
          .redirectError(Slf4jStream
              .of(LoggerFactory.getLogger(GenerationProcessorImpl.class.getName() + "." + "mvn-compile-templates"))
              .asError())
          .redirectOutput(Slf4jStream
              .of(LoggerFactory.getLogger(GenerationProcessorImpl.class.getName() + "." + "mvn-compile-templates"))
              .asInfo())
          .start();

      Future<ProcessResult> future = process.getFuture();
      ProcessResult processResult = future.get();
      if (processResult.getExitValue() != 0) {
        throw new CobiGenRuntimeException("Unable to compile template project " + templateFolder);
      }
    } catch (IOException | InterruptedException | ExecutionException e) {
      throw new CobiGenRuntimeException("Unable to compile template project " + templateFolder, e);
    }
  }

  /**
   * Flattens the {@link GenerableArtifact}s to a list of {@link TemplateTo}s also removing duplicates.
   *
   * @param generableArtifacts {@link List} of {@link GenerableArtifact}s to be flattened
   * @return {@link Collection} of collected {@link TemplateTo}s
   */
  private Collection<TemplateTo> flatten(List<? extends GenerableArtifact> generableArtifacts) {

    // create Map to remove duplicates by ID
    Map<String, TemplateTo> templateIdToTemplateMap = Maps.newHashMap();

    for (GenerableArtifact artifact : generableArtifacts) {
      if (artifact instanceof TemplateTo) {
        TemplateTo template = (TemplateTo) artifact;
        checkAndAddToTemplateMap(templateIdToTemplateMap, template);
      } else if (artifact instanceof IncrementTo) {
        for (TemplateTo template : ((IncrementTo) artifact).getTemplates()) {
          checkAndAddToTemplateMap(templateIdToTemplateMap, template);
        }
      } else {
        throw new IllegalArgumentException(
            "Unknown GenerableArtifact type '" + artifact.getClass().getCanonicalName() + "'.");
      }
    }

    return new TreeSet<>(templateIdToTemplateMap.values());
  }

  /**
   * Checks whether the template already exists in the templateIdToTemplateMap. If so, a warning will be generated and
   * the template will be overwritten in the map.
   *
   * @param templateIdToTemplateMap Mapping from template ID to template
   * @param template {@link TemplateTo} to be added.
   */
  private void checkAndAddToTemplateMap(Map<String, TemplateTo> templateIdToTemplateMap, TemplateTo template) {

    if (templateIdToTemplateMap.containsKey(template.getId())) {
      String oldTriggerId = templateIdToTemplateMap.get(template.getId()).getTriggerId();
      if (!oldTriggerId.equals(template.getTriggerId())) {
        this.generationReport.addWarning("Template with ID '" + template.getId()
            + "' has been triggered by two different triggers ['" + oldTriggerId + "','" + template.getTriggerId()
            + "']. This might lead to unintended generation results" + " if the trigger's variableAssignments differ.");
      }
    }
    templateIdToTemplateMap.put(template.getId(), template);
  }

  /**
   * Generates code for the given input with the given template and the given {@link TriggerInterpreter} to the
   * destination specified by the templates configuration.
   *
   * @param template to be processed for generation
   * @param triggerInterpreter {@link TriggerInterpreter} to be used for reading the input and creating the model
   * @param origToTmpFileTrace the mapping of temporary generated files to their original target destination to
   *        eventually finalizing the generation process
   * @param progressCallback to track progress
   * @throws InvalidConfigurationException if the inputs do not fit to the configuration or there are some configuration
   *         failures
   */
  private void generate(TemplateTo template, TriggerInterpreter triggerInterpreter, Map<File, File> origToTmpFileTrace,
      BiConsumer<String, Integer> progressCallback) {

    Trigger trigger = this.configurationHolder.readContextConfiguration().getTrigger(template.getTriggerId());

    InputReader inputReader = triggerInterpreter.getInputReader();
    if (!inputReader.isValidInput(this.input)) {
      throw new CobiGenRuntimeException("An invalid input of type " + this.input.getClass() + " has been passed to "
          + inputReader.getClass() + " (derived from trigger '" + trigger.getId() + "')");
    }

    List<Object> inputObjects = this.inputResolver.resolveContainerElements(this.input, trigger);
    TemplatesConfiguration tConfig = this.configurationHolder.readTemplatesConfiguration(trigger);
    String templateEngineName = tConfig.getTemplateEngine();
    TextTemplateEngine templateEngine = TemplateEngineRegistry.getEngine(templateEngineName);

    templateEngine.setTemplateFolder(this.configurationHolder.readContextConfiguration()
        .getConfigRootforTrigger(trigger.getId()).resolve(trigger.getTemplateFolder()));

    Template templateEty = tConfig.getTemplate(template.getId());
    if (templateEty == null) {
      throw new UnknownTemplateException(template.getId());
    }

    for (Object generatorInput : inputObjects) {
      progressCallback.accept("Building template model for input " + generatorInput, 1);
      Map<String, Object> model = buildModel(triggerInterpreter, trigger, generatorInput, templateEty);

      String targetCharset = templateEty.getTargetCharset();

      // resolve temporary file paths
      @SuppressWarnings("unchecked")
      PathExpressionResolver pathExpressionResolver = new PathExpressionResolver(
          Variables.fromMap((Map<String, String>) model.get(ModelBuilderImpl.NS_VARIABLES)));
      String resolvedTargetDestinationPath = pathExpressionResolver
          .evaluateExpressions(templateEty.getUnresolvedTargetPath());
      String resolvedTmpDestinationPath = pathExpressionResolver
          .evaluateExpressions(templateEty.getUnresolvedTemplatePath());

      File originalFile = this.targetRootPath.resolve(resolvedTargetDestinationPath).toFile();
      File tmpOriginalFile;
      if (origToTmpFileTrace.containsKey(originalFile)) {
        // use the available temporary file
        tmpOriginalFile = origToTmpFileTrace.get(originalFile);
      } else {
        tmpOriginalFile = this.tmpTargetRootPath.resolve(resolvedTmpDestinationPath).toFile();
        // remember mapping to later on copy the generated resources to its target destinations
        origToTmpFileTrace.put(originalFile, tmpOriginalFile);
      }

      if (originalFile.exists() || tmpOriginalFile.exists()) {
        if (!tmpOriginalFile.exists()) {
          try {
            FileUtils.copyFile(originalFile, tmpOriginalFile);
          } catch (IOException e) {
            throw new CobiGenRuntimeException(
                "Could not copy file " + originalFile.getPath() + " to tmp generation directory! Generation skipped.",
                e);
          }
        }

        if ((this.forceOverride || template.isForceOverride()) && templateEty.getMergeStrategy() == null
            || ConfigurationConstants.MERGE_STRATEGY_OVERRIDE.equals(templateEty.getMergeStrategy())) {
          try (Formatter formatter = new Formatter()) {
            formatter.format("Overriding %1$-40s FROM %2$-50s TO %3$s ...", originalFile.getName(),
                templateEty.getName(), resolvedTargetDestinationPath);
            LOG.info(formatter.out().toString());
            progressCallback.accept(formatter.out().toString(), 1);
          }
          progressCallback.accept("Generating " + template.getId() + " for " + generatorInput, 1);
          generateTemplateAndWriteFile(tmpOriginalFile, templateEty, templateEngine, model, targetCharset);
        } else if (templateEty.getMergeStrategy() != null) {
          try (Formatter formatter = new Formatter()) {
            formatter.format("Merging    %1$-40s FROM %2$-50s TO %3$s ...", originalFile.getName(),
                templateEty.getName(), resolvedTargetDestinationPath);
            LOG.info(formatter.out().toString());
            progressCallback.accept(formatter.out().toString(), 1);
          }
          String patch = null;
          try (Writer out = new StringWriter()) {
            templateEngine.process(templateEty, model, out, targetCharset);
            patch = out.toString();
            String mergeResult = null;
            Merger merger = PluginRegistry.getMerger(templateEty.getMergeStrategy());
            if (merger != null) {
              mergeResult = merger.merge(tmpOriginalFile, patch, targetCharset);
            } else {
              throw new PluginNotAvailableException("merge strategy '" + templateEty.getMergeStrategy() + "'", null);
            }

            if (mergeResult != null) {
              LOG.debug("Merge {} with char set {}.", tmpOriginalFile.getName(), targetCharset);
              FileUtils.writeStringToFile(tmpOriginalFile, mergeResult, targetCharset);
            } else {
              throw new PluginProcessingException(
                  "Merger " + merger.getType() + " returned null on merge(...), which is not allowed.");
            }
          } catch (MergeException e) {
            writeBrokenPatchFile(targetCharset, tmpOriginalFile, patch);
            // enrich merge exception to provide template ID
            throw new MergeException(e, templateEty.getAbsoluteTemplatePath());
          } catch (IOException e) {
            throw new CobiGenRuntimeException("Could not write file " + tmpOriginalFile.toPath() + " after merge.", e);
          }
        }
      } else {
        try (Formatter formatter = new Formatter()) {
          formatter.format("Generating %1$-40s FROM %2$-50s TO %3$s ...", originalFile.getName(), templateEty.getName(),
              resolvedTargetDestinationPath);
          LOG.info(formatter.out().toString());
          progressCallback.accept(formatter.out().toString(), 1);
        }
        generateTemplateAndWriteFile(tmpOriginalFile, templateEty, templateEngine, model, targetCharset);
      }
    }
  }

  /**
   * Writes a broken patch file to the file system. As an invalid generation will not lead to a merge into the code
   * base, we simply can generate it next to the target file.
   *
   * @param targetCharset target charset to write the file with.
   * @param tmpOriginalFile the temporary file to originally merge to
   * @param patch the generated patch
   */
  private void writeBrokenPatchFile(String targetCharset, File tmpOriginalFile, String patch) {

    boolean written = false;
    int i = 0;
    while (!written) {
      String fileextension = FilenameUtils.getExtension(tmpOriginalFile.getName());
      String baseName = FilenameUtils.getBaseName(tmpOriginalFile.getName());
      Path newPatchFile = tmpOriginalFile.toPath().getParent()
          .resolve(baseName + ".patch." + i++ + "." + fileextension);
      if (newPatchFile.toFile().exists()) {
        continue;
      } else {
        try {
          FileUtils.writeStringToFile(newPatchFile.toFile(), patch, targetCharset);
        } catch (IOException e) {
          // Just log as this should not happen and is not a direct error of generation
          LOG.error("Could not write broken patch to file {}", newPatchFile, e);
        } finally {
          // just to assure to not end up in an infinite execution
          written = true;
        }
      }
    }
  }

  /**
   * Builds the model for he given input.
   *
   * @param triggerInterpreter {@link TriggerInterpreter} to be used
   * @param trigger activated {@link Trigger}
   * @param generatorInput input for generation to retrieve information from.
   * @param template the internal {@link Template} representation
   * @return the object model for generation.
   */
  private Map<String, Object> buildModel(TriggerInterpreter triggerInterpreter, Trigger trigger, Object generatorInput,
      Template template) {

    ModelBuilderImpl modelBuilderImpl = new ModelBuilderImpl(generatorInput, trigger);
    Map<String, Object> model;
    if (this.rawModel != null) {
      model = this.rawModel;
    } else {
      model = modelBuilderImpl.createModel(triggerInterpreter);
    }
    modelBuilderImpl.enrichByContextVariables(model, triggerInterpreter, template, this.targetRootPath,
        this.generationReport);
    if (this.logicClassesModel != null) {
      model.putAll(this.logicClassesModel);
    }
    return model;
  }

  /**
   * Generates the given template contents using the given model and writes the contents into the given {@link File}
   *
   * @param output {@link File} to be written
   * @param template FreeMarker template which will generate the contents
   * @param templateEngine template engine to be used
   * @param model to generate with
   * @param outputCharset charset the target file should be written with
   */
  private void generateTemplateAndWriteFile(File output, Template template, TextTemplateEngine templateEngine,
      Map<String, Object> model, String outputCharset) {

    try (Writer out = new StringWriter()) {
      templateEngine.process(template, model, out, outputCharset);
      FileUtils.writeStringToFile(output, out.toString(), outputCharset);
    } catch (IOException e) {
      throw new CobiGenRuntimeException(
          "Could not write file while processing template " + template.getAbsoluteTemplatePath(), e);
    }
  }
}

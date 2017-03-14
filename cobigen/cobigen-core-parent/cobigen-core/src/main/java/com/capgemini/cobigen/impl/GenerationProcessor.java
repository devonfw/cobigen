package com.capgemini.cobigen.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.constants.ConfigurationConstants;
import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;
import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.api.extension.TextTemplate;
import com.capgemini.cobigen.api.extension.TextTemplateEngine;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.api.to.GenerableArtifact;
import com.capgemini.cobigen.api.to.GenerationReportTo;
import com.capgemini.cobigen.api.to.IncrementTo;
import com.capgemini.cobigen.api.to.MatcherTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.impl.config.ConfigurationHolder;
import com.capgemini.cobigen.impl.config.TemplatesConfiguration;
import com.capgemini.cobigen.impl.config.entity.ContainerMatcher;
import com.capgemini.cobigen.impl.config.entity.Matcher;
import com.capgemini.cobigen.impl.config.entity.Template;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.config.entity.io.AccumulationType;
import com.capgemini.cobigen.impl.config.resolver.PathExpressionResolver;
import com.capgemini.cobigen.impl.exceptions.PluginProcessingException;
import com.capgemini.cobigen.impl.exceptions.UnknownTemplateException;
import com.capgemini.cobigen.impl.model.ContextVariableResolver;
import com.capgemini.cobigen.impl.model.ModelBuilderImpl;
import com.capgemini.cobigen.impl.util.CopyDirectoryVisitor;
import com.capgemini.cobigen.impl.validator.InputValidator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Generation processor. Caches calculations and thus should be newly created on each request.
 */
public class GenerationProcessor {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(GenerationProcessor.class);

    /** {@link ConfigurationHolder} for configuration caching purposes */
    private ConfigurationHolder configurationHolder;

    /** {@link TextTemplateEngine} used for {@link TextTemplate} processing */
    private TextTemplateEngine templateEngine;

    /** States, whether existing contents should be overwritten by generation */
    private boolean forceOverride;

    /** Input to process generation for */
    private Object input;

    /** Artifacts to generate, e.g. templates or increments */
    private List<? extends GenerableArtifact> generableArtifacts;

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

    /**
     * Creates a new {@link GenerationProcessor} instance. Due to caching, one instance should only be used
     * for one generation request.
     *
     * @param configurationHolder
     *            {@link ConfigurationHolder} instance
     * @param templateEngine
     *            {@link TextTemplateEngine template engine to be used for generation}
     * @param input
     *            generator input object
     * @param generableArtifacts
     *            a {@link List} of artifacts to be generated
     * @param targetRootPath
     *            target root path to generate to (to be used to resolve the dependent template destination
     *            paths)
     * @param forceOverride
     *            if <code>true</code> and the destination path is already existent, the contents will be
     *            overwritten by the generated ones iff there is no merge strategy defined by the templates
     *            configuration. (default: {@code false})
     * @param logicClasses
     *            a {@link List} of java class files, which will be included as accessible beans in the
     *            template model. Such classes can be used to implement more complex template logic.
     * @param rawModel
     *            externally adapted model to be used for generation.
     */
    public GenerationProcessor(ConfigurationHolder configurationHolder, TextTemplateEngine templateEngine, Object input,
        List<? extends GenerableArtifact> generableArtifacts, Path targetRootPath, boolean forceOverride,
        List<Class<?>> logicClasses, Map<String, Object> rawModel) {

        InputValidator.validateInputsUnequalNull(input, generableArtifacts);

        this.configurationHolder = configurationHolder;
        this.templateEngine = templateEngine;
        this.forceOverride = forceOverride;
        this.input = input;
        this.generableArtifacts = generableArtifacts;
        if (logicClasses != null) {
            loadLogicClasses(logicClasses);
        }
        this.rawModel = rawModel;
        try {
            tmpTargetRootPath = Files.createTempDirectory("cobigen-");
        } catch (IOException e) {
            throw new CobiGenRuntimeException("Could not create temporary folder.", e);
        }
        this.targetRootPath = targetRootPath;

        generationReport = new GenerationReportTo();
    }

    /**
     * Loads the logic classes passed to assure a singleton instance for the complete generation. Mapping from
     * simple type to instance.
     * @param logicClasses
     *            logic classes to instantiate.
     */
    private void loadLogicClasses(List<Class<?>> logicClasses) {
        logicClassesModel = Maps.newHashMap();
        for (Class<?> logicClass : logicClasses) {
            try {
                logicClassesModel.put(logicClass.getSimpleName(), logicClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.warn(
                    "The Java class '{}' could not been instantiated for template processing and thus will be missing in the model.",
                    logicClass.getCanonicalName());
            }
        }
    }

    /**
     * Generates code by processing the {@link List} of {@link GenerableArtifact}s for the given input.
     * @return {@link GenerationReportTo the GenerationReport}
     */
    GenerationReportTo generate() {

        Collection<TemplateTo> templatesToBeGenerated = flatten(generableArtifacts);

        for (TemplateTo template : templatesToBeGenerated) {
            try {
                Trigger trigger = configurationHolder.readContextConfiguration().getTrigger(template.getTriggerId());
                TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
                InputValidator.validateTriggerInterpreter(triggerInterpreter, trigger);
                generate(template, triggerInterpreter);
            } catch (CobiGenRuntimeException e) {
                generationReport.setIncompleteGenerationPath(tmpTargetRootPath);
                generationReport.addError(e);
            } catch (Throwable e) {
                generationReport.setIncompleteGenerationPath(tmpTargetRootPath);
                generationReport.addError(new CobiGenRuntimeException(
                    "Something unexpected happened" + ((e.getMessage() != null) ? ": " + e.getMessage() : "!"), e));
            }
        }

        if (generationReport.isSuccessful()) {
            try {
                Files.walkFileTree(tmpTargetRootPath,
                    new CopyDirectoryVisitor(tmpTargetRootPath, targetRootPath, StandardCopyOption.REPLACE_EXISTING));
                tmpTargetRootPath.toFile().delete();
            } catch (IOException e) {
                generationReport.setIncompleteGenerationPath(tmpTargetRootPath);
                throw new CobiGenRuntimeException("Could not copy generated files to target location!", e);
            }
        } else {
            generationReport.setIncompleteGenerationPath(tmpTargetRootPath);
            LOG.warn("Generation finished non-successful. Generated contents can be reviewed in "
                + tmpTargetRootPath.toUri());
        }

        return generationReport;
    }

    /**
     * Flattens the {@link GenerableArtifact}s to a list of {@link TemplateTo}s also removing duplicates.
     * @param generableArtifacts
     *            {@link List} of {@link GenerableArtifact}s to be flattened
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

        return templateIdToTemplateMap.values();
    }

    /**
     * Checks whether the template already exists in the templateIdToTemplateMap. If so, a warning will be
     * generated and the template will be overwritten in the map.
     * @param templateIdToTemplateMap
     *            Mapping from template ID to template
     * @param template
     *            {@link TemplateTo} to be added.
     */
    private void checkAndAddToTemplateMap(Map<String, TemplateTo> templateIdToTemplateMap, TemplateTo template) {
        if (templateIdToTemplateMap.containsKey(template.getId())) {
            String oldTriggerId = templateIdToTemplateMap.get(template.getId()).getTriggerId();
            if (!oldTriggerId.equals(template.getTriggerId())) {
                generationReport.addWarning("Template with ID '" + template.getId()
                    + "' has been triggered by two different triggers ['" + oldTriggerId + "','"
                    + template.getTriggerId() + "']. This might lead to unintended generation results"
                    + " if the trigger's variableAssignments differ.");
            }
        }
        templateIdToTemplateMap.put(template.getId(), template);
    }

    /**
     * Generates code for the given input with the given template and the given {@link TriggerInterpreter} to
     * the destination specified by the templates configuration.
     *
     * @param template
     *            to be processed for generation
     * @param triggerInterpreter
     *            {@link TriggerInterpreter} to be used for reading the input and creating the model
     * @throws InvalidConfigurationException
     *             if the inputs do not fit to the configuration or there are some configuration failures
     */
    private void generate(TemplateTo template, TriggerInterpreter triggerInterpreter) {

        Trigger trigger = configurationHolder.readContextConfiguration().getTrigger(template.getTriggerId());
        templateEngine.setTemplateFolder(
            configurationHolder.readContextConfiguration().getConfigurationPath().resolve(trigger.getTemplateFolder()));

        InputReader inputReader = triggerInterpreter.getInputReader();
        if (!inputReader.isValidInput(input)) {
            throw new CobiGenRuntimeException("An invalid input of type " + input.getClass() + " has been passed to "
                + inputReader.getClass() + " (derived from trigger '" + trigger.getId() + "')");
        }

        List<Object> inputObjects = collectInputObjects(input, triggerInterpreter, trigger);
        Template templateEty = getTemplate(template, triggerInterpreter);

        for (Object generatorInput : inputObjects) {

            Map<String, Object> model = buildModel(triggerInterpreter, trigger, generatorInput);

            String targetCharset = templateEty.getTargetCharset();
            LOG.info("Generating template '{}' ...", templateEty.getName(), generatorInput);

            Map<String, String> variables =
                new ContextVariableResolver(generatorInput, trigger).resolveVariables(triggerInterpreter);

            PathExpressionResolver pathExpressionResolver = new PathExpressionResolver(variables);
            String resolvedTargetDestinationPath =
                pathExpressionResolver.evaluateExpressions(templateEty.getUnresolvedTargetPath());
            String resolvedTmpDestinationPath =
                pathExpressionResolver.evaluateExpressions(templateEty.getUnresolvedTemplatePath());
            File originalFile = targetRootPath.resolve(resolvedTargetDestinationPath).toFile();
            File tmpOriginalFile = tmpTargetRootPath.resolve(resolvedTmpDestinationPath).toFile();

            if (originalFile.exists() || tmpOriginalFile.exists()) {
                if (!tmpOriginalFile.exists()) {
                    try {
                        FileUtils.copyFile(originalFile, tmpOriginalFile);
                    } catch (IOException e) {
                        throw new CobiGenRuntimeException("Could not copy file " + originalFile.getPath()
                            + " to tmp generation directory! Generation skipped.", e);
                    }
                }

                if (forceOverride || template.isForceOverride() && templateEty.getMergeStrategy() == null
                    || ConfigurationConstants.MERGE_STRATEGY_OVERRIDE.equals(templateEty.getMergeStrategy())) {
                    generateTemplateAndWriteFile(tmpOriginalFile, templateEty, model, targetCharset);
                } else {
                    String patch = null;
                    try (Writer out = new StringWriter()) {
                        templateEngine.process(templateEty, model, out, targetCharset);
                        patch = out.toString();
                        String result = null;
                        Merger merger = PluginRegistry.getMerger(templateEty.getMergeStrategy());
                        if (merger != null) {
                            result = merger.merge(tmpOriginalFile, patch, targetCharset);
                        } else {
                            throw new InvalidConfigurationException(
                                "No merger for merge strategy '" + templateEty.getMergeStrategy() + "' found.");
                        }

                        if (result != null) {
                            LOG.debug("Merge {} with char set {}.", tmpOriginalFile.getName(), targetCharset);
                            FileUtils.writeStringToFile(tmpOriginalFile, result, targetCharset);
                        } else {
                            throw new PluginProcessingException(
                                "Merger " + merger.getType() + " returned null on merge(...), which is not allowed.");
                        }
                    } catch (MergeException e) {
                        writeBrokenPatchFile(targetCharset, tmpOriginalFile, patch);
                        // enrich merge exception to provide template ID
                        throw new MergeException(e, templateEty.getAbsoluteTemplatePath());
                    } catch (IOException e) {
                        throw new CobiGenRuntimeException(
                            "Could not write file " + tmpOriginalFile.toPath() + " after merge.", e);
                    }
                }
            } else {
                LOG.info("Create new File {} with charset {}.", tmpOriginalFile.toURI(), targetCharset);
                generateTemplateAndWriteFile(tmpOriginalFile, templateEty, model, targetCharset);
            }
        }
    }

    /**
     * Writes a broken patch file to the file system. As an invalid generation will not lead to a merge into
     * the code base, we simply can generate it next to the target file.
     * @param targetCharset
     *            target charset to write the file with.
     * @param tmpOriginalFile
     *            the temporary file to originally merge to
     * @param patch
     *            the generated patch
     */
    private void writeBrokenPatchFile(String targetCharset, File tmpOriginalFile, String patch) {

        boolean written = false;
        int i = 0;
        while (!written) {
            String fileextension = FilenameUtils.getExtension(tmpOriginalFile.getName());
            String baseName = FilenameUtils.getBaseName(tmpOriginalFile.getName());
            Path newPatchFile =
                tmpOriginalFile.toPath().getParent().resolve(baseName + ".patch." + i++ + "." + fileextension);
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
     * @param triggerInterpreter
     *            {@link TriggerInterpreter} to be used
     * @param trigger
     *            activated {@link Trigger}
     * @param generatorInput
     *            input for generation to retrieve information from.
     * @return the object model for generation.
     */
    private Map<String, Object> buildModel(TriggerInterpreter triggerInterpreter, Trigger trigger,
        Object generatorInput) {
        ModelBuilderImpl modelBuilderImpl = new ModelBuilderImpl(generatorInput, trigger);
        Map<String, Object> model;
        if (rawModel != null) {
            model = rawModel;
        } else {
            model = modelBuilderImpl.createModel(triggerInterpreter);
        }
        modelBuilderImpl.enrichByContextVariables(model, triggerInterpreter);
        if (logicClassesModel != null) {
            model.putAll(logicClassesModel);
        }
        return model;
    }

    /**
     * Collects all input objects. Especially, resolves container inputs.
     * @param input
     *            object
     * @param triggerInterpreter
     *            {@link TriggerInterpreter} to be used
     * @param trigger
     *            {@link Trigger} to be used
     * @return the {@link List} of collected input objects.
     */
    private List<Object> collectInputObjects(Object input, TriggerInterpreter triggerInterpreter, Trigger trigger) {

        InputReader inputReader = triggerInterpreter.getInputReader();
        List<Object> inputObjects = Lists.newArrayList(input);
        if (inputReader.combinesMultipleInputObjects(input)) {

            // check whether the inputs should be retrieved recursively
            boolean retrieveInputsRecursively = false;
            for (ContainerMatcher containerMatcher : trigger.getContainerMatchers()) {
                MatcherTo matcherTo = new MatcherTo(containerMatcher.getType(), containerMatcher.getValue(), input);
                if (triggerInterpreter.getMatcher().matches(matcherTo)) {
                    if (!retrieveInputsRecursively) {
                        retrieveInputsRecursively = containerMatcher.isRetrieveObjectsRecursively();
                    } else {
                        break;
                    }
                }
            }

            if (retrieveInputsRecursively) {
                inputObjects = inputReader.getInputObjectsRecursively(input, trigger.getInputCharset());
            } else {
                inputObjects = inputReader.getInputObjects(input, trigger.getInputCharset());
            }

            // Remove non matching inputs
            Iterator<Object> it = inputObjects.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (!matches(next, trigger.getMatcher(), triggerInterpreter)) {
                    it.remove();
                }
            }
        }
        return inputObjects;
    }

    /**
     * Checks whether the list of matches matches the matcher input according to the given trigger
     * interpreter.
     * @param matcherInput
     *            input for the matcher
     * @param matcherList
     *            list of matchers to be checked
     * @param triggerInterpreter
     *            to called for checking retrieving the matchers matching result
     * @return <code>true</code> if the given matcher input matches the matcher list<br>
     *         <code>false</code>, otherwise
     */
    public static boolean matches(Object matcherInput, List<Matcher> matcherList,
        TriggerInterpreter triggerInterpreter) {
        boolean matcherSetMatches = false;
        LOG.info("Check matchers for TriggerInterpreter[type='{}'] ...", triggerInterpreter.getType());
        MATCHER_LOOP:
        for (Matcher matcher : matcherList) {
            MatcherTo matcherTo = new MatcherTo(matcher.getType(), matcher.getValue(), matcherInput);
            LOG.debug("Check {} ...", matcherTo);
            if (triggerInterpreter.getMatcher().matches(matcherTo)) {
                switch (matcher.getAccumulationType()) {
                case NOT:
                    LOG.debug("NOT Matcher matches -> trigger match fails.");
                    matcherSetMatches = false;
                    break MATCHER_LOOP;
                case OR:
                case AND:
                    LOG.debug("Matcher matches.");
                    matcherSetMatches = true;
                    break;
                default:
                }
            } else {
                if (matcher.getAccumulationType() == AccumulationType.AND) {
                    LOG.debug("AND Matcher does not match -> trigger match fails.");
                    matcherSetMatches = false;
                    break MATCHER_LOOP;
                }
            }
        }
        LOG.info("Matcher declarations " + (matcherSetMatches ? "match the input." : "do not match the input."));
        return matcherSetMatches;
    }

    /**
     * Generates the given template contents using the given model and writes the contents into the given
     * {@link File}
     *
     * @param output
     *            {@link File} to be written
     * @param template
     *            FreeMarker template which will generate the contents
     * @param model
     *            to generate with
     * @param outputCharset
     *            charset the target file should be written with
     */
    private void generateTemplateAndWriteFile(File output, Template template, Map<String, Object> model,
        String outputCharset) {

        try (Writer out = new StringWriter()) {
            templateEngine.process(template, model, out, outputCharset);
            FileUtils.writeStringToFile(output, out.toString(), outputCharset);
        } catch (IOException e) {
            throw new CobiGenRuntimeException(
                "Could not write file while processing template " + template.getAbsoluteTemplatePath(), e);
        }
    }

    /**
     * Returns the {@link Template} for a given {@link TemplateTo}
     *
     * @param templateTo
     *            which should be found as internal representation
     * @param triggerInterpreter
     *            to be used for variable resolving (for the final destination path)
     * @return the recovered {@link Template} object
     * @throws InvalidConfigurationException
     *             if at least one of the destination path variables could not be resolved
     */
    private Template getTemplate(TemplateTo templateTo, TriggerInterpreter triggerInterpreter)
        throws InvalidConfigurationException {

        Trigger trigger = configurationHolder.readContextConfiguration().getTrigger(templateTo.getTriggerId());
        TemplatesConfiguration tConfig = configurationHolder.readTemplatesConfiguration(trigger, triggerInterpreter);
        Template template = tConfig.getTemplate(templateTo.getId());
        if (template == null) {
            throw new UnknownTemplateException(templateTo.getId());
        }
        return template;
    }
}

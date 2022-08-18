package com.devonfw.cobigen.impl.config.reader;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.api.exception.UnknownExpressionException;
import com.devonfw.cobigen.api.extension.TextTemplateEngine;
import com.devonfw.cobigen.api.util.ExceptionUtil;
import com.devonfw.cobigen.api.util.JvmUtil;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.impl.config.ConfigurationHolder;
import com.devonfw.cobigen.impl.config.constant.MavenMetadata;
import com.devonfw.cobigen.impl.config.constant.TemplateSetConfigurationVersion;
import com.devonfw.cobigen.impl.config.entity.ContainerMatcher;
import com.devonfw.cobigen.impl.config.entity.Matcher;
import com.devonfw.cobigen.impl.config.entity.Template;
import com.devonfw.cobigen.impl.config.entity.TemplateFile;
import com.devonfw.cobigen.impl.config.entity.TemplateFolder;
import com.devonfw.cobigen.impl.config.entity.TemplatePath;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.VariableAssignment;
import com.devonfw.cobigen.impl.config.entity.io.TemplateExtension;
import com.devonfw.cobigen.impl.config.entity.io.TemplateScan;
import com.devonfw.cobigen.impl.config.entity.io.TemplateScans;
import com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration;
import com.devonfw.cobigen.impl.config.entity.io.Templates;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator.Type;
import com.devonfw.cobigen.impl.exceptions.UnknownContextVariableException;
import com.devonfw.cobigen.impl.extension.TemplateEngineRegistry;
import com.devonfw.cobigen.impl.util.FileSystemUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;

/**
 * The {@link TemplateSetConfigurationReader} combines everything from the {@link TemplatesConfigurationReader} and
 * {@link ContextConfigurationReader}
 */
public class TemplateSetConfigurationReader {

  /** Map with the paths of the configuration locations for a template-set.xml file */
  private Map<Path, Path> configLocations = new HashMap<>();

  /** Map with the paths of the template set location for a trigger */
  private Map<String, Path> triggerTemplateSetLocations = new HashMap<>();

  /** Map with XML Nodes 'template-set' of the template-set.xml files */
  protected Map<Path, TemplateSetConfiguration> templateSetConfigurations;

  /** Paths of the template set configuration files */
  protected List<Path> templateSetFiles;

  /** Root of the template set configuration file, used for passing to TemplateSetConfiguration */
  protected Path templateSetsRoot;

  /** JAXB root node of the configuration */
  private TemplateSetConfiguration configNode;

  /** Configuration file */
  private Path configFilePath;

  /**
   * The {@link Properties#getProperty(String) name of the property} to relocate a template target folder.
   */
  private static final String PROPERTY_RELOCATE = "relocate";

  /** The syntax for the variable pointing to the current working directory (CWD) of a template. */
  private static final String VARIABLE_CWD = "${cwd}";

  /** {@link JXPathContext} for the configNode */
  @SuppressWarnings("unused")
  private JXPathContext xPathContext;

  /** Cache to find all templates by name for each template scan */
  private Map<String, List<String>> templateScanTemplates = Maps.newHashMap();

  /** The top-level folder where the templates are located. */
  private TemplateFolder rootTemplateFolder;

  /** The {@link ConfigurationHolder} used for reading templates folder **/
  @SuppressWarnings("unused")
  private ConfigurationHolder configurationHolder;

  /**
   * The constructor.
   *
   * @param configRoot the templateSet root directory
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public TemplateSetConfigurationReader(Path configRoot) throws InvalidConfigurationException {

    if (configRoot == null)
      throw new IllegalArgumentException("Configuraion path cannot be null.");

    this.templateSetFiles = new ArrayList<>();

    Path templateSetsDownloaded = configRoot.resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    Path templateSetsAdapted = configRoot.resolve(ConfigurationConstants.ADAPTED_FOLDER);

    if (!Files.exists(templateSetsDownloaded) && !Files.exists(templateSetsAdapted)) {
      throw new InvalidConfigurationException(configRoot,
          "Could not find a folder in which to search for the template set configuration file.");
    } else {
      if (Files.exists(templateSetsAdapted)) {
        this.templateSetFiles.addAll(loadTemplateSetFilesAdapted(templateSetsAdapted));
      }

      // if (Files.exists(templateSetsDownloaded)) {
      // this.templateSetFiles.addAll(loadTemplateSetFilesDownloaded(templateSetsDownloaded));
      // }

      if (this.templateSetFiles.isEmpty()) {
        throw new InvalidConfigurationException(configRoot,
            "Could not find any template set configuration file in the given folder.");
      }
    }

    // TODO: Merge other constructor in here!
    for (int i = 0; i < this.templateSetFiles.size(); i++) {
      Path templateSetFile = this.templateSetFiles.get(i);
      Path templateLocation;

      Path rootTemplatePath = configRoot.resolve(templateSetFile).getParent();

      this.configFilePath = templateSetFile;
      Path configFilePath = this.configFilePath;

      if (!Files.exists(configFilePath)) {
        Path sourceTemplatePath = configRoot.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
        sourceTemplatePath = sourceTemplatePath.resolve(templateSetFile.getParent());
        configFilePath = sourceTemplatePath.resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);
        templateLocation = sourceTemplatePath;
      }
      if (!Files.exists(configFilePath)) {
        throw new InvalidConfigurationException(configFilePath, "Could not find templates set configuration file.");
      } else {

        // Change this line to templatesLocation = rootTemplatePath if additional "templates" folder is removed
        templateLocation = Path.of(rootTemplatePath + "/templates");
      }
      this.rootTemplateFolder = TemplateFolder.create(templateLocation);
    }

    readConfiguration();
  }

  // TODO: Either the templates are saved as: src/main/templates
  // or the Constant TEMPLATE_RESOURCE_FOLDER has to be changed to "src/main/templates/templates"

  // TODO
  // /**
  // * The templates constructor.
  // *
  // * @param projectRoot
  // * @param templateFolder
  // * @param configurationHolder
  // * @throws InvalidConfigurationException
  // */
  // public TemplateSetConfigurationReader(Path projectRoot, String templateFolder,
  // ConfigurationHolder configurationHolder) throws InvalidConfigurationException {
  //
  // Path templateLocation;
  //
  // Path rootTemplatePath = projectRoot.resolve(templateFolder);
  // this.configFilePath = rootTemplatePath.resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);
  //
  // if (!Files.exists(this.configFilePath)) {
  // Path sourceTemplatePath = projectRoot.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
  // sourceTemplatePath = sourceTemplatePath.resolve(templateFolder);
  // this.configFilePath = sourceTemplatePath.resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);
  // templateLocation = sourceTemplatePath;
  // }
  // if (!Files.exists(this.configFilePath)) {
  // throw new InvalidConfigurationException(this.configFilePath, "Could not find templates set configuration file.");
  // } else {
  // templateLocation = rootTemplatePath;
  // }
  // this.rootTemplateFolder = TemplateFolder.create(templateLocation);
  // readConfiguration();
  // this.configurationHolder = configurationHolder;
  // }

  // TODO
  // /**
  // * Creates a new instance of the {@link TemplatesConfigurationReader} which initially parses the given configuration
  // * file without a ConfigurationFolder
  // *
  // * @param projectRoot root path for the templates, has to be an absolute path
  // * @param templateFolder name of the folder containing the configuration and templates, has to be a relative path
  // * @throws InvalidConfigurationException if the configuration is not valid against its xsd specification
  // */
  // public TemplateSetConfigurationReader(Path projectRoot, String templateFolder) {
  //
  // this(projectRoot, templateFolder, null);
  // }

  /**
   * TODO
   */
  protected void readConfiguration() {

    // workaround to make JAXB work in OSGi context by
    // https://github.com/ControlSystemStudio/cs-studio/issues/2530#issuecomment-450991188
    final ClassLoader orig = Thread.currentThread().getContextClassLoader();
    if (JvmUtil.isRunningJava9OrLater()) {
      Thread.currentThread().setContextClassLoader(JAXBContext.class.getClassLoader());
    }

    this.templateSetConfigurations = new HashMap<>();

    for (int i = 0; i < this.templateSetFiles.size(); i++) {
      Path templateSetFile = this.templateSetFiles.get(i);
      try (InputStream in = Files.newInputStream(templateSetFile)) {
        Unmarshaller unmarschaller = JAXBContext.newInstance(TemplateSetConfiguration.class).createUnmarshaller();

        // Unmarshal without schema checks for getting the version attribute of the root node.
        // This is necessary to provide an automatic upgrade client later on
        Object rootNode = unmarschaller.unmarshal(in);
        BigDecimal configVersion;
        if (rootNode instanceof TemplateSetConfiguration) {
          configVersion = ((TemplateSetConfiguration) rootNode).getVersion();
          if (configVersion == null) {
            throw new InvalidConfigurationException(templateSetFile,
                "The required 'version' attribute of node \"templateSetConfiguration\" has not been set");
          } else {
            VersionValidator validator = new VersionValidator(Type.TEMPLATE_SET_CONFIGURATION, MavenMetadata.VERSION);
            try {
              validator.validate(configVersion.floatValue());
            } catch (NotYetSupportedException e) {
              // TODO
            }
          }
        } else {
          throw new InvalidConfigurationException(templateSetFile,
              "Unknown Root Node. Use \"templateSetConfiguration\" as root Node");
        }

        // If we reach this point, the configuration version and root node has been validated.
        // Unmarshal with schema checks for checking the correctness and give the user more hints to
        // correct his failures
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        TemplateSetConfigurationVersion templateSetVersion = TemplateSetConfigurationVersion.getLatest();
        try (
            InputStream schemaStream = getClass()
                .getResourceAsStream("/schema/" + templateSetVersion + "/templateSetConfiguration.xsd");
            InputStream configInputStream = Files.newInputStream(templateSetFile)) {

          Schema schema = schemaFactory.newSchema(new StreamSource(schemaStream));
          unmarschaller.setSchema(schema);
          rootNode = unmarschaller.unmarshal(configInputStream);
          this.configNode = (TemplateSetConfiguration) rootNode;
          this.templateSetConfigurations.put(templateSetFile, (TemplateSetConfiguration) rootNode);
        }
      } catch (JAXBException e) {
        // try getting SAXParseException for better error handling and user support
        Throwable parseCause = ExceptionUtil.getCause(e, SAXParseException.class, UnmarshalException.class);
        String message = "";
        if (parseCause != null && parseCause.getMessage() != null) {
          message = parseCause.getMessage();
        }

        throw new InvalidConfigurationException(templateSetFile,
            "Could not parse template set configuration file:\n" + message, e);
      } catch (SAXException e) {
        // Should never occur. Programming error.
        throw new IllegalStateException(
            "Could not parse template set configuration schema. Please state this as a bug.");
      } catch (NumberFormatException e) {
        // The version number is currently the only xml value which will be parsed to a number data type
        // So provide help
        throw new InvalidConfigurationException(
            "Invalid version number defined. The version of the template set configuration should consist of 'major.minor' version.");
      } catch (IOException e) {
        throw new InvalidConfigurationException(templateSetFile, "Could not read template set configuration file.", e);
      } finally {
        Thread.currentThread().setContextClassLoader(orig);
      }
    }

  }

  /**
   * search for configuration files in the subfolder for adapted templates
   *
   * @param configRoot root directory of the configuration template-sets/adapted
   */
  private List<Path> loadTemplateSetFilesAdapted(Path configRoot) {

    List<Path> templateSetPaths = new ArrayList<>();

    List<Path> templateSetDirectories = new ArrayList<>();

    try (Stream<Path> files = Files.list(configRoot)) {
      files.forEach(path -> {
        if (Files.isDirectory(path)) {
          templateSetDirectories.add(path);
        }
      });
    } catch (IOException e) {
      throw new InvalidConfigurationException(configRoot, "Could not read configuration root directory.", e);
    }

    for (Path templateDirectory : templateSetDirectories) {
      Path templateSetFilePath = templateDirectory.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
          .resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

      addConfigRoot(templateSetFilePath, templateDirectory, templateSetPaths);
    }

    return templateSetPaths;
  }

  /**
   * search for configuration files in the subfolder for downloaded template jars
   *
   * @param configRoot root directory of the configuration template-sets/downloaded
   */
  private List<Path> loadTemplateSetFilesDownloaded(Path configRoot) {

    List<Path> templateSetPaths = new ArrayList<>();

    List<Path> templateJars = TemplatesJarUtil.getJarFiles(configRoot);
    if (templateJars != null) {
      for (Path jarPath : templateJars) {
        Path configurationPath = FileSystemUtil.createFileSystemDependentPath(jarPath.toUri());

        Path templateSetFilePath = configurationPath.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
            .getParent().resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

        addConfigRoot(templateSetFilePath, jarPath, templateSetPaths);
      }
    }

    return templateSetPaths;
  }

  /**
   * Loads all {@link Trigger}s of the static context into the local representation
   *
   * @return a {@link List} containing all the {@link Trigger}s
   */
  public Map<String, Trigger> loadTriggers() {

    Map<String, Trigger> triggers = Maps.newHashMap();
    for (Path contextFile : this.templateSetConfigurations.keySet()) {
      TemplateSetConfiguration contextConfiguration = this.templateSetConfigurations.get(contextFile);
      Path configLocation = this.configLocations.get(contextFile);
      boolean isJarFile = FileSystemUtil.isZipFile(configLocation.toUri());

      List<com.devonfw.cobigen.impl.config.entity.io.Trigger> triggerList = contextConfiguration.getTrigger();
      if (!triggerList.isEmpty()) {
        // context configuration in template sets consists of only one trigger
        com.devonfw.cobigen.impl.config.entity.io.Trigger trigger = triggerList.get(0);

        if (!this.triggerTemplateSetLocations.containsKey(trigger.getId()) || !isJarFile) {
          // prefer the adapted templates
          this.triggerTemplateSetLocations.put(trigger.getId(), configLocation);

          triggers.put(trigger.getId(),
              new Trigger(trigger.getId(), trigger.getType(), ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER,
                  Charset.forName(trigger.getInputCharset()), loadMatchers(trigger), loadContainerMatchers(trigger)));
        }
      }
    }
    return triggers;
  }

  /**
   * Loads all {@link ContainerMatcher}s of a given {@link com.devonfw.cobigen.impl.config.entity.io.Trigger}
   *
   * @param trigger {@link com.devonfw.cobigen.impl.config.entity.io.Trigger} to retrieve the {@link Matcher}s from
   * @return the {@link List} of {@link Matcher}s
   */
  protected List<ContainerMatcher> loadContainerMatchers(com.devonfw.cobigen.impl.config.entity.io.Trigger trigger) {

    List<ContainerMatcher> containerMatchers = Lists.newLinkedList();
    for (com.devonfw.cobigen.impl.config.entity.io.ContainerMatcher cm : trigger.getContainerMatcher()) {
      containerMatchers.add(new ContainerMatcher(cm.getType(), cm.getValue(), cm.isRetrieveObjectsRecursively()));
    }
    return containerMatchers;
  }

  /**
   * Loads all {@link Matcher}s of a given {@link com.devonfw.cobigen.impl.config.entity.io.Trigger}
   *
   * @param trigger {@link com.devonfw.cobigen.impl.config.entity.io.Trigger} to retrieve the {@link Matcher}s from
   * @return the {@link List} of {@link Matcher}s
   */
  protected List<Matcher> loadMatchers(com.devonfw.cobigen.impl.config.entity.io.Trigger trigger) {

    List<Matcher> matcher = new LinkedList<>();
    for (com.devonfw.cobigen.impl.config.entity.io.Matcher m : trigger.getMatcher()) {
      matcher.add(new Matcher(m.getType(), m.getValue(), loadVariableAssignments(m), m.getAccumulationType()));
    }
    return matcher;
  }

  /**
   * Loads all {@link VariableAssignment}s from a given {@link com.devonfw.cobigen.impl.config.entity.io.Matcher}
   *
   * @param matcher {@link com.devonfw.cobigen.impl.config.entity.io.Matcher} to retrieve the {@link VariableAssignment}
   *        from
   * @return the {@link List} of {@link Matcher}s
   */
  protected List<VariableAssignment> loadVariableAssignments(
      com.devonfw.cobigen.impl.config.entity.io.Matcher matcher) {

    List<VariableAssignment> variableAssignments = new LinkedList<>();
    for (com.devonfw.cobigen.impl.config.entity.io.VariableAssignment va : matcher.getVariableAssignment()) {
      variableAssignments.add(new VariableAssignment(va.getType(), va.getKey(), va.getValue(), va.isMandatory()));
    }
    return variableAssignments;
  }

  /**
   * Get the configuration location for a given trigger. Either a jar file or a folder
   *
   * @param triggerId the trigger id to search the config root for
   * @return the {@link Path} of the config location for a trigger
   */
  public Path getConfigLocationForTrigger(String triggerId) {

    return this.triggerTemplateSetLocations.get(triggerId);
  }

  /**
   * Adds the path to a template-set.xml file to the list of all config files. Also adds the path of the
   * template-set.xml file and its root directory to the configRoots map
   *
   * @param templateSetFilePath the {@link Path} to the template-set.xml file
   * @param configRootPath the {@link Path} containing the config root directory for a template-set.xml
   * @param templateSetPaths a list containing all paths to template-set.xml files
   */
  private void addConfigRoot(Path templateSetFilePath, Path configRootPath, List<Path> templateSetPaths) {

    if (Files.exists(templateSetFilePath)) {
      templateSetPaths.add(templateSetFilePath);
      this.configLocations.put(templateSetFilePath, configRootPath);
    }
  }

  /**
   * @return the list of the template set files
   */
  public List<Path> getTemplateSetFiles() {

    return this.templateSetFiles;
  }

  /**
   * =========================================================================================================
   * ***************************************TEMPLATE-SET PART STARTS HERE*************************************
   * =========================================================================================================
   */

  /**
   * Returns the configured template engine to be used
   *
   * @return the configured template engine to be used
   */
  public String getTemplateEngine() {

    return this.configNode.getTemplateEngine();

  }

  /**
   * Loads all templates of the static configuration into the local representation
   *
   * @param trigger {@link Trigger} for which the templates should be loaded
   * @return the mapping of template names to the corresponding {@link Template}
   * @throws UnknownContextVariableException if the destination path contains an undefined context variable
   * @throws UnknownExpressionException if there is an unknown variable modifier
   * @throws InvalidConfigurationException if there are multiple templates with the same name
   */
  public Map<String, Template> loadTemplates(Trigger trigger)
      throws UnknownExpressionException, UnknownContextVariableException, InvalidConfigurationException {

    Map<String, Template> templates = new HashMap<>();
    Templates templatesNode = this.configNode.getTemplates();

    if (templatesNode != null) {
      for (com.devonfw.cobigen.impl.config.entity.io.Template t : templatesNode.getTemplate()) {
        if (templates.get(t.getName()) != null) {
          throw new InvalidConfigurationException(this.configFilePath.toUri().toString(),
              "Multiple template definitions found for ref='" + t.getName() + "'");
        }
        TemplatePath child = this.rootTemplateFolder.navigate(t.getTemplateFile());

        if ((child == null) || (child.isFolder())) {
          throw new InvalidConfigurationException(this.configFilePath.toUri().toString(),
              "no template file found for '" + t.getTemplateFile() + "'");
        }
        Template template = createTemplate((TemplateFile) child, t.getName(), t.getDestinationPath(),
            t.getMergeStrategy(), t.getTargetCharset(), null);
        templates.put(t.getName(), template);
      }
    }

    TemplateScans templateScans = this.configNode.getTemplateScans();
    if (templateScans != null) {
      List<TemplateScan> scans = templateScans.getTemplateScan();
      if (scans != null) {
        for (TemplateScan scan : scans) {
          scanTemplates(scan, templates, trigger);
        }
      }
    }

    // override existing templates with extension definitions
    Set<String> observedExtensionNames = Sets.newHashSet();
    if (templatesNode != null && templatesNode.getTemplateExtension() != null) {
      for (TemplateExtension ext : this.configNode.getTemplates().getTemplateExtension()) {
        // detection of duplicate templateExtensions
        if (observedExtensionNames.contains(ext.getRef())) {
          throw new InvalidConfigurationException(
              "Two templateExtensions declared for ref='" + ext.getRef() + "'. Don't know what to do.");
        }
        observedExtensionNames.add(ext.getRef());

        // overriding properties if defined
        if (templates.containsKey(ext.getRef())) {
          Template template = templates.get(ext.getRef());
          if (ext.getDestinationPath() != null) {
            template.setUnresolvedTargetPath(ext.getDestinationPath());
          }
          if (ext.getMergeStrategy() != null) {
            template.setMergeStrategy(ext.getMergeStrategy());
          }
          if (ext.getTargetCharset() != null) {
            template.setTargetCharset(ext.getTargetCharset());
          }
        } else {
          throw new InvalidConfigurationException(
              "The templateExtension with ref='" + ext.getRef() + "' does not reference any template!");
        }
      }
    }

    return templates;
  }

  /**
   * Scans the templates specified by the given {@link TemplateScan} and adds them to the given <code>templates</code>
   * {@link Map}.
   *
   * @param scan is the {@link TemplateScan} configuration.
   * @param templates is the {@link Map} where to add the templates.
   * @param trigger the templates are from
   */
  private void scanTemplates(TemplateScan scan, Map<String, Template> templates, Trigger trigger) {

    String templatePath = scan.getTemplatePath();
    TemplatePath templateFolder = this.rootTemplateFolder.navigate(templatePath);

    if ((templateFolder == null) || templateFolder.isFile()) {
      throw new InvalidConfigurationException(this.configFilePath.toUri().toString(), "The templatePath '"
          + templatePath + "' of templateScan with name '" + scan.getName() + "' does not describe a directory.");
    }

    if (scan.getName() != null) {
      if (this.templateScanTemplates.containsKey(scan.getName())) {
        throw new InvalidConfigurationException(this.configFilePath.toUri().toString(),
            "Two templateScan nodes have been defined with the same @name by mistake.");
      } else {
        this.templateScanTemplates.put(scan.getName(), new ArrayList<String>());
      }
    }

    scanTemplates((TemplateFolder) templateFolder, "", scan, templates, trigger, Sets.<String> newHashSet());
  }

  /**
   * Recursively scans the templates specified by the given {@link TemplateScan} and adds them to the given
   * <code>templates</code> {@link Map}.
   *
   * @param templateFolder the {@link TemplateFolder} pointing to the current directory to scan.
   * @param currentPath the current path relative to the top-level directory where we started the scan.
   * @param scan is the {@link TemplateScan} configuration.
   * @param templates is the {@link Map} where to add the templates.
   * @param trigger the templates are from
   * @param observedTemplateNames observed template name during template scan. Needed for conflict detection
   */
  private void scanTemplates(TemplateFolder templateFolder, String currentPath, TemplateScan scan,
      Map<String, Template> templates, Trigger trigger, HashSet<String> observedTemplateNames) {

    String currentPathWithSlash = currentPath;
    if (!currentPathWithSlash.isEmpty()) {
      currentPathWithSlash = currentPathWithSlash + "/";
    }

    for (TemplatePath child : templateFolder.getChildren()) {

      if (child.isFolder()) {
        scanTemplates((TemplateFolder) child, currentPathWithSlash + child.getFileName(), scan, templates, trigger,
            observedTemplateNames);
      } else {
        String templateFileName = child.getFileName();
        if (StringUtils.isEmpty(currentPath) && templateFileName.equals("template-set.xml")) {
          continue;
        }
        String templateNameWithoutExtension = stripTemplateFileending(templateFileName);

        TextTemplateEngine templateEngine = TemplateEngineRegistry.getEngine(getTemplateEngine());
        if (!StringUtils.isEmpty(templateEngine.getTemplateFileEnding())
            && templateFileName.endsWith(templateEngine.getTemplateFileEnding())) {
          templateNameWithoutExtension = templateFileName.substring(0,
              templateFileName.length() - templateEngine.getTemplateFileEnding().length());
        }
        String templateName = (scan.getTemplateNamePrefix() != null ? scan.getTemplateNamePrefix() : "")
            + templateNameWithoutExtension;
        if (observedTemplateNames.contains(templateName)) {
          throw new InvalidConfigurationException(
              "TemplateScan has detected two files with the same file name (" + child + ") and thus with the same "
                  + "template name. Continuing would result in an indeterministic behavior.\n"
                  + "For now, multiple files with the same name are not supported to be automatically "
                  + "configured with templateScans.");
        }
        observedTemplateNames.add(templateName);
        if (!templates.containsKey(templateName)) {
          String destinationPath = "";
          if (!StringUtils.isEmpty(scan.getDestinationPath())) {
            destinationPath = scan.getDestinationPath() + "/";
          }
          destinationPath += currentPathWithSlash + templateNameWithoutExtension;

          String mergeStratgey = scan.getMergeStrategy();
          Template template = createTemplate((TemplateFile) child, templateName, destinationPath, mergeStratgey,
              scan.getTargetCharset(), scan.getTemplatePath());
          templates.put(templateName, template);

          if (this.templateScanTemplates.get(scan.getName()) != null) {
            this.templateScanTemplates.get(scan.getName()).add(templateName);
          }
        }
      }
    }
  }

  /**
   * Strips the file ending provided by the template engine from the file name.
   *
   * @param templateFileName file name of the template
   * @return the file name without the template file ending
   */
  private String stripTemplateFileending(String templateFileName) {

    String templateNameWithoutExtension = templateFileName;
    TextTemplateEngine templateEngine = TemplateEngineRegistry.getEngine(getTemplateEngine());
    if (!StringUtils.isEmpty(templateEngine.getTemplateFileEnding())
        && templateFileName.endsWith(templateEngine.getTemplateFileEnding())) {
      templateNameWithoutExtension = templateFileName.substring(0,
          templateFileName.length() - templateEngine.getTemplateFileEnding().length());
    }
    return templateNameWithoutExtension;
  }

  /**
   * @param templateFile the {@link TemplateFile}.
   * @param templateName the {@link Template#getName() template name} (ID).
   * @param unresolvedTemplatePath the {@link Template#getUnresolvedTemplatePath() unresolved template path}.
   * @param mergeStratgey the {@link Template#getMergeStrategy() merge strategy}.
   * @param outputCharset the {@link Template#getTargetCharset() target charset}.
   * @param scanSourcePath {@link TemplateScan#getTemplatePath() root path} of the {@link TemplateScan}
   * @return the new template instance.
   */
  private Template createTemplate(TemplateFile templateFile, String templateName, String unresolvedTemplatePath,
      String mergeStratgey, String outputCharset, String scanSourcePath) {

    String unresolvedDestinationPath = unresolvedTemplatePath;
    TemplateFolder templateFolder = templateFile.getParent();
    String relocate = templateFolder.getVariables().get(PROPERTY_RELOCATE);
    if (relocate != null) {
      if (scanSourcePath != null) {
        // The relative template path has to be specifically parsed to string and back to a path so
        // the templateFile and scanSourcePath are using the same file system. More info can be found
        // at https://github.com/devonfw/cobigen/issues/715
        String templateFilePath = templateFile.getRootRelativePath().toString();
        Path destinationPath = Paths.get(scanSourcePath).relativize(Paths.get(templateFilePath));
        unresolvedDestinationPath = relocate.replace(VARIABLE_CWD, destinationPath.toString().replace("\\", "/"));
      }
    }
    return new Template(templateFile, templateName, stripTemplateFileending(unresolvedDestinationPath),
        unresolvedTemplatePath, mergeStratgey, outputCharset);
  }
}
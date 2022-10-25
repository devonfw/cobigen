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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.devonfw.cobigen.impl.config.ConfigurationHolder;
import com.devonfw.cobigen.impl.config.TemplateSetConfiguration;
import com.devonfw.cobigen.impl.config.constant.MavenMetadata;
import com.devonfw.cobigen.impl.config.constant.TemplateSetConfigurationVersion;
import com.devonfw.cobigen.impl.config.entity.ContainerMatcher;
import com.devonfw.cobigen.impl.config.entity.Increment;
import com.devonfw.cobigen.impl.config.entity.Matcher;
import com.devonfw.cobigen.impl.config.entity.Template;
import com.devonfw.cobigen.impl.config.entity.TemplateFile;
import com.devonfw.cobigen.impl.config.entity.TemplateFolder;
import com.devonfw.cobigen.impl.config.entity.TemplatePath;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.VariableAssignment;
import com.devonfw.cobigen.impl.config.entity.io.IncrementRef;
import com.devonfw.cobigen.impl.config.entity.io.Increments;
import com.devonfw.cobigen.impl.config.entity.io.TemplateExtension;
import com.devonfw.cobigen.impl.config.entity.io.TemplateRef;
import com.devonfw.cobigen.impl.config.entity.io.TemplateScan;
import com.devonfw.cobigen.impl.config.entity.io.TemplateScanRef;
import com.devonfw.cobigen.impl.config.entity.io.TemplateScans;
import com.devonfw.cobigen.impl.config.entity.io.Templates;
import com.devonfw.cobigen.impl.config.reader.interfaces.ContextConfigurationInterface;
import com.devonfw.cobigen.impl.config.reader.interfaces.TemplatesConfigurationInterface;
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
public class TemplateSetConfigurationReader implements ContextConfigurationInterface, TemplatesConfigurationInterface {

  /** Path of the template set configuration file */
  public Path templateSetFile;

  /** Paths of the configuration location for a template-set.xml file */
  private Path configLocation;

  /** Map with the paths of the template set location for a trigger */
  private Map<String, Path> triggerTemplateSetLocations = new HashMap<>();

  /** JAXB root node of the configuration */
  private com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration templateSetConfiguration;

  /** XML Node 'template-set' of the template-set.xml files */
  protected TemplateSetConfiguration templateSetConfigurationDecorator;

  /** Root of the configuration */
  private Path configRoot;

  /** The {@link Properties#getProperty(String) name of the property} to relocate a template target folder. */
  private static final String PROPERTY_RELOCATE = "relocate";

  /** The syntax for the variable pointing to the current working directory (CWD) of a template. */
  private static final String VARIABLE_CWD = "${cwd}";

  /** {@link JXPathContext} for the configNode */
  private JXPathContext xPathContext;

  /** Cache to find all templates by name for each template scan */
  private Map<String, List<String>> templateScanTemplates = Maps.newHashMap();

  /** The top-level folder where the templates are located. */
  private TemplateFolder rootTemplateFolder;

  /** The {@link ConfigurationHolder} used for reading templates folder **/
  private ConfigurationHolder configurationHolder;

  // TODO: Use dependency injection here instead of the new operator
  private final TemplateSetConfigurationManager templateSetConfigurationManager = new TemplateSetConfigurationManager();

  /**
   * The constructor.
   *
   * @param configRoot Root of the configuration
   * @param templateSetConfiguration Wrapped configuration that's being read
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public TemplateSetConfigurationReader(Path configRoot, TemplateSetConfiguration templateSetConfiguration)
      throws InvalidConfigurationException {

    if (configRoot == null)
      throw new IllegalArgumentException("Configuraion path cannot be null.");

    this.templateSetConfigurationDecorator = templateSetConfiguration;
    Path templateSetsDownloaded = configRoot.resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    Path templateSetsAdapted = configRoot.resolve(ConfigurationConstants.ADAPTED_FOLDER);

    if (!Files.exists(templateSetsDownloaded) && !Files.exists(templateSetsAdapted)) {
      throw new InvalidConfigurationException(configRoot,
          "Could not find a folder in which to search for the template-set configuration file.");
    } else {
      if (Files.exists(templateSetsAdapted)) {
        templateSetConfiguration.templateSetFiles
            .addAll(this.templateSetConfigurationManager.loadTemplateSetFilesAdapted(templateSetsAdapted));
        this.configRoot = templateSetsAdapted;

      }

      else if (Files.exists(templateSetsDownloaded)) {
        templateSetConfiguration.templateSetFiles
            .addAll(this.templateSetConfigurationManager.loadTemplateSetFilesDownloaded(templateSetsDownloaded));
        this.configRoot = configRoot.resolve("template-set.jar");

      }

      if (templateSetConfiguration.templateSetFiles.isEmpty()) {
        throw new InvalidConfigurationException(configRoot,
            "Could not find any template-set configuration file in the given folder.");
      }
    }

  }

  /**
   *
   */
  @Override
  public void readConfiguration() {

    this.configLocation = this.templateSetFile.getParent();

    Path templateLocation;
    if (!FileSystemUtil.isZipFile(this.configRoot.toUri())) {
      this.configRoot = this.templateSetFile.getParent().resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);
      templateLocation = this.configRoot;
      this.rootTemplateFolder = TemplateFolder.create(templateLocation.getParent().resolve("templates"));
    }

    if (FileSystemUtil.isZipFile(this.configRoot.toUri())) {
      templateLocation = FileSystemUtil.createFileSystemDependentPath(this.configRoot.toUri());
      this.rootTemplateFolder = TemplateFolder
          .create(templateLocation.resolve(this.configLocation.resolve("templates")));
    }

    if (!Files.exists(this.templateSetFile)) {
      throw new InvalidConfigurationException(this.templateSetFile, "Could not find templates set configuration file.");
    } else {
      // Change this line to templatesLocation = rootTemplatePath if additional "templates" folder is removed
      // templateLocation = this.configRoot.resolve(this.configLocation.toUri() + "/templates");
    }

    // workaround to make JAXB work in OSGi context by
    // https://github.com/ControlSystemStudio/cs-studio/issues/2530#issuecomment-450991188
    final ClassLoader orig = Thread.currentThread().getContextClassLoader();
    if (JvmUtil.isRunningJava9OrLater()) {
      Thread.currentThread().setContextClassLoader(JAXBContext.class.getClassLoader());
    }

    try (InputStream in = Files.newInputStream(this.templateSetFile)) {
      Unmarshaller unmarschaller = JAXBContext
          .newInstance(com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration.class).createUnmarshaller();

      // Unmarshal without schema checks for getting the version attribute of the root node.
      // This is necessary to provide an automatic upgrade client later on
      com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration rootNode = (com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration) unmarschaller
          .unmarshal(in);
      BigDecimal configVersion;
      if (rootNode != null) {
        configVersion = rootNode.getVersion();
        if (configVersion == null) {
          throw new InvalidConfigurationException(this.templateSetFile,
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
        throw new InvalidConfigurationException(this.templateSetFile,
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
          InputStream configInputStream = Files.newInputStream(this.templateSetFile)) {

        Schema schema = schemaFactory.newSchema(new StreamSource(schemaStream));
        unmarschaller.setSchema(schema);
        rootNode = (com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration) unmarschaller
            .unmarshal(configInputStream);
        this.templateSetConfiguration = rootNode;
      }
    } catch (JAXBException e) {
      // try getting SAXParseException for better error handling and user support
      Throwable parseCause = ExceptionUtil.getCause(e, SAXParseException.class, UnmarshalException.class);
      String message = "";
      if (parseCause != null && parseCause.getMessage() != null) {
        message = parseCause.getMessage();
      }

      throw new InvalidConfigurationException(this.templateSetFile,
          "Could not parse template set configuration file:\n" + message, e);
    } catch (SAXException e) {
      // Should never occur. Programming error.
      throw new IllegalStateException("Could not parse template set configuration schema. Please state this as a bug.");
    } catch (NumberFormatException e) {
      // The version number is currently the only xml value which will be parsed to a number data type
      // So provide help
      throw new InvalidConfigurationException(
          "Invalid version number defined. The version of the template set configuration should consist of 'major.minor' version.");
    } catch (IOException e) {
      throw new InvalidConfigurationException(this.templateSetFile, "Could not read template set configuration file.",
          e);
    } finally {
      Thread.currentThread().setContextClassLoader(orig);
    }

  }

  /**
   * Loads all {@link Trigger}s of the static context into the local representation
   *
   * @return a {@link List} containing all the {@link Trigger}s
   */
  @Override
  public Map<String, Trigger> loadTriggers() {

    Map<String, Trigger> triggers = Maps.newHashMap();
    boolean isJarFile = FileSystemUtil.isZipFile(this.configRoot.toUri());

    List<com.devonfw.cobigen.impl.config.entity.io.Trigger> triggerList = this.templateSetConfiguration.getTrigger();
    if (!triggerList.isEmpty()) {
      // context configuration in template sets consists of only one trigger
      com.devonfw.cobigen.impl.config.entity.io.Trigger trigger = triggerList.get(0);
      if (!this.triggerTemplateSetLocations.containsKey(trigger.getId()) || !isJarFile) {
        // prefer the adapted templates
        this.triggerTemplateSetLocations.put(trigger.getId(), this.configLocation);
        triggers.put(trigger.getId(),
            new Trigger(trigger.getId(), trigger.getType(), ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER,
                Charset.forName(trigger.getInputCharset()), loadMatchers(trigger), loadContainerMatchers(trigger)));
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
      this.configLocation = configRootPath;
    }
  }

  /**
   * =========================================================================================================
   * ****************************************TEMPLATES PART STARTS HERE***************************************
   * =========================================================================================================
   */

  /**
   * Returns the configured template engine to be used
   *
   * @return the configured template engine to be used
   */
  @Override
  public String getTemplateEngine() {

    return this.templateSetConfiguration.getTemplateEngine();

  }

  /**
   * Loads all templates of the static configuration into the local representation
   *
   * @return the mapping of template names to the corresponding {@link Template}
   * @throws UnknownContextVariableException if the destination path contains an undefined context variable
   * @throws UnknownExpressionException if there is an unknown variable modifier
   * @throws InvalidConfigurationException if there are multiple templates with the same name
   *
   */
  @Override
  public Map<String, Template> loadTemplates()
      throws UnknownExpressionException, UnknownContextVariableException, InvalidConfigurationException {

    Map<String, Template> templates = new HashMap<>();
    Templates templatesNode = this.templateSetConfiguration.getTemplates();

    if (templatesNode != null) {
      for (com.devonfw.cobigen.impl.config.entity.io.Template t : templatesNode.getTemplate()) {
        if (templates.get(t.getName()) != null) {
          throw new InvalidConfigurationException(this.templateSetFile.toUri().toString(),
              "Multiple template definitions found for ref='" + t.getName() + "'");
        }
        TemplatePath child = this.rootTemplateFolder.navigate(t.getTemplateFile());

        if ((child == null) || (child.isFolder())) {
          throw new InvalidConfigurationException(this.templateSetFile.toUri().toString(),
              "no template file found for '" + t.getTemplateFile() + "'");
        }
        Template template = createTemplate((TemplateFile) child, t.getName(), t.getDestinationPath(),
            t.getMergeStrategy(), t.getTargetCharset(), null);
        templates.put(t.getName(), template);
      }
    }

    TemplateScans templateScans = this.templateSetConfiguration.getTemplateScans();
    if (templateScans != null) {
      List<TemplateScan> scans = templateScans.getTemplateScan();
      if (scans != null) {
        for (TemplateScan scan : scans) {
          scanTemplates(scan, templates);
        }
      }
    }

    // override existing templates with extension definitions
    Set<String> observedExtensionNames = Sets.newHashSet();
    if (templatesNode != null && templatesNode.getTemplateExtension() != null) {
      for (TemplateExtension ext : this.templateSetConfiguration.getTemplates().getTemplateExtension()) {
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
  private void scanTemplates(TemplateScan scan, Map<String, Template> templates) {

    String templatePath = scan.getTemplatePath();
    TemplatePath templateFolder = this.rootTemplateFolder.navigate(templatePath);

    if ((templateFolder == null) || templateFolder.isFile()) {
      throw new InvalidConfigurationException(this.templateSetFile.toUri().toString(), "The templatePath '"
          + templatePath + "' of templateScan with name '" + scan.getName() + "' does not describe a directory.");
    }

    if (scan.getName() != null) {
      if (this.templateScanTemplates.containsKey(scan.getName())) {
        throw new InvalidConfigurationException(this.templateSetFile.toUri().toString(),
            "Two templateScan nodes have been defined with the same @name by mistake.");
      } else {
        this.templateScanTemplates.put(scan.getName(), new ArrayList<String>());
      }
    }

    scanTemplates((TemplateFolder) templateFolder, "", scan, templates, Sets.<String> newHashSet());
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
      Map<String, Template> templates, HashSet<String> observedTemplateNames) {

    String currentPathWithSlash = currentPath;
    if (!currentPathWithSlash.isEmpty()) {
      currentPathWithSlash = currentPathWithSlash + "/";
    }

    for (TemplatePath child : templateFolder.getChildren()) {

      if (child.isFolder()) {
        scanTemplates((TemplateFolder) child, currentPathWithSlash + child.getFileName(), scan, templates,
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

  /**
   * =========================================================================================================
   * ****************************************INCREMENTS PART STARTS HERE**************************************
   * =========================================================================================================
   */

  /**
   * Loads all increments of the static configuration into the local representation.
   *
   * @return the mapping of increment names to the corresponding {@link Increment}
   * @param templates {@link Map} of all templates (see {@link TemplatesConfigurationReader#loadTemplates(Trigger)}
   * @param trigger {@link Trigger} for which the templates should be loaded
   * @throws InvalidConfigurationException if there is an invalid ref attribute
   */
  @Override
  public Map<String, Increment> loadIncrements(Map<String, Template> templates, Trigger trigger)
      throws InvalidConfigurationException {

    Map<String, Increment> increments = new HashMap<>();
    Increments incrementsNode = this.templateSetConfiguration.getIncrements();
    if (incrementsNode != null) {
      // Add first all increments informally be able to resolve recursive increment references
      for (com.devonfw.cobigen.impl.config.entity.io.Increment source : incrementsNode.getIncrement()) {
        if (!increments.containsKey(source.getName())) {
          increments.put(source.getName(), new Increment(source.getName(), source.getDescription(), trigger));
        } else {
          throw new InvalidConfigurationException(this.templateSetFile.toUri().toString(),
              "Duplicate increment found with name='" + source.getName() + "'.");
        }
      }
      // Collect templates
      for (com.devonfw.cobigen.impl.config.entity.io.Increment p : this.templateSetConfiguration.getIncrements()
          .getIncrement()) {
        Increment target = increments.get(p.getName());
        addAllTemplatesRecursively(target, p, templates, increments);
      }
    }
    return increments;
  }

  /**
   * Loads an specific increment of the static configuration into the local representation. The return object must be a
   * map because maybe this increment references other increments
   *
   * @return the mapping of increment names to the corresponding {@link Increment}
   * @param templates {@link Map} of all templates (see {@link TemplatesConfigurationReader#loadTemplates(Trigger)}
   * @param trigger {@link Trigger} for which the templates should be loaded
   * @param incrementName the increment to search
   * @throws InvalidConfigurationException if there is an invalid ref attribute
   */
  @Override
  public Map<String, Increment> loadSpecificIncrement(Map<String, Template> templates, Trigger trigger,
      String incrementName) throws InvalidConfigurationException {

    Map<String, Increment> increments = new HashMap<>();
    Increments incrementsNode = this.templateSetConfiguration.getIncrements();
    if (incrementsNode != null) {
      // We only add the specific increment we want
      com.devonfw.cobigen.impl.config.entity.io.Increment source = getSpecificIncrement(incrementsNode.getIncrement(),
          incrementName);
      if (source == null) {
        throw new InvalidConfigurationException(this.templateSetFile.toUri().toString(),
            "No increment found with name='" + incrementName + "' on the external templates.xml folder.");
      }

      increments.put(source.getName(), new Increment(source.getName(), source.getDescription(), trigger));

      // Collect templates for our specific increment
      Increment target = increments.get(source.getName());
      addAllTemplatesRecursively(target, source, templates, increments);
    }
    return increments;
  }

  /**
   * Adds all templates defined within the increment and sub increments recursively.
   *
   * @param rootIncrement the {@link Increment} on which the templates should be added
   * @param current the source {@link com.devonfw.cobigen.impl.config.entity.io.Increment} from which to retrieve the
   *        data
   * @param templates {@link Map} of all templates (see {@link TemplatesConfigurationReader#loadTemplates(Trigger)}
   * @param increments {@link Map} of all retrieved increments
   * @throws InvalidConfigurationException if there is an invalid ref attribute
   */
  private void addAllTemplatesRecursively(Increment rootIncrement,
      com.devonfw.cobigen.impl.config.entity.io.Increment current, Map<String, Template> templates,
      Map<String, Increment> increments) throws InvalidConfigurationException {

    for (TemplateRef ref : current.getTemplateRefOrIncrementRefOrTemplateScanRef().stream()
        .filter(e -> e instanceof TemplateRef).map(e -> (TemplateRef) e).collect(Collectors.toList())) {
      Template temp = templates.get(ref.getRef());
      if (temp == null) {
        if (isExternalRef(ref.getRef())) {
          rootIncrement.addTemplate(loadExternalTemplate(ref));
        } else {
          throw new InvalidConfigurationException(this.templateSetFile.toUri().toString(),
              "No template found for ref='" + ref.getRef() + "'!");
        }
      } else {
        rootIncrement.addTemplate(temp);
      }
    }

    for (IncrementRef ref : current.getTemplateRefOrIncrementRefOrTemplateScanRef().stream()
        .filter(e -> e instanceof IncrementRef).map(e -> (IncrementRef) e).collect(Collectors.toList())) {
      Increment parentPkg = increments.get(current.getName());
      Increment childPkg = increments.get(ref.getRef());

      if (childPkg == null) {

        // We try to find the increment inside our templates.xml file
        Increments incrementsNode = this.templateSetConfiguration.getIncrements();
        com.devonfw.cobigen.impl.config.entity.io.Increment source = null;
        if (incrementsNode != null) {
          // We only add the specific increment we want
          source = getSpecificIncrement(incrementsNode.getIncrement(), ref.getRef());
          if (source != null) {
            addAllTemplatesRecursively(rootIncrement, source, templates, increments);
          }
          // We have not found the increment inside our templates.xml file, now let's see if this
          // incrementRef contains "::". That would mean we have to search on another folder.
          else if (isExternalRef(ref.getRef())) {
            parentPkg.addIncrementDependency(loadExternalIncrement(ref));
          } else {
            throw new InvalidConfigurationException(this.templateSetFile.toUri().toString(),
                "No increment found for ref='" + ref.getRef() + "'!");
          }
        }

      } else {
        parentPkg.addIncrementDependency(childPkg);

        com.devonfw.cobigen.impl.config.entity.io.Increment pkg = getIncrementDeclaration(ref);
        addAllTemplatesRecursively(rootIncrement, pkg, templates, increments);
      }
    }

    for (TemplateScanRef ref : current.getTemplateRefOrIncrementRefOrTemplateScanRef().stream()
        .filter(e -> e instanceof TemplateScanRef).map(e -> (TemplateScanRef) e).collect(Collectors.toList())) {
      List<String> scannedTemplateNames = this.templateScanTemplates.get(ref.getRef());
      if (scannedTemplateNames == null) {
        throw new InvalidConfigurationException(this.templateSetFile.toUri().toString(),
            "No templateScan found for ref='" + ref.getRef() + "'!");
      } else {
        for (String scannedTemplateName : scannedTemplateNames) {
          rootIncrement.addTemplate(templates.get(scannedTemplateName));
        }
      }
    }
  }

  /**
   * Tries to load an external template, returning the reference template
   *
   * @param ref The reference to the template
   * @return the referenced template
   */
  private Template loadExternalTemplate(TemplateRef ref) {

    String[] split = splitExternalRef(ref.getRef());
    String refTrigger = split[0];
    String refTemplate = split[1];

    com.devonfw.cobigen.impl.config.TemplatesConfiguration externalTemplatesConfiguration = loadExternalConfig(
        refTrigger);

    Template template = externalTemplatesConfiguration.getTemplate(refTemplate);

    if (template == null) {
      throw new InvalidConfigurationException("No Template found for ref=" + ref.getRef());
    }

    return template;
  }

  /**
   * Tries to load an external increment. It loads the trigger of the external increment and all its increments for
   * finding the needed one
   *
   * @param ref incrementRef to load and store on the root increment
   * @return the referenced child increment
   */
  private Increment loadExternalIncrement(IncrementRef ref) {

    Increment childPkg;
    String[] split = splitExternalRef(ref.getRef());
    String refTrigger = split[0];
    String refIncrement = split[1];

    com.devonfw.cobigen.impl.config.TemplatesConfiguration externalTemplatesConfiguration = loadExternalConfig(
        refTrigger);

    Map<String, Increment> externalIncrements = externalTemplatesConfiguration.getIncrements();

    childPkg = externalIncrements.get(refIncrement);

    if (childPkg == null) {
      throw new InvalidConfigurationException("No Increment found for ref=" + ref.getRef());
    }

    return childPkg;
  }

  /**
   * Returns the TemplatesConfiguration file corresponding to the given trigger
   *
   * @param refTrigger The trigger by which the TemplatesConfiguration shoul be searched
   * @return The TemplatesConfiguration corresponding to the trigger
   */
  private com.devonfw.cobigen.impl.config.TemplatesConfiguration loadExternalConfig(String refTrigger) {

    Trigger extTrigger = getExternalTrigger(refTrigger);
    return this.configurationHolder.readTemplatesConfiguration(extTrigger);
  }

  /**
   * Returns the {@link com.devonfw.cobigen.impl.config.entity.io.Increment} for the given {@link IncrementRef}
   *
   * @param source {@link IncrementRef}
   * @return the referenced {@link com.devonfw.cobigen.impl.config.entity.io.Increment}
   * @throws InvalidConfigurationException if there is an invalid increment ref
   */
  private com.devonfw.cobigen.impl.config.entity.io.Increment getIncrementDeclaration(IncrementRef source)
      throws InvalidConfigurationException {

    if (this.xPathContext == null) {
      this.xPathContext = JXPathContext.newContext(this.templateSetConfiguration);
    }

    // does not work any longer as name is not a NCName type any more
    // xPathContext.iterate("//increment[@name='" + source.getRef() + "']");
    Iterator<com.devonfw.cobigen.impl.config.entity.io.Increment> allNamedIncrementsIt = this.xPathContext
        .iterate("//increment[@name]");

    String incrementToSearch = source.getRef();
    // Check whether we have an external incrementRef
    if (isExternalRef(incrementToSearch)) {
      String[] splitted = splitExternalRef(source.getRef());
      incrementToSearch = splitted[1];
    }

    com.devonfw.cobigen.impl.config.entity.io.Increment result = null;
    while (allNamedIncrementsIt.hasNext()) {
      com.devonfw.cobigen.impl.config.entity.io.Increment currentIncrement = allNamedIncrementsIt.next();
      if (incrementToSearch.equals(currentIncrement.getName())) {
        if (result == null) {
          result = currentIncrement;
        } else {
          throw new InvalidConfigurationException(this.templateSetFile.toUri().toString(),
              "Multiple increment definitions found for ref='" + source.getRef() + "'");
        }
      }
    }

    if (result != null) {
      return result;
    } else {
      throw new InvalidConfigurationException(this.templateSetFile.toUri().toString(),
          "No increment definition found for ref='" + source.getRef() + "'");
    }
  }

  /**
   * External ref's syntax is "triggerName::incrementName" . Therefore, this method splits the ref using "::" as the
   * delimiter so that we can get both strings separately.
   *
   * @param ref the increment ref to split
   * @return an string array that will contain 2 elements
   */
  private String[] splitExternalRef(String ref) {

    String[] split = ref.split(ConfigurationConstants.REFERENCE_DELIMITER);
    if (split.length != 2) {
      throw new InvalidConfigurationException(this.templateSetFile.toUri().toString(),
          "Invalid external ref for ref='" + ref + "', it should be 'nameOfFolder::nameOfIncrement'!");
    }
    return split;
  }

  /**
   * Checks whether this increment ref is an external increment ref
   *
   * @param ref the increment ref to check
   * @return true if it is an external IncrementRef
   */
  private boolean isExternalRef(String ref) {

    return ref.contains(ConfigurationConstants.REFERENCE_DELIMITER);
  }

  /**
   * Tries to read the context.xml file for finding and returning an external trigger
   *
   * @param triggerToSearch string containing the name of the trigger to search
   * @return the found external trigger
   */
  private Trigger getExternalTrigger(String triggerToSearch) {

    ContextConfigurationReader contextConfigurationReader = new ContextConfigurationReader(
        this.configurationHolder.readContextConfiguration().getConfigurationPath());
    Map<String, Trigger> triggers = contextConfigurationReader.loadTriggers();
    Trigger trig = triggers.get(triggerToSearch);
    if (trig == null) {
      throw new InvalidConfigurationException(this.templateSetFile.toUri().toString(),
          "Invalid external ref, no trigger '" + triggerToSearch + "' was found on your context.xml!");
    }
    return trig;
  }

  /**
   * Tries to find an increment on a list of increments and return it
   *
   * @param increment list of increments
   * @param ref name of the increment to get
   * @return Increment if it was found, null if no increment with that name was found
   */

  @Override
  public com.devonfw.cobigen.impl.config.entity.io.Increment getSpecificIncrement(
      List<com.devonfw.cobigen.impl.config.entity.io.Increment> increment, String ref) {

    for (com.devonfw.cobigen.impl.config.entity.io.Increment inc : increment) {
      if (inc.getName().equals(ref)) {
        return inc;
      }
    }
    return null;
  }

}

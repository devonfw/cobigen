package com.devonfw.cobigen.impl.config.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.api.util.ExceptionUtil;
import com.devonfw.cobigen.api.util.JvmUtil;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.impl.config.ConfigurationHolder;
import com.devonfw.cobigen.impl.config.constant.MavenMetadata;
import com.devonfw.cobigen.impl.config.constant.TemplateSetConfigurationVersion;
import com.devonfw.cobigen.impl.config.entity.TemplateFolder;
import com.devonfw.cobigen.impl.config.entity.io.TemplatesConfiguration;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator.Type;
import com.devonfw.cobigen.impl.util.FileSystemUtil;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;

/**
 * The {@link TemplateSetConfigurationReader} combines everything from the {@link TemplatesConfigurationReader} and
 * {@link ContextConfigurationReader}
 */
public class TemplateSetConfigurationReader {

  /** Path of the template set configuration file */
  private Path templateSetFile;

  /** Paths of the configuration location for a template-set.xml file */
  private Path configLocation;

  /** Root of the configuration */
  private Path configRoot;

  /** The static representation of the TemplateSetConfiguration */
  private com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration templateSetConfiguration;

  /** List with the paths of the configuration locations for the template-set.xml files */
  private Map<Path, Path> configLocations = new HashMap<>();

  /**
   * The {@link ConfigurationHolder}
   */
  protected ConfigurationHolder configurationHolder;

  /**
   * The {@link com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration}
   */
  protected com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration contextConfiguration;

  /**
   * @return contextConfiguration
   */
  public com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration getContextConfiguration() {

    return this.contextConfiguration;
  }

  /**
   * @return templatesConfiguration
   */
  public TemplatesConfiguration getTemplatesConfiguration() {

    return this.templatesConfiguration;
  }

  /**
   * The {@link TemplatesConfiguration} to initialize
   */
  protected TemplatesConfiguration templatesConfiguration;

  /** The top-level folder where the templates are located. */
  private TemplateFolder rootTemplateFolder;

  /** The list of template set configuration paths */
  private List<Path> templateSetConfigurationPaths;

  // TODO: Use dependency injection here instead of the new operator
  private final TemplateSetConfigurationManager templateSetConfigurationManager = new TemplateSetConfigurationManager();

  /**
   * The constructor.
   *
   * @param configRoot Path of the configuration root directory
   */
  public TemplateSetConfigurationReader(Path configRoot) {

    if (configRoot == null) {
      throw new IllegalArgumentException("Configuration path cannot be null.");
    }

    this.templateSetConfigurationPaths = new ArrayList<>();

    Path templateSetsDownloaded = configRoot.resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    Path templateSetsAdapted = configRoot.resolve(ConfigurationConstants.ADAPTED_FOLDER);

    if (!Files.exists(templateSetsDownloaded) && !Files.exists(templateSetsAdapted)) {
      throw new InvalidConfigurationException(configRoot,
          "Could not find any template-set configuration file in the given folder.");
    } else {
      if (Files.exists(templateSetsAdapted)) {
        this.templateSetConfigurationPaths
            .addAll(this.templateSetConfigurationManager.loadTemplateSetFilesAdapted(templateSetsAdapted));
      }

      if (Files.exists(templateSetsDownloaded)) {
        this.templateSetConfigurationPaths
            .addAll(this.templateSetConfigurationManager.loadTemplateSetFilesDownloaded(templateSetsDownloaded));
      }

      if (this.templateSetConfigurationPaths.isEmpty()) {
        throw new InvalidConfigurationException(configRoot,
            "Could not find any template-set configuration file in the given folder.");
      }
    }

    this.configRoot = configRoot;
  }

  /**
   * @return templateSetConfigurationPaths
   */
  public List<Path> getTemplateSetConfigurationPaths() {

    return this.templateSetConfigurationPaths;
  }

  /**
   * @return rootTemplateFolder
   */
  public TemplateFolder getRootTemplateFolder() {

    return this.rootTemplateFolder;
  }

  /**
   * Search for configuration files in the sub folder for adapted templates
   *
   * @param configurationRoot root directory of the configuration template-sets/adapted
   * @return List of Paths to the adapted templateSetFiles
   */
  protected List<Path> loadTemplateSetFilesAdapted(Path configurationRoot) {

    // We need to empty this list to prevent duplicates from being added
    this.templateSetConfigurationPaths.clear();
    List<Path> templateSetDirectories = new ArrayList<>();

    try (Stream<Path> files = Files.list(configurationRoot)) {
      files.forEach(path -> {
        if (Files.isDirectory(path)) {
          templateSetDirectories.add(path);
        }
      });
    } catch (IOException e) {
      throw new InvalidConfigurationException(configurationRoot, "Could not read configuration root directory.", e);
    }

    for (Path templateDirectory : templateSetDirectories) {
      Path templateSetFilePath = templateDirectory.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
          .resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

      addConfigRoot(templateSetFilePath, templateDirectory, this.templateSetConfigurationPaths);
    }

    return this.templateSetConfigurationPaths;
  }

  /**
   * Adds the path of a template-set.xml file to the list of all configuration files. Also adds the path of the
   * template-set.xml file and its root directory to the configRoots map
   *
   * @param templateSetFilePath the {@link Path} to the template-set.xml file
   * @param configRootPath the {@link Path} containing the configuration root directory for a template-set.xml
   * @param templateSetPaths a list containing all paths to template-set.xml files
   * @throws FileNotFoundException
   */
  public void addConfigRoot(Path templateSetFilePath, Path configRootPath, List<Path> templateSetPaths) {

    if (Files.exists(templateSetFilePath)) {
      templateSetPaths.add(templateSetFilePath);
      this.configLocations.put(templateSetFilePath, configRootPath);
    }
  }

  /**
   * Search for configuration files in the subfolder for downloaded template jars
   *
   * @param configurationRoot root directory of the configuration template-sets/downloaded
   * @return List of Paths to the downloaded templateSetFiles
   */
  protected List<Path> loadTemplateSetFilesDownloaded(Path configurationRoot) {

    // We need to empty this list to prevent duplicates from being added
    this.templateSetConfigurationPaths.clear();
    List<Path> templateJars = TemplatesJarUtil.getJarFiles(configurationRoot);
    if (templateJars != null) {
      for (Path jarPath : templateJars) {
        Path configurationPath = FileSystemUtil.createFileSystemDependentPath(jarPath.toUri());
        Path templateSetFilePath = configurationPath.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
            .resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

        addConfigRoot(templateSetFilePath, jarPath, this.templateSetConfigurationPaths);
      }
    }

    return this.templateSetConfigurationPaths;
  }

  /**
   * Reads the template set xml file and initializes the templates and context configurations and readers
   */
  public void readConfiguration(Path templateSetFile) {

    this.templateSetFile = templateSetFile;
    this.configLocation = this.templateSetFile.getParent();

    if (!FileSystemUtil.isZipFile(this.configRoot.toUri())) {
      this.configRoot = this.configLocation;
      initializeTemplateSetTemplatesRoot();
    }

    if (FileSystemUtil.isZipFile(this.configRoot.toUri())) {
      Path templateLocation = FileSystemUtil.createFileSystemDependentPath(this.configRoot.toUri());
      this.rootTemplateFolder = TemplateFolder.create(templateLocation.resolve(this.configLocation));
      initializeTemplateSetTemplatesRoot();
    }

    if (!Files.exists(this.templateSetFile)) {
      throw new InvalidConfigurationException(this.templateSetFile, "Could not find templates set configuration file.");
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

      URL schemaStream = getClass().getResource("/schema/" + templateSetVersion + "/templateSetConfiguration.xsd");
      try (InputStream configInputStream = Files.newInputStream(this.templateSetFile)) {

        Schema schema = schemaFactory.newSchema(schemaStream);
        unmarschaller.setSchema(schema);
        rootNode = (com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration) unmarschaller
            .unmarshal(configInputStream);
        this.templateSetConfiguration = rootNode;

        this.templatesConfiguration = rootNode.getTemplatesConfiguration();
        this.contextConfiguration = rootNode.getContextConfiguration();

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
   * @return templateSetConfigurations
   */
  public com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration getTemplateSetConfiguration() {

    return this.templateSetConfiguration;
  }

  /**
   * Initializes the template set templates root path (workaround for template/templates path)
   */
  private void initializeTemplateSetTemplatesRoot() {

    if (Files.exists(this.configLocation.resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH)))
      this.rootTemplateFolder = TemplateFolder
          .create(this.configLocation.resolve(ConfigurationConstants.CONFIG_PROPERTY_TEMPLATES_PATH));
    else {
      this.rootTemplateFolder = TemplateFolder.create(this.configLocation);
    }
  }

}

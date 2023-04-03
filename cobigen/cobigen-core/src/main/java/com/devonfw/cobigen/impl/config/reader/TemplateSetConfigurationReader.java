package com.devonfw.cobigen.impl.config.reader;

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

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.ExceptionUtil;
import com.devonfw.cobigen.api.util.JvmUtil;
import com.devonfw.cobigen.impl.config.constant.MavenMetadata;
import com.devonfw.cobigen.impl.config.constant.TemplateSetConfigurationVersion;
import com.devonfw.cobigen.impl.config.entity.TemplateFolder;
import com.devonfw.cobigen.impl.config.entity.io.TemplatesConfiguration;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator.Type;

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

  /** Path of the configuration location for a template-set.xml file e.g. src/main/resources */
  private Path configLocation;

  /** The static representation of the TemplateSetConfiguration */
  private com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration templateSetConfiguration;

  /** List with the paths of the configuration locations for the template-set.xml files */
  private Map<Path, Path> configLocations = new HashMap<>();

  /**
   * The {@link com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration}
   */
  private com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration contextConfiguration;

  /**
   * The {@link TemplatesConfiguration} to initialize
   */
  private TemplatesConfiguration templatesConfiguration;

  /** The top-level folder where the templates are located. */
  private TemplateFolder rootTemplateFolder;

  /** The list of adapted template set configuration paths */
  private List<Path> templateSetConfigurationPathsAdapted;

  /** The list of downloaded template set configuration paths */
  private List<Path> templateSetConfigurationPathsDownloaded;

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

    this.templateSetConfigurationPathsAdapted = new ArrayList<>();
    this.templateSetConfigurationPathsDownloaded = new ArrayList<>();

    Path templateSetsDownloaded = configRoot.resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    Path templateSetsAdapted = configRoot.resolve(ConfigurationConstants.ADAPTED_FOLDER);

    if (!Files.exists(templateSetsDownloaded) && !Files.exists(templateSetsAdapted)) {
      throw new InvalidConfigurationException(configRoot,
          "Could not find any template-set configuration file in the given folder.");
    } else {
      if (Files.exists(templateSetsAdapted)) {
        this.templateSetConfigurationPathsAdapted
            .addAll(this.templateSetConfigurationManager.loadTemplateSetFilesAdapted(templateSetsAdapted));
      }

      if (Files.exists(templateSetsDownloaded)) {
        this.templateSetConfigurationPathsDownloaded
            .addAll(this.templateSetConfigurationManager.loadTemplateSetFilesDownloaded(templateSetsDownloaded));
      }
      this.configLocations = this.templateSetConfigurationManager.getConfigLocations();

      if (this.templateSetConfigurationPathsAdapted.isEmpty()
          && this.templateSetConfigurationPathsDownloaded.isEmpty()) {
        throw new InvalidConfigurationException(configRoot,
            "Could not find any template-set configuration file in the given folder.");
      }
    }

  }

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
   * @return configLocations
   */
  public Map<Path, Path> getConfigLocations() {

    return this.configLocations;
  }

  /**
   * @return templateSetConfigurationPaths
   */
  public List<Path> getTemplateSetConfigurationPathsAdapted() {

    return this.templateSetConfigurationPathsAdapted;
  }

  /**
   * @return templateSetConfigurationPathsDownloaded
   */
  public List<Path> getTemplateSetConfigurationPathsDownloaded() {

    return this.templateSetConfigurationPathsDownloaded;
  }

  /**
   * @return rootTemplateFolder
   */
  public TemplateFolder getRootTemplateFolder() {

    return this.rootTemplateFolder;
  }

  /**
   * Reads the template set xml file and initializes the templates and context configurations and readers
   *
   * @param templateSetFile Path to template-set xml file
   */
  @SuppressWarnings("hiding")
  public void readConfiguration(Path templateSetFile) {

    this.templateSetFile = templateSetFile;
    this.configLocation = this.templateSetFile.getParent();

    this.rootTemplateFolder = TemplateFolder.create(this.configLocation);

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
          validator.validate(configVersion.floatValue());
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

}

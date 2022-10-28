package com.devonfw.cobigen.impl.config.reader;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.ConfigurationConflictException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.api.util.ExceptionUtil;
import com.devonfw.cobigen.api.util.JvmUtil;
import com.devonfw.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.devonfw.cobigen.impl.config.constant.MavenMetadata;
import com.devonfw.cobigen.impl.config.constant.WikiConstants;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.io.v3_0.ContextConfiguration;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator.Type;
import com.devonfw.cobigen.impl.util.FileSystemUtil;
import com.google.common.collect.Maps;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;

/** The {@link ContextConfigurationReader} reads the context xml */
public class ContextConfigurationReader extends AbstractContextConfigurationReader {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(ContextConfigurationReader.class);

  /** Map with XML Nodes 'context' of the context.xml files */
  protected Map<Path, ContextConfiguration> contextConfigurations;

  /** Map with the paths of the config location for a trigger */
  private Map<String, Path> triggerConfigLocations = new HashMap<>();

  /** Map with the paths of the config locations for a context.xml file */
  private Map<Path, Path> configLocations = new HashMap<>();

  /**
   * Creates a new instance of the {@link ContextConfigurationReader} which initially parses the given context file
   *
   * @param configRoot root directory of the configuration
   * @throws InvalidConfigurationException if the configuration is not valid against its xsd specification
   */
  public ContextConfigurationReader(Path configRoot) throws InvalidConfigurationException {

    super(configRoot);

    this.contextFiles = new ArrayList<>();

    // use old context.xml in templates root (CobiGen_Templates)
    Path contextFile = configRoot.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

    if (!Files.exists(contextFile)) {
      // if no context.xml is found in the root folder search in src/main/templates
      configRoot = configRoot.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
      contextFile = configRoot.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
      if (!Files.exists(contextFile)) {

        throw new InvalidConfigurationException(contextFile, "Could not find any context configuration file.");

      } else {
        checkForConflict(configRoot, contextFile);
        this.contextFiles.add(contextFile);
      }
    } else {
      Path subConfigRoot = configRoot.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
      if (Files.isDirectory(subConfigRoot)) {
        checkForConflict(subConfigRoot, contextFile);
      }
      this.contextFiles.add(contextFile);
    }

    this.contextRoot = configRoot;

    readConfiguration();
  }

  /**
   * Reads the context configuration.
   */
  @Override
  protected void readConfiguration() {

    // workaround to make JAXB work in OSGi context by
    // https://github.com/ControlSystemStudio/cs-studio/issues/2530#issuecomment-450991188
    final ClassLoader orig = Thread.currentThread().getContextClassLoader();
    if (JvmUtil.isRunningJava9OrLater()) {
      Thread.currentThread().setContextClassLoader(JAXBContext.class.getClassLoader());
    }

    this.contextConfigurations = new HashMap<>();

    for (Path contextFile : this.contextFiles) {
      try (InputStream in = Files.newInputStream(contextFile)) {
        Unmarshaller unmarschaller = JAXBContext.newInstance(ContextConfiguration.class).createUnmarshaller();

        // Unmarshal without schema checks for getting the version attribute of the root node.
        // This is necessary to provide an automatic upgrade client later on
        Object rootNode = unmarschaller.unmarshal(in);
        BigDecimal configVersion;
        if (rootNode instanceof ContextConfiguration) {
          configVersion = ((ContextConfiguration) rootNode).getVersion();
          if (configVersion == null) {
            throw new InvalidConfigurationException(contextFile,
                "The required 'version' attribute of node \"contextConfiguration\" has not been set");
          } else {
            VersionValidator validator = new VersionValidator(Type.CONTEXT_CONFIGURATION, MavenMetadata.VERSION);
            try {
              validator.validate(configVersion.floatValue());
            } catch (NotYetSupportedException e) {
              // TODO
            }
          }
        } else {
          throw new InvalidConfigurationException(contextFile,
              "Unknown Root Node. Use \"contextConfiguration\" as root Node");
        }

        // If we reach this point, the configuration version and root node has been validated.
        // Unmarshal with schema checks for checking the correctness and give the user more hints to
        // correct his failures
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        ContextConfigurationVersion contextVersion = ContextConfigurationVersion.values()[configVersion.intValue()];
        try (
            InputStream schemaStream = getClass()
                .getResourceAsStream("/schema/" + contextVersion + "/contextConfiguration.xsd");
            InputStream configInputStream = Files.newInputStream(contextFile)) {

          Schema schema = schemaFactory.newSchema(new StreamSource(schemaStream));
          unmarschaller.setSchema(schema);
          rootNode = unmarschaller.unmarshal(configInputStream);
          this.contextConfigurations.put(contextFile, (ContextConfiguration) rootNode);
        }
      } catch (JAXBException e) {
        // try getting SAXParseException for better error handling and user support
        Throwable parseCause = ExceptionUtil.getCause(e, SAXParseException.class, UnmarshalException.class);
        String message = "";
        if (parseCause != null && parseCause.getMessage() != null) {
          message = parseCause.getMessage();
        }

        throw new InvalidConfigurationException(contextFile, "Could not parse configuration file:\n" + message, e);
      } catch (SAXException e) {
        // Should never occur. Programming error.
        throw new IllegalStateException("Could not parse context configuration schema. Please state this as a bug.");
      } catch (NumberFormatException e) {
        // The version number is currently the only xml value which will be parsed to a number data type
        // So provide help
        throw new InvalidConfigurationException(
            "Invalid version number defined. The version of the context configuration should consist of 'major.minor' version.");
      } catch (IOException e) {
        throw new InvalidConfigurationException(contextFile, "Could not read context configuration file.", e);
      } finally {
        Thread.currentThread().setContextClassLoader(orig);
      }
    }
  }

  /**
   * Checks if a conflict with the old and modular configuration exists
   *
   * @param configRoot Path to root directory of the configuration
   * @param contextFile Path to context file of the configuration
   */
  private void checkForConflict(Path configRoot, Path contextFile) {

    if (!loadContextFilesInSubfolder(configRoot).isEmpty()) {
      String message = "You are using an old configuration of the templates in addition to new ones. Please make sure this is not the case as both at the same time are not supported. For more details visit this wiki page: "
          + WikiConstants.WIKI_UPDATE_OLD_CONFIG;
      ConfigurationConflictException exception = new ConfigurationConflictException(contextFile, message);
      LOG.error("A conflict with the old and modular configuration exists", exception);
      throw exception;
    }

  }

  /**
   * search for configuration Files in the subfolders of configRoot
   *
   * @param configRoot root directory of the configuration
   * @throws InvalidConfigurationException if the configuration is not valid against its xsd specification
   */
  private List<Path> loadContextFilesInSubfolder(Path configRoot) {

    List<Path> contextPaths = new ArrayList<>();

    List<Path> templateDirectories = new ArrayList<>();

    try (Stream<Path> files = Files.list(configRoot)) {
      files.forEach(path -> {
        if (Files.isDirectory(path)) {
          templateDirectories.add(path);
        }
      });
    } catch (IOException e) {
      throw new InvalidConfigurationException(configRoot, "Could not read configuration root directory.", e);
    }

    for (Path file : templateDirectories) {
      Path contextPath = file.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
      if (Files.exists(contextPath)) {
        contextPaths.add(contextPath);
      }
    }

    return contextPaths;
  }

  /**
   * Loads all {@link Trigger}s of the static context into the local representation
   *
   * @return a {@link List} containing all the {@link Trigger}s
   */
  @Override
  public Map<String, Trigger> loadTriggers() {

    Map<String, Trigger> triggers = Maps.newHashMap();
    for (Path contextFile : this.contextConfigurations.keySet()) {
      ContextConfiguration contextConfiguration = this.contextConfigurations.get(contextFile);
      Path configLocation = this.configLocations.get(contextFile);
      boolean isJarFile = FileSystemUtil.isZipFile(configLocation.toUri());
      for (com.devonfw.cobigen.impl.config.entity.io.v3_0.Trigger t : contextConfiguration.getTrigger()) {
        // templateFolder property is optional in schema version 3.0. If not set take the path of the context.xml file
        String templateFolder = t.getTemplateFolder();
        if (templateFolder.isEmpty() || templateFolder.equals("/")) {
          templateFolder = contextFile.getParent().getFileName().toString();
        }
        triggers.put(t.getId(), new Trigger(t.getId(), t.getType(), templateFolder,
            Charset.forName(t.getInputCharset()), loadMatchers(t), loadContainerMatchers(t)));
      }
    }
    return triggers;
  }
}
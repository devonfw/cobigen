package com.devonfw.cobigen.impl.config.reader;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.ExceptionUtil;
import com.devonfw.cobigen.api.util.JvmUtil;
import com.devonfw.cobigen.impl.config.constant.ContextConfigurationVersion;
import com.devonfw.cobigen.impl.config.constant.MavenMetadata;
import com.devonfw.cobigen.impl.config.constant.WikiConstants;
import com.devonfw.cobigen.impl.config.entity.ContainerMatcher;
import com.devonfw.cobigen.impl.config.entity.Matcher;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.VariableAssignment;
import com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator.Type;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;

/** The {@link ContextConfigurationReader} reads the context xml */
public class ContextConfigurationReader {

  /** Map with XML Nodes 'context' of the context.xml files */
  private Map<Path, ContextConfiguration> contextConfigurations;

  /** Paths of the context configuration files */
  private List<Path> contextFiles;

  /** Root of the context configuration file, used for passing to ContextConfiguration */
  private Path contextRoot;

  /**
   * Creates a new instance of the {@link ContextConfigurationReader} which initially parses the given context file
   *
   * @param configRoot root directory of the configuration
   * @throws InvalidConfigurationException if the configuration is not valid against its xsd specification
   */
  public ContextConfigurationReader(Path configRoot) throws InvalidConfigurationException {

    if (configRoot == null) {
      throw new IllegalArgumentException("Configuration path cannot be null.");
    }

    this.contextFiles = new ArrayList<>();

    // use old context.xml in templates root
    Path contextFile = configRoot.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);

    if (!Files.exists(contextFile)) {
      // if no context.xml is found in the root folder search in src/main/templates
      configRoot = configRoot.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER);
      contextFile = configRoot.resolve(ConfigurationConstants.CONTEXT_CONFIG_FILENAME);
      if (!Files.exists(contextFile)) {

        this.contextFiles = loadContextFilesInSubfolder(configRoot);

        if (this.contextFiles.isEmpty()) {
          throw new InvalidConfigurationException(contextFile, "Could not find any context configuration file.");
        }

      } else {
        this.contextFiles.add(contextFile);
        // check if conflict with old and modular configuration exists

        if (!loadContextFilesInSubfolder(configRoot).isEmpty())
          throw new InvalidConfigurationException(contextFile,
              "You are using an old configuration of the templates in addition to new ones. Please make sure this is not the case as both at the same time are not supported. For more details visit this wiki page: "
                  + WikiConstants.WIKI_UPDATE_OLD_CONFIG);

      }
    } else {
      this.contextFiles.add(contextFile);
    }

    this.contextRoot = configRoot;

    readConfiguration();
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
   * Reads the context configuration.
   */
  private void readConfiguration() {

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
        if (rootNode instanceof ContextConfiguration) {
          BigDecimal configVersion = ((ContextConfiguration) rootNode).getVersion();
          if (configVersion == null) {
            throw new InvalidConfigurationException(contextFile,
                "The required 'version' attribute of node \"contextConfiguration\" has not been set");
          } else {
            VersionValidator validator = new VersionValidator(Type.CONTEXT_CONFIGURATION, MavenMetadata.VERSION);
            validator.validate(configVersion.floatValue());
          }
        } else {
          throw new InvalidConfigurationException(contextFile,
              "Unknown Root Node. Use \"contextConfiguration\" as root Node");
        }

        // If we reach this point, the configuration version and root node has been validated.
        // Unmarshal with schema checks for checking the correctness and give the user more hints to
        // correct his failures
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        ContextConfigurationVersion latestConfigurationVersion = ContextConfigurationVersion.getLatest();
        try (
            InputStream schemaStream = getClass()
                .getResourceAsStream("/schema/" + latestConfigurationVersion + "/contextConfiguration.xsd");
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
   * Loads all {@link Trigger}s of the static context into the local representation
   *
   * @return a {@link List} containing all the {@link Trigger}s
   */
  public Map<String, Trigger> loadTriggers() {

    // Read both for now

    Map<String, Trigger> triggers = Maps.newHashMap();
    for (Path contextFile : this.contextConfigurations.keySet()) {
      ContextConfiguration contextConfiguration = this.contextConfigurations.get(contextFile);
      if (contextConfiguration.getVersion().equals(ContextConfigurationVersion.v3_0)) {
        com.devonfw.cobigen.impl.config.entity.io.Trigger t = contextConfiguration.getTrigger();
        // templateFolder property is optional in schema version 2.2. If not set take the path of the context.xml file
        String templateFolder = t.getTemplateFolder();
        if (templateFolder.isEmpty() || templateFolder.equals("/")) {
          templateFolder = contextFile.getParent().getFileName().toString();
        }
        triggers.put(t.getId(), new Trigger(t.getId(), t.getType(), templateFolder,
            Charset.forName(t.getInputCharset()), loadMatchers(t), loadContainerMatchers(t)));
      } else {
        for (com.devonfw.cobigen.impl.config.entity.io.Trigger t : com.devonfw.cobigen.impl.config.entity.io.v2_2.ContextConfiguration
            .getTrigger()) {
          // templateFolder property is optional in schema version 2.2. If not set take the path of the context.xml file
          String templateFolder = t.getTemplateFolder();
          if (templateFolder.isEmpty() || templateFolder.equals("/")) {
            templateFolder = contextFile.getParent().getFileName().toString();
          }
          triggers.put(t.getId(), new Trigger(t.getId(), t.getType(), templateFolder,
              Charset.forName(t.getInputCharset()), loadMatchers(t), loadContainerMatchers(t)));
        }
      }
    }

    return triggers;
  }

  /**
   * Loads all {@link Matcher}s of a given {@link com.devonfw.cobigen.impl.config.entity.io.Trigger}
   *
   * @param trigger {@link com.devonfw.cobigen.impl.config.entity.io.Trigger} to retrieve the {@link Matcher}s from
   * @return the {@link List} of {@link Matcher}s
   */
  private List<Matcher> loadMatchers(com.devonfw.cobigen.impl.config.entity.io.Trigger trigger) {

    List<Matcher> matcher = new LinkedList<>();
    for (com.devonfw.cobigen.impl.config.entity.io.Matcher m : trigger.getMatcher()) {
      matcher.add(new Matcher(m.getType(), m.getValue(), loadVariableAssignments(m), m.getAccumulationType()));
    }
    return matcher;
  }

  /**
   * Loads all {@link ContainerMatcher}s of a given {@link com.devonfw.cobigen.impl.config.entity.io.Trigger}
   *
   * @param trigger {@link com.devonfw.cobigen.impl.config.entity.io.Trigger} to retrieve the {@link Matcher}s from
   * @return the {@link List} of {@link Matcher}s
   */
  private List<ContainerMatcher> loadContainerMatchers(com.devonfw.cobigen.impl.config.entity.io.Trigger trigger) {

    List<ContainerMatcher> containerMatchers = Lists.newLinkedList();
    for (com.devonfw.cobigen.impl.config.entity.io.ContainerMatcher cm : trigger.getContainerMatcher()) {
      containerMatchers.add(new ContainerMatcher(cm.getType(), cm.getValue(), cm.isRetrieveObjectsRecursively()));
    }
    return containerMatchers;
  }

  /**
   * Loads all {@link VariableAssignment}s from a given {@link com.devonfw.cobigen.impl.config.entity.io.Matcher}
   *
   * @param matcher {@link com.devonfw.cobigen.impl.config.entity.io.Matcher} to retrieve the {@link VariableAssignment}
   *        from
   * @return the {@link List} of {@link Matcher}s
   */
  private List<VariableAssignment> loadVariableAssignments(com.devonfw.cobigen.impl.config.entity.io.Matcher matcher) {

    List<VariableAssignment> variableAssignments = new LinkedList<>();
    for (com.devonfw.cobigen.impl.config.entity.io.VariableAssignment va : matcher.getVariableAssignment()) {
      variableAssignments.add(new VariableAssignment(va.getType(), va.getKey(), va.getValue(), va.isMandatory()));
    }
    return variableAssignments;
  }

  /**
   * @return the path of the context file
   */
  public Path getContextRoot() {

    return this.contextRoot;
  }

  /**
   * @return the list of the context files
   */
  public List<Path> getContextFiles() {

    return this.contextFiles;
  }

}

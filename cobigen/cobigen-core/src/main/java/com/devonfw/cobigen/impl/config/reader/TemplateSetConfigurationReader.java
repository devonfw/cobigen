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
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.api.util.ExceptionUtil;
import com.devonfw.cobigen.api.util.JvmUtil;
import com.devonfw.cobigen.api.util.TemplatesJarUtil;
import com.devonfw.cobigen.impl.config.constant.MavenMetadata;
import com.devonfw.cobigen.impl.config.constant.TemplateSetConfigurationVersion;
import com.devonfw.cobigen.impl.config.entity.ContainerMatcher;
import com.devonfw.cobigen.impl.config.entity.Matcher;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.VariableAssignment;
import com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator.Type;
import com.devonfw.cobigen.impl.util.FileSystemUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;

public class TemplateSetConfigurationReader {

  /** Map with the paths of the template set locations for a template-set.xml file */
  private Map<Path, Path> templateSetLocations = new HashMap<>();

  /** Map with the paths of the template set location for a trigger */
  private Map<String, Path> triggerTemplateSetLocations = new HashMap<>();

  /** Map with XML Nodes 'context' of the context.xml files */
  protected Map<Path, TemplateSetConfiguration> templateSetConfigurations;

  /** Paths of the template set configuration files */
  protected List<Path> templateSetFiles;

  /** Root of the template set configuration file, used for passing to TemplateSetConfiguration */
  protected Path templateSetRoot;

  /**
   * The constructor.
   *
   * @param configRoot the config root directory
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public TemplateSetConfigurationReader(Path configRoot) throws InvalidConfigurationException {

    this.templateSetFiles = new ArrayList<>();

    Path templateSetsDownloaded = configRoot.resolve(ConfigurationConstants.DOWNLOADED_FOLDER);
    Path templateSetsAdapted = configRoot.resolve(ConfigurationConstants.ADAPTED_FOLDER);

    if (!Files.exists(templateSetsDownloaded) && !Files.exists(templateSetsAdapted)) {
      throw new InvalidConfigurationException(configRoot,
          "Could not find a folder in which to search for the template set configuration file.");
    } else {
      if (Files.exists(templateSetsAdapted)) {
        this.templateSetFiles.addAll(loadContextFilesAdapted(templateSetsAdapted));
      }

      if (Files.exists(templateSetsDownloaded)) {
        this.templateSetFiles.addAll(loadContextFilesDownloaded(templateSetsDownloaded));
      }

      if (this.templateSetFiles.isEmpty()) {
        throw new InvalidConfigurationException(configRoot, "Could not find any template set configuration file.");
      }
    }

    this.templateSetRoot = configRoot;

    readConfiguration();
  }

  /**
   * Reads the template-set configuration.
   */
  protected void readConfiguration() {

    // workaround to make JAXB work in OSGi context by
    // https://github.com/ControlSystemStudio/cs-studio/issues/2530#issuecomment-450991188
    final ClassLoader orig = Thread.currentThread().getContextClassLoader();
    if (JvmUtil.isRunningJava9OrLater()) {
      Thread.currentThread().setContextClassLoader(JAXBContext.class.getClassLoader());
    }

    this.templateSetConfigurations = new HashMap<>();

    for (Path templateSetFile : this.templateSetFiles) {
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
  private List<Path> loadContextFilesAdapted(Path configRoot) {

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

    for (Path templateDirectory : templateDirectories) {
      Path contextFilePath = templateDirectory.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
          .resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

      addConfigRoot(contextFilePath, templateDirectory, contextPaths);
    }

    return contextPaths;
  }

  /**
   * search for configuration files in the subfolder for downloaded template jars
   *
   * @param configRoot root directory of the configuration template-sets/downloaded
   */
  private List<Path> loadContextFilesDownloaded(Path configRoot) {

    List<Path> contextPaths = new ArrayList<>();

    List<Path> templateJars = TemplatesJarUtil.getJarFiles(configRoot);
    if (templateJars != null) {
      for (Path jarPath : templateJars) {
        Path configurationPath = FileSystemUtil.createFileSystemDependentPath(jarPath.toUri());
        Path contextFilePath = configurationPath.resolve(ConfigurationConstants.TEMPLATE_RESOURCE_FOLDER)
            .resolve(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME);

        addConfigRoot(contextFilePath, jarPath, contextPaths);
      }
    }

    return contextPaths;
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
      Path configLocation = this.templateSetLocations.get(contextFile);
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
   * Adds the path to a context.xml file to the list of all config files. Also adds the path of the context.xml file and
   * its root directory to the configRoots map
   *
   * @param contextFilePath the {@link Path} to the context.xml file
   * @param configRootPath the {@link Path} containing the config root directory for a context.xml
   * @param contextPaths a list containing all paths to context.xml files
   */
  private void addConfigRoot(Path contextFilePath, Path configRootPath, List<Path> contextPaths) {

    if (Files.exists(contextFilePath)) {
      contextPaths.add(contextFilePath);
      this.templateSetLocations.put(contextFilePath, configRootPath);
    }
  }

  /**
   * @return the list of the template set files
   */
  public List<Path> getTemplateSetFiles() {

    return this.templateSetFiles;
  }
}

package com.devonfw.cobigen.impl.config;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.util.FileSystemUtil;
import com.google.common.collect.Maps;

/**
 * Cached in-memory CobiGen configuration.
 */
public class ConfigurationHolder {

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationHolder.class);

  /** Cached templates configurations. Trigger ID -> Configuration File URI -> configuration instance */
  private Map<String, Map<Path, TemplatesConfiguration>> templatesConfigurations = Maps.newHashMap();

  /** Cached context configuration */
  private ContextConfiguration contextConfiguration;

  /** Cached template-set configuration */
  private TemplateSetConfiguration templateSetConfiguration;

  /** Root path of the configuration */
  private Path contextConfigurationPath;

  /** Root path of the configuration */
  private Path templateSetConfigurationPath;

  /** The OS filesystem path of the configuration */
  private URI configurationLocation;

  /** The factory class which initializes new configurations */
  private ConfigurationFactory configurationFactory;

  /** Location where the properties are saved */
  private ConfigurationProperties configurationProperties;

  /**
   * Creates a new {@link ConfigurationHolder} which serves as a cache for CobiGen's external configuration.
   *
   * @param configurationLocation the OS Filesystem path of the configuration location.
   */
  public ConfigurationHolder(URI configurationLocation) {

    this.configurationLocation = configurationLocation;
    this.contextConfigurationPath = FileSystemUtil.createFileSystemDependentPath(configurationLocation);
    this.configurationFactory = new ConfigurationFactory(configurationLocation);

    // updates the root template path and informs all of its observers
    PluginRegistry.notifyPlugins(this.contextConfigurationPath);
  }

  /**
   * @return <code>true</code> if the configuration is based in a JAR file
   */
  public boolean isJarConfig() {

    return FileSystemUtil.isZipFile(this.configurationLocation);
  }

  /**
   * @return the path of the configuration based on the OS filesystem. It could be a .jar file or a maven project root
   *         folder
   */
  public URI getConfigurationLocation() {

    return this.configurationLocation;
  }

  /**
   * @return the path within the configuration. Might be a different file system than OS in case of a .jar configuration
   */
  public Path getConfigurationPath() {

    return this.contextConfigurationPath;
  }

  /**
   * Reads the {@link TemplatesConfiguration} from cache or from file if not present in cache.
   *
   * @param trigger to get matcher declarations from
   * @return the {@link TemplatesConfiguration}
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public TemplatesConfiguration readTemplatesConfiguration(Trigger trigger) {

    Path templateFolder = Paths.get(trigger.getTemplateFolder());
    return this.configurationFactory.retrieveTemplatesConfiguration(this.templatesConfigurations, templateFolder,
        trigger, this);
  }

  /**
   * Reads the {@link ContextConfiguration} from cache or from file if not present in cache.
   *
   * @return the {@link ContextConfiguration}
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public ContextConfiguration readContextConfiguration() {

    if (this.contextConfiguration == null) {
      this.contextConfiguration = new ContextConfiguration(this.contextConfigurationPath);
    }
    return this.contextConfiguration;
  }

  /**
   * @return return if the template folder structure consists of template sets or if the monolithic structure is used.
   */
  public boolean isTemplateSetConfiguration() {

    if (this.contextConfigurationPath.toUri().getScheme().equals("jar") || !this.contextConfigurationPath.getFileName()
        .toString().equals(ConfigurationConstants.TEMPLATE_SETS_FOLDER)) {
      return false;
    }
    return true;

  }

  /**
   * Search for the location of the Java utils
   *
   * @return the {@link Path} of the location of the util classes or null if no location was found
   */
  public List<Path> getUtilsLocation() {

    List<Path> utilsLocationPaths = new ArrayList<>();
    if (isTemplateSetConfiguration()) {
      List<Trigger> triggers = readContextConfiguration().getTriggers();

      for (Trigger trigger : triggers) {
        Path configLocation = readContextConfiguration().getConfigLocationforTrigger(trigger.getId(), false);
        utilsLocationPaths.add(configLocation);
      }
    } else {
      utilsLocationPaths.add(Paths.get(this.configurationLocation));
    }

    return utilsLocationPaths;
  }

  /**
   * This is a helper method to read a given cobigen configuration file
   *
   * @param cobigenConfigFile cobigen configuration file
   * @throws InvalidConfigurationException if the file isn't present or the path is invalid
   * @return Properties containing configuration
   */
  private Properties readConfigurationFile(Path cobigenConfigFile) {

    Properties props = new Properties();
    try {
      String configFileContents = Files.readAllLines(cobigenConfigFile, Charset.forName("UTF-8")).stream()
          .collect(Collectors.joining("\n"));
      configFileContents = configFileContents.replace("\\", "\\\\");
      try (StringReader strReader = new StringReader(configFileContents)) {
        props.load(strReader);
      }
    } catch (IOException e) {
      throw new InvalidConfigurationException("An error occured while reading the config file " + cobigenConfigFile, e);
    }
    return props;
  }

}

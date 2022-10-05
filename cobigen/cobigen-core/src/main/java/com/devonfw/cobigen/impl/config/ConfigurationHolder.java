package com.devonfw.cobigen.impl.config;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.MavenCoordinate;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.util.FileSystemUtil;
import com.devonfw.cobigen.impl.util.MavenCoordinateUtil;
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
  private ContextConfigurationDecorator contextConfiguration;

  /** Cached template-set configuration */
  private TemplateSetConfigurationDecorator templateSetConfiguration;

  /** Root path of the configuration */
  private Path configurationPath;

  /** The OS filesystem path of the configuration */
  private URI configurationLocation;

  /** The factory class which initializes new configurations */
  private ConfigurationFactory configurationFactory;

  /**
   * Creates a new {@link ConfigurationHolder} which serves as a cache for CobiGen's external configuration.
   *
   * @param configurationLocation the OS Filesystem path of the configuration location.
   */
  public ConfigurationHolder(URI configurationLocation) {

    this.configurationLocation = configurationLocation;
    this.configurationPath = FileSystemUtil.createFileSystemDependentPath(configurationLocation);
    this.configurationFactory = new ConfigurationFactory(configurationLocation);

    // updates the root template path and informs all of its observers
    PluginRegistry.notifyPlugins(this.configurationPath);
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

    return this.configurationPath;
  }

  /**
   * Reads the {@link TemplatesConfiguration} from cache or from file if not present in cache.
   *
   * @param trigger to get matcher declarations from
   * @return the {@link TemplatesConfiguration}
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public TemplatesConfiguration readTemplatesConfiguration(Trigger trigger) {

    Path configRoot = readContextConfiguration().getConfigLocationforTrigger(trigger.getId(), true);
    Path templateFolder = Paths.get(trigger.getTemplateFolder());
    return this.configurationFactory.getTemplatesConfiguration(this.templatesConfigurations, templateFolder, trigger,
        this);
  }

  /**
   * Reads the {@link ContextConfigurationDecorator} from cache or from file if not present in cache.
   *
   * @return the {@link ContextConfigurationDecorator}
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public ContextConfigurationDecorator readContextConfiguration() {

    if (this.contextConfiguration == null) {
      this.contextConfiguration = new ContextConfigurationDecorator(this.configurationPath);
    }
    return this.contextConfiguration;
  }

  /**
   * @param path the configuration root path
   * @return the {@link TemplateSetConfigurationDecorator}
   */
  public TemplateSetConfigurationDecorator readTemplateSetConfiguration(Path path) {

    Properties props = new Properties();
    try {
      props = readConfigurationFile(path);
    } catch (InvalidConfigurationException e) {
      LOG.info("This path {} is invalid. The default Config values will be loaded instead.", path);
    }

    String groupId = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_GROUPIDS;
    String snapshot = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_SNAPSHOTS;
    String hide = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_HIDE;
    String disableLookup = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DISABLE_LOOKUP;
    String defaultGroupId = ConfigurationConstants.CONFIG_PROPERTY_TEMPLATE_SETS_DEFAULT_GROUPID;

    List<String> groupIdsList = (props.getProperty(groupId) != null)
        ? Arrays.asList(props.getProperty(groupId).split(","))
        : new ArrayList<>();
    // Creating a new ArrayList object which can be modified and prevents UnsupportedOperationException.
    List<String> groupIds = new ArrayList<>(groupIdsList);
    if (props.getProperty(disableLookup) == null || props.getProperty(disableLookup).equals("false"))
      if (!groupIds.contains(defaultGroupId))
        groupIds.add(defaultGroupId);

    boolean useSnapshots = props.getProperty(snapshot) == null || props.getProperty(snapshot).equals("false") ? false
        : true;
    List<String> hiddenIdsString = (props.getProperty(hide) != null) ? Arrays.asList(props.getProperty(hide).split(","))
        : new ArrayList<>();

    List<MavenCoordinate> hiddenIds = MavenCoordinateUtil.convertToMavenCoordinates(hiddenIdsString);
    ConfigurationFactory configurationFactory = new ConfigurationFactory(this.configurationLocation);
    this.templateSetConfiguration = configurationFactory.getTemplateSetConfiguration(groupIds, useSnapshots, hiddenIds);
    return this.templateSetConfiguration;
  }

  /**
   * @return return if the template folder structure consists of template sets or if the old structure is used
   */
  public boolean isTemplateSetConfiguration() {

    if (this.configurationPath.toUri().getScheme().equals("jar")
        || !this.configurationPath.getFileName().toString().equals(ConfigurationConstants.TEMPLATE_SETS_FOLDER)) {
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

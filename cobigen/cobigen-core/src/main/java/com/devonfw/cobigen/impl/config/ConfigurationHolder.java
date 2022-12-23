package com.devonfw.cobigen.impl.config;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.util.ConfigurationFinder;
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

  /** Cached templateSet configuration */
  private TemplateSetConfiguration templateSetConfiguration;

  /**
   * @return templateSetConfiguration
   */
  public TemplateSetConfiguration getTemplateSetConfiguration() {

    return this.templateSetConfiguration;
  }

  /** Root path of the configuration */
  private Path configurationPath;

  /** The OS filesystem path of the configuration */
  private URI configurationLocation;

  /** Location where the properties are saved */
  private ConfigurationProperties configurationProperties;

  /**
   * Creates a new {@link ConfigurationHolder} which serves as a cache for CobiGen's external configuration. Since this
   * is a Singleton, this constructor is private
   *
   * @param configurationLocation the OS Filesystem path of the configuration location.
   */
  public ConfigurationHolder(URI configurationLocation) {

    this.configurationLocation = configurationLocation;
    this.configurationPath = FileSystemUtil.createFileSystemDependentPath(configurationLocation);
    this.configurationProperties = ConfigurationFinder.loadTemplateSetConfigurations(
        CobiGenPaths.getCobiGenHomePath().resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE), this.configurationPath);

    // updates the root template path and informs all of its observers
    PluginRegistry.notifyPlugins(this.configurationPath);
  }

  /**
   * @return configurationProperties
   */
  public ConfigurationProperties getConfigurationProperties() {

    return this.configurationProperties;
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

    if (!this.templatesConfigurations.containsKey(trigger.getId())) {

      TemplatesConfiguration config;

      if (isTemplateSetConfiguration()) {

        List<TemplatesConfiguration> templatesConfigurations = getTemplateSetConfiguration()
            .getTemplatesConfigurations();

        for (TemplatesConfiguration configurations : templatesConfigurations) {
          Trigger localTrigger = configurations.getTrigger();
          if (localTrigger.equals(trigger)) {
            return configurations;
          }
        }

      }
      config = new TemplatesConfiguration(configRoot, trigger, this);

      this.templatesConfigurations.put(trigger.getId(), Maps.<Path, TemplatesConfiguration> newHashMap());

      this.templatesConfigurations.get(trigger.getId()).put(templateFolder, config);
    }

    return this.templatesConfigurations.get(trigger.getId()).get(templateFolder);
  }

  /**
   * @return templatesConfigurations
   */
  public Map<String, Map<Path, TemplatesConfiguration>> getTemplatesConfigurations() {

    return this.templatesConfigurations;
  }

  /**
   * Reads the {@link ContextConfiguration} from cache or from file if not present in cache.
   *
   * @return the {@link ContextConfiguration}
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  public ContextConfiguration readContextConfiguration() {

    if (this.contextConfiguration == null) {
      if (isTemplateSetConfiguration()) {
        if (this.templateSetConfiguration == null) {
          this.templateSetConfiguration = new TemplateSetConfiguration(this.configurationPath);

        }
        this.contextConfiguration = new ContextConfiguration(
            this.templateSetConfiguration.getTemplateSetConfigurationReader().getContextConfiguration(),
            this.configurationPath, this, this.templateSetConfiguration.getTriggers());

      } else {

        this.contextConfiguration = new ContextConfiguration(this.configurationPath, isTemplateSetConfiguration());
      }
    }

    return this.contextConfiguration;
  }

  /**
   * checks if this this a template set configuration or a templates configuration (true if templateSetConfiguraion)
   *
   * @return return if the template folder structure consists of template sets or if the monolithic structure is used.
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
   * @param templateSetConfigurations Cached templateSet configurations
   * @param templateSetFolder folder where to get the specific configuration from
   * @return the {@link TemplateSetConfiguration} instance saved in the given map
   */
  public TemplateSetConfiguration retrieveTemplateSetConfiguration(
      Map<Path, TemplateSetConfiguration> templateSetConfigurations, Path templateSetFolder) {

    return templateSetConfigurations.get(templateSetFolder);
  }

}

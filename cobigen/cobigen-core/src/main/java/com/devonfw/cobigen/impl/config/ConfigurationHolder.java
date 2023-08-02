package com.devonfw.cobigen.impl.config;

import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.reader.ConfigurationReader;
import com.devonfw.cobigen.impl.config.reader.ConfigurationReaderFactory;
import com.devonfw.cobigen.impl.config.reader.TemplateSetsConfigReader;
import com.devonfw.cobigen.impl.util.ConfigurationFinder;
import com.devonfw.cobigen.impl.util.FileSystemUtil;

/**
 * Cached in-memory CobiGen configuration.
 */
public class ConfigurationHolder {

  /**
   * Cached templates configurations. Trigger ID -> Configuration instance
   */
  private final Map<String, TemplatesConfiguration> templatesConfigurations = new HashMap<>();

  /**
   * Cached context configuration
   */
  private final ContextConfiguration contextConfiguration;

  /**
   * The OS filesystem path of the configuration
   */
  private final URI configurationLocation;

  /** Configuration properties */
  private final ConfigurationProperties configurationProperties;

  private final ConfigurationReader configurationReader;

  /**
   * Creates a new {@link ConfigurationHolder} which serves as a cache for CobiGen's external configuration.
   *
   * @param configurationLocation the OS file system path of the configuration location.
   */
  public ConfigurationHolder(URI configurationLocation) {

    this.configurationLocation = configurationLocation;
    this.configurationReader = ConfigurationReaderFactory.create(configurationLocation);
    this.contextConfiguration = this.configurationReader.readContextConfiguration();

    this.configurationProperties = ConfigurationFinder.retrieveCobiGenProperties(
        CobiGenPaths.getCobiGenHomePath().resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE));
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
   * Search for the location of the Java utils
   *
   * @return the {@link Path} of the location of the util classes or null if no location was found
   */
  public List<Path> getUtilsLocation(Trigger trigger) {

    return List.of(getTemplatesConfiguration(trigger).getConfigRoot());
  }

  public Trigger getTrigger(String s) {

    return this.contextConfiguration.getTrigger(s);
  }

  public TemplatesConfiguration getTemplatesConfiguration(Trigger trigger) {

    if (this.templatesConfigurations.containsKey(trigger.getId())) {
      return this.templatesConfigurations.get(trigger.getId());
    }
    TemplatesConfiguration templatesConfiguration = this.configurationReader
        .readTemplatesConfiguration(trigger.getId());
    this.templatesConfigurations.put(trigger.getId(), templatesConfiguration);
    return templatesConfiguration;
  }

  public ContextConfiguration getContextConfiguration() {

    return this.contextConfiguration;
  }

  public boolean isTemplateSetConfiguration() {

    return this.configurationReader instanceof TemplateSetsConfigReader;
  }
}

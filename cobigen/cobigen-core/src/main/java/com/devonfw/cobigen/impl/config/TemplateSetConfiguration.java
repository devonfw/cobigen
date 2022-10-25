package com.devonfw.cobigen.impl.config;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.MavenCoordinate;
import com.devonfw.cobigen.impl.config.entity.Increment;
import com.devonfw.cobigen.impl.config.entity.Template;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.reader.TemplateSetConfigurationReader;
import com.google.common.collect.Maps;

/**
 * This is the readable and persistent representation of the automatically generated TemplateSetConfiguration file
 *
 */
public class TemplateSetConfiguration {

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(TemplateSetConfiguration.class);

  /** The stored properties are groupIds, allowSnapshots and hideTemplates */
  private ConfigurationProperties configurationProperties;

  /** List of mavenCoordinates for the template sets that should be installed at cobigen startup */
  private List<MavenCoordinate> mavenCoordinates;

  /** All available {@link Trigger}s */
  private Map<String, Trigger> triggers;

  /** All available {@Link Template}s */
  private Map<String, Template> templates;

  /** All available {@Link Increment} */
  private Map<String, Increment> increments;

  /** The automatically generated templateSetConfiguration this class wraps */
  private com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration templateSetConfiguration;

  /** The reader to read the template-set.xml files */
  public TemplateSetConfigurationReader templateSetConfigurationReader;

  /** Paths of the template set configuration files */
  public List<Path> templateSetFiles = new ArrayList<>();

  /** Root of the configuration */
  public Path configRoot;

  /**
   * @param configRoot Root of the configuration
   */
  public TemplateSetConfiguration(Path configRoot) {

    this.triggers = Maps.newHashMap();
    this.templates = Maps.newHashMap();
    readConfiguration(configRoot);

  }

  /**
   * The constructor. loads properties from a given source
   *
   * @param configRoot Root of the configuration
   * @param properties the configuration properties
   */
  public TemplateSetConfiguration(ConfigurationProperties properties, Path configRoot) {

    this(configRoot);
    setConfigurationProperties(properties);
  }

  /**
   * Reads the configuration from the given path
   *
   * @param configRoot CobiGen configuration root path
   * @throws InvalidConfigurationException thrown if the {@link File} is not valid with respect to the context.xsd
   */

  public void readConfiguration(Path configRoot) throws InvalidConfigurationException {

    if (this.templateSetConfigurationReader == null) {
      this.templateSetConfigurationReader = new TemplateSetConfigurationReader(configRoot, this);
    }

    this.increments = new HashMap<>();
    for (Path templateSetFile : this.templateSetFiles) {
      this.templateSetConfigurationReader.templateSetFile = templateSetFile;
      this.templateSetConfigurationReader.readConfiguration();
      // Fix this this.configurationPath = this.templateSetConfigurationReader.getContextRoot();
      this.triggers.putAll(this.templateSetConfigurationReader.loadTriggers());
      this.templates.putAll(this.templateSetConfigurationReader.loadTemplates());
    }

    // For every trigger put all increments depended on that trigger into the local increments hash map
    for (Entry<String, Trigger> trigger : this.triggers.entrySet()) {
      this.increments.putAll(this.templateSetConfigurationReader.loadIncrements(this.templates, trigger.getValue()));
    }
  }

  /**
   * Reloads the configuration from source. This function might be called if the configuration file has changed in a
   * running system
   *
   * @param configRoot CobiGen configuration root path
   * @throws InvalidConfigurationException thrown if the {@link File} is not valid with respect to the template-set.xsd
   */
  public void reloadConfigurationFromFile(Path configRoot) throws InvalidConfigurationException {

    readConfiguration(configRoot);
  }

  /**
   * @return Trigger from wrapped templateSetConfiguration
   */
  public List<com.devonfw.cobigen.impl.config.entity.io.Trigger> getTrigger() {

    return this.templateSetConfiguration.getTrigger();
  }

  /**
   * @return the list of the template set files
   */
  public List<Path> getTemplateSetFiles() {

    return this.templateSetFiles;
  }

  /**
   * @return s the map of the templates
   */
  public Map<String, Template> getTemplates() {

    return this.templates;
  }

  /**
   * Returns a list of maven coordinates for the download of template sets
   *
   * @return maven coordinates
   */
  public List<MavenCoordinate> getMavenCoordinates() {

    return this.mavenCoordinates;
  }

  /**
   * @return configurationProperties
   */
  public ConfigurationProperties getConfigurationProperties() {

    return this.configurationProperties;
  }

  /**
   * @param configurationProperties new value of {@link #getconfigurationProperties}.
   */
  public void setConfigurationProperties(ConfigurationProperties configurationProperties) {

    this.configurationProperties = configurationProperties;
  }

}
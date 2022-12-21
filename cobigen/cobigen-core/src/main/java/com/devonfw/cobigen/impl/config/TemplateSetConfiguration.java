package com.devonfw.cobigen.impl.config;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.MavenCoordinate;
import com.devonfw.cobigen.impl.config.entity.Increment;
import com.devonfw.cobigen.impl.config.entity.Template;
import com.devonfw.cobigen.impl.config.entity.TemplateFolder;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.reader.ContextConfigurationReader;
import com.devonfw.cobigen.impl.config.reader.TemplateSetConfigurationReader;
import com.devonfw.cobigen.impl.config.reader.TemplatesConfigurationReader;
import com.google.common.collect.Maps;

/**
 * This is the readable and persistent representation of the automatically generated TemplateSetConfiguration file
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

  /**
   * @return increments
   */
  public Map<String, Increment> getIncrements() {

    return this.increments;
  }

  /** The automatically generated templateSetConfiguration this class wraps */
  private com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration templateSetConfiguration;

  /** The reader to read the template-set.xml files */
  private TemplateSetConfigurationReader templateSetConfigurationReader;

  /**
   * The {@link TemplatesConfigurationReader} initially parsed by the template-set.xml (required to access template
   * specific functionalities)
   */
  private TemplatesConfigurationReader templatesConfigurationReader;

  /**
   * The {@link ContextConfigurationReader} initially parsed by the template-set.xml (required to access context.xml
   * specific functionalities
   */
  private ContextConfigurationReader contextConfigurationReader;

  /** Paths of the template set configuration files */
  private List<Path> templateSetFiles = new ArrayList<>();

  /** Root of the configuration */
  private Path configRoot;

  private ConfigurationHolder configurationHolder;

  /**
   * Map of the root template folders distinguished by their trigger ID
   */
  private Map<String, Path> rootTemplateFolders;

  /**
   * The constructor.
   *
   * @param configurationPath CobiGen configuration root path
   */
  public TemplateSetConfiguration(Path configurationPath) {

    this.triggers = Maps.newHashMap();
    this.templates = Maps.newHashMap();
    this.rootTemplateFolders = Maps.newHashMap();
    this.configRoot = configurationPath;
    readConfiguration(configurationPath);
  }

  /**
   * Reads the configuration from the given path
   *
   * @param configurationPath CobiGen configuration root path
   * @throws InvalidConfigurationException thrown if the {@link File} is not valid with respect to the context.xsd
   */
  public void readConfiguration(Path configurationPath) throws InvalidConfigurationException {

    if (this.templateSetConfigurationReader == null) {
      this.templateSetConfigurationReader = new TemplateSetConfigurationReader(configurationPath);
    }

    List<Path> templateSetFiles = this.templateSetConfigurationReader.getTemplateSetConfigurationPaths();

    this.increments = new HashMap<>();
    for (Path templateSetFile : templateSetFiles) {
      // TODO: Fix this WIP block
      // this.templateSetConfigurationReader.templateSetFile = templateSetFile;
      this.templateSetConfigurationReader.readConfiguration(templateSetFile);
      TemplateFolder templateFolder = this.templateSetConfigurationReader.getRootTemplateFolder();
      TemplatesConfigurationReader templatesReader = this.templateSetConfigurationReader
          .getTemplatesConfigurationReader();
      ContextConfigurationReader contextReader = this.templateSetConfigurationReader.getContextConfigurationReader();
      Map<String, Trigger> trigger = contextReader.loadTriggers();
      this.rootTemplateFolders.put(trigger.get(trigger.keySet().toArray()[0]).getId(), templateFolder.getPath());
      this.configRoot = configurationPath;
      this.triggers.putAll(trigger);
      // this.templates.putAll(getTemplates());
      Map<String, Template> templates = templatesReader.loadTemplates(trigger.get(trigger.keySet().toArray()[0]));
      this.templates.putAll(templates);
      this.increments.putAll(this.templateSetConfigurationReader.getTemplatesConfigurationReader()
          .loadIncrements(templates, trigger.get(trigger.keySet().toArray()[0])));
    }
    // For every trigger put all increments depended on that trigger into the local increments hash map
    // for (Entry<String, Trigger> trigger : this.triggers.entrySet()) {
    // this.increments.putAll(this.templateSetConfigurationReader.getTemplatesConfigurationReader()
    // .loadIncrements(this.templates, trigger.getValue()));
    // }
  }

  /**
   * @return templateSetConfigurationReader
   */
  public TemplateSetConfigurationReader getTemplateSetConfigurationReader() {

    return this.templateSetConfigurationReader;
  }

  /**
   * @return rootTemplateFolders
   */
  public Map<String, Path> getRootTemplateFolders() {

    return this.rootTemplateFolders;
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

    return this.templateSetConfiguration.getContextConfiguration().getTrigger();
  }

  /**
   * @return the list of the template set files
   */
  public List<Path> getTemplateSetFiles() {

    return this.templateSetFiles;
  }

  /**
   * Adds template set files to the list of template sets
   *
   * @param templateSets List of template sets
   */
  public void addTemplateSetFiles(List<Path> templateSets) {

    this.templateSetFiles.addAll(templateSets);
  }

  /**
   * @return s the map of triggers
   */
  public Map<String, Trigger> getTriggers() {

    return this.triggers;
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
package com.devonfw.cobigen.impl.config;

import java.io.File;
import java.nio.file.Path;
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
import com.google.common.collect.Lists;
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

  List<TemplatesConfiguration> templatesConfigurations;

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

  /** Root of the configuration */
  private Path configRoot;

  /**
   * Map of the root template folders distinguished by their trigger ID
   */
  private Map<String, Path> rootTemplateFolders;

  /**
   * Map of the utility folders distinguished by their trigger ID
   */
  private Map<String, Path> utilFolders;

  /**
   * @return utilFolders
   */
  public Map<String, Path> getUtilFolders() {

    return this.utilFolders;
  }

  /**
   * The constructor.
   *
   * @param configurationPath CobiGen configuration root path
   */
  public TemplateSetConfiguration(Path configurationPath) {

    this.triggers = Maps.newHashMap();
    this.templates = Maps.newHashMap();
    this.rootTemplateFolders = Maps.newHashMap();
    this.utilFolders = Maps.newHashMap();
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

    List<Path> templateSetsAdapted = this.templateSetConfigurationReader.getTemplateSetConfigurationPathsAdapted();
    List<Path> templateSetsDownloaded = this.templateSetConfigurationReader
        .getTemplateSetConfigurationPathsDownloaded();

    this.increments = new HashMap<>();
    this.templatesConfigurations = Lists.newLinkedList();
    if (!templateSetsAdapted.isEmpty()) {
      for (Path templateSetFile : templateSetsAdapted) {
        initializeTemplateSets(false, configurationPath, templateSetFile);
      }
    }

    if (!templateSetsDownloaded.isEmpty()) {
      for (Path templateSetFile : templateSetsDownloaded) {
        initializeTemplateSets(true, configurationPath, templateSetFile);
      }
    }

  }

  /**
   * Initializes template sets
   *
   * @param isZipFile boolean true if downloaded template-sets need to be processed
   * @param configurationPath CobiGen configuration root path
   * @param templateSetFile Path to template-set xml to be processed
   */
  private void initializeTemplateSets(boolean isZipFile, Path configurationPath, Path templateSetFile) {

    this.templateSetConfigurationReader.readConfiguration(templateSetFile);

    com.devonfw.cobigen.impl.config.entity.io.TemplatesConfiguration templatesConfigurationStatic = this.templateSetConfigurationReader
        .getTemplatesConfiguration();

    com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration contextConfigurationStatic = this.templateSetConfigurationReader
        .getContextConfiguration();

    TemplateFolder templateFolder = this.templateSetConfigurationReader.getRootTemplateFolder();

    TemplatesConfigurationReader templatesConfigurationReader = new TemplatesConfigurationReader(
        templatesConfigurationStatic, templateFolder, null, templateSetFile);

    ContextConfigurationReader contextConfigurationReader = new ContextConfigurationReader(contextConfigurationStatic,
        templateSetFile);

    Map<String, Trigger> trigger = contextConfigurationReader.loadTriggers(true);
    Trigger activeTrigger = trigger.get(trigger.keySet().toArray()[0]);

    if (isZipFile) {
      Map<Path, Path> configLocations = this.templateSetConfigurationReader.getConfigLocations();
      Path jarPath = configLocations.get(templateSetFile);
      this.utilFolders.put(activeTrigger.getId(), jarPath);
    } else {
      this.utilFolders.put(activeTrigger.getId(), getUtilSourceFolder(templateSetFile));
    }

    this.rootTemplateFolders.put(activeTrigger.getId(), templateFolder.getPath());
    this.configRoot = configurationPath;
    this.triggers.putAll(trigger);

    Map<String, Template> loadedTemplates = templatesConfigurationReader.loadTemplates(activeTrigger);
    this.templates.putAll(loadedTemplates);

    Map<String, Increment> loadedIncrements = templatesConfigurationReader.loadIncrements(loadedTemplates,
        activeTrigger);
    this.increments.putAll(templatesConfigurationReader.loadIncrements(loadedTemplates, activeTrigger));
    String templateEngine = templatesConfigurationReader.getTemplateEngine();

    TemplatesConfiguration templatesConfiguration = new TemplatesConfiguration(configurationPath, activeTrigger,
        loadedTemplates, loadedIncrements, templateEngine);

    this.templatesConfigurations.add(templatesConfiguration);
  }

  /**
   * Gets the source folder for utility classes
   *
   * @return the source folder where utility classes are located
   */
  private Path getUtilSourceFolder(Path path) {

    // TODO: replace with proper root template set folder
    return path.getParent().getParent().getParent().getParent();
  }

  /**
   * @return templatesConfigurations
   */
  public List<TemplatesConfiguration> getTemplatesConfigurations() {

    return this.templatesConfigurations;
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
package com.devonfw.cobigen.impl.config;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.reader.ContextConfigurationReader;

/**
 * The {@link ContextConfiguration} is a configuration data wrapper for all information about templates and the target
 * destination for the generated data.
 */
public class ContextConfiguration {

  /**
   * All available {@link Trigger}s
   */
  private Map<String, Trigger> triggers;

  /**
   * Path of the configuration. Might point to a folder or a jar or maybe even something different in future.
   */
  private Path configurationPath;

  /**
   * This is the automatically generated ContextConfiguration
   */
  private com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration contextConfiguration;

  /**
   * The reader to read the context.xml files
   */
  private ContextConfigurationReader contextConfigurationReader;

  /**
   * The {@link ConfigurationHolder}
   */
  private ConfigurationHolder configurationHolder;

  /**
   * Creates a new {@link ContextConfiguration} with the contents initially loaded from the context.xml
   *
   * @param configRoot root path for the configuration of CobiGen
   * @throws InvalidConfigurationException thrown if the {@link File} is not valid with respect to the context.xsd
   */
  public ContextConfiguration(Path configRoot) throws InvalidConfigurationException {

    this.configurationPath = configRoot;
    readConfiguration(configRoot);
  }

  /**
   * Creates a new {@link ContextConfiguration} with the contents initially loaded from the template-set.xml
   *
   * @param contextConfiguration provided by template-set reader
   * @param configRoot root path for the configuration of CobiGen
   * @param configurationHolder the {@link ConfigurationHolder} to initialize
   * @param triggers the map of triggers
   */
  public ContextConfiguration(com.devonfw.cobigen.impl.config.entity.io.ContextConfiguration contextConfiguration,
      Path configRoot, ConfigurationHolder configurationHolder, Map<String, Trigger> triggers) {

    this.configurationHolder = configurationHolder;
    this.configurationPath = configRoot;
    this.contextConfigurationReader = new ContextConfigurationReader(contextConfiguration, configRoot);
    this.triggers = triggers;
  }

  /**
   * Reads the configuration from the given path
   *
   * @param configRoot CobiGen configuration root path
   * @throws InvalidConfigurationException thrown if the {@link File} is not valid with respect to the context.xsd
   */
  private void readConfiguration(Path configRoot) throws InvalidConfigurationException {

    if (this.contextConfigurationReader == null) {
      this.contextConfigurationReader = new ContextConfigurationReader(configRoot);
    }

    this.configurationPath = this.contextConfigurationReader.getContextRoot();
    this.triggers = this.contextConfigurationReader.loadTriggers();

  }

  /**
   * Reloads the configuration from source. This function might be called if the configuration file has changed in a
   * running system
   *
   * @param configRoot CobiGen configuration root path
   * @throws InvalidConfigurationException thrown if the {@link File} is not valid with respect to the context.xsd
   */
  public void reloadConfigurationFromFile(Path configRoot) throws InvalidConfigurationException {

    readConfiguration(configRoot);
  }

  /**
   * Returns all registered {@link Trigger}s
   *
   * @return all registered {@link Trigger}s
   */
  public List<Trigger> getTriggers() {

    return new ArrayList<>(this.triggers.values());
  }

  /**
   * @return the version
   */
  public BigDecimal getVersion() {

    return this.contextConfiguration.getVersion();
  }

  /**
   * Returns the {@link Trigger} with the given id
   *
   * @param id of the {@link Trigger} to be searched
   * @return the {@link Trigger} with the given id or <code>null</code> if there is no
   */
  public Trigger getTrigger(String id) {

    return this.triggers.get(id);
  }

  /**
   * Returns the configuration's {@link Path} represented by this object.
   *
   * @return the {@link Path}
   */
  public Path getConfigurationPath() {

    return this.configurationPath;
  }

  /**
   * Retrieves the directory of the configuration root directory by given trigger ID
   *
   * @param triggerId the trigger id to get the config location for
   * @return the {@link Path} of the config location of the trigger
   */
  public Path retrieveConfigRootByTrigger(String triggerId) {

    if (this.configurationHolder != null) {
      Map<String, Path> rootTemplateFolders = this.configurationHolder.getTemplateSetConfiguration()
          .getRootTemplateFolders();
      Path rootTemplateFolder = rootTemplateFolders.get(triggerId);
      return rootTemplateFolder;
    }

    return this.configurationPath;
  }

  /**
   * Retrieves the utility folder from the given trigger id
   *
   * @param triggerId String of triggerId to search for
   * @return Path to utility folder
   */
  public Path retrieveTemplateSetUtilsLocationByTrigger(String triggerId) {

    if (this.configurationHolder != null) {
      Map<String, Path> utilityFolders = this.configurationHolder.getTemplateSetConfiguration().getUtilFolders();
      Path utilityFolder = utilityFolders.get(triggerId);
      return utilityFolder;
    }
    return this.configurationPath;
  }

}

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
import com.devonfw.cobigen.impl.config.entity.io.TemplateSetConfiguration;
import com.devonfw.cobigen.impl.config.reader.TemplateSetConfigurationReader;
import com.google.common.collect.Maps;

/**
 * mdukhan: This Class is used to set specific properties if not found, or save them if correctly found. These
 * properties are groupIds, allowSnapshots and hideTemplates. khucklen: This is our stable and structured representation
 * of the automatically generated TemplateSetConfiguration and should not be confused with the XML-generated
 * {@link TemplateSetConfiguration}.
 */
public class TemplateSetConfigurationDecorator {

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(TemplateSetConfigurationDecorator.class);

  /** variable for template-set artifacts */
  private List<String> groupIds;

  /** allow snapshots of template-sets */
  private boolean allowSnapshots;

  /** variable to hide very specific template sets or versions of template sets */
  private List<MavenCoordinate> hideTemplates;

  /** All available {@link Trigger}s */
  private Map<String, Trigger> triggers;

  /** All available {@Link Template}s */
  private Map<String, Template> templates;

  /** All available {@Link Increment} */
  private Map<String, Increment> increments;

  /** The templateSetConfiguration this decorator wraps */
  private TemplateSetConfiguration templateSetConfiguration;

  /** The reader to read the template-set.xml files */
  public TemplateSetConfigurationReader templateSetConfigurationReader;

  /** Paths of the template set configuration files */
  public List<Path> templateSetFiles = new ArrayList<>();

  /** Root of the configuration */
  public Path configRoot;

  /** List of mavenCoordinates for the template sets that should be installed at cobigen startup */
  private List<MavenCoordinate> mavenCoordinates;

  /**
   * The constructor. This constructor is used, when the specific properties aren't needed
   *
   * @param configRoot Root of the configuration
   */
  public TemplateSetConfigurationDecorator(Path configRoot) {

    this.templateSetConfiguration = new TemplateSetConfiguration();
    this.triggers = Maps.newHashMap();
    this.templates = Maps.newHashMap();
    readConfiguration(configRoot);

  }

  /**
   * The constructor. load properties from a given source -
   *
   * @param groupIds variable for template-set artifacts
   * @param allowSnapshots allow snapshots of template-sets
   * @param hideTemplates variable to hide very specific template sets or versions of template sets
   * @param configRoot Root of the configuration
   */
  public TemplateSetConfigurationDecorator(List<String> groupIds, boolean allowSnapshots,
      List<MavenCoordinate> hideTemplates, Path configRoot) {

    this(configRoot);
    this.groupIds = groupIds;
    this.allowSnapshots = allowSnapshots;
    this.hideTemplates = hideTemplates;

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
   * return a list of the saved groupIds
   *
   * @return groupIds
   */
  public List<String> getGroupIds() {

    return this.groupIds;
  }

  /**
   * set a list of the groupIds from a source
   *
   * @param groupIds new value of groupIds}.
   */
  public void setGroupIds(List<String> groupIds) {

    this.groupIds = groupIds;
  }

  /**
   * return a boolean which states if specific Snapshots should be allowed.
   *
   * @return allowSnapshots
   */
  public boolean isAllowSnapshots() {

    return this.allowSnapshots;
  }

  /**
   * set a value on the snapshot
   *
   * @param allowSnapshots new value of getallowSnapshots}.
   */
  public void setAllowSnapshots(boolean allowSnapshots) {

    this.allowSnapshots = allowSnapshots;
  }

  /**
   * return a list of the saved templates to be hidden
   *
   * @return hideTemplates
   */
  public List<MavenCoordinate> getHideTemplates() {

    return this.hideTemplates;
  }

  /**
   * set a list of the HideTemplate from a source
   *
   * @param hideTemplates new value of gethideTemplates}.
   */
  public void setHideTemplates(List<MavenCoordinate> hideTemplates) {

    this.hideTemplates = hideTemplates;
  }

  /**
   * @return the map of the templates
   */
  public Map<String, Template> getTemplates() {

    return this.templates;
  }

  /**
   * @return @param mavenCoordinates
   */
  public List<MavenCoordinate> getMavenCoordinates() {

    // TODO Auto-generated method stub
    return this.mavenCoordinates;
  }

}
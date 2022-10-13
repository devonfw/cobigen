package com.devonfw.cobigen.impl.config;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayList;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.TemplatesJarConstants;
import com.devonfw.cobigen.api.util.MavenCoordinate;

/**

 * This Class is used to set specific properties if not found, or save them if correctly found. These properties are
 * groupIds, allowSnapshots and hideTemplates.
 */
public class TemplateSetConfiguration {

  /** Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(TemplateSetConfiguration.class);

  /** variable for template-set artifacts */
  private List<String> groupIds;

  /** allow snapshots of template-sets */
  private boolean allowSnapshots;

  /** variable to hide very specific template sets or versions of template sets */
  private List<MavenCoordinate> hideTemplates;

  /** List of mavenCoordinates for the template sets that should be installed at cobigen startup */
  private List<MavenCoordinate> mavenCoordinates;

  /** All available {@link Trigger}s */
  private Map<String, Trigger> triggers;

  /** All available {@Link Template}s */
  private Map<String, Template> templates;

  /** All available {@Link Increment} */
  private Map<String, Increment> increments;

  /** The templateSetConfiguration this decorator wraps */
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
    // this.templateSetConfiguration = new TemplateSetConfiguration();
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
  public TemplateSetConfiguration(List<String> groupIds, boolean allowSnapshots, List<MavenCoordinate> hideTemplates,
      Path configRoot) {

    this(configRoot);
    this.groupIds = groupIds;
    this.allowSnapshots = allowSnapshots;
    this.hideTemplates = convertToMavenCoordinates(hideTemplates);
    this.mavenCoordinates = convertToMavenCoordinates(mavenCoordinates);

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
   * Takes a string with multiple maven coordinates separates them and checks if they meet the maven naming conventions
   * and are therefore valid.
   *
   * @param mavenCoordinatesString a String that contains maven coordinates
   * @return List with {@link MavenCoordinate}
   */
  private List<MavenCoordinate> convertToMavenCoordinates(List<String> mavenCoordinatesString) {

    List<MavenCoordinate> result = new ArrayList<>();
    for (String mavenCoordinate : mavenCoordinatesString) {
      mavenCoordinate = mavenCoordinate.trim();
      if (!mavenCoordinate.matches(TemplatesJarConstants.MAVEN_COORDINATES_CHECK)) {
        LOG.warn("configuration key:" + mavenCoordinate + " in .cobigen for "
            + "template-sets.installed or template-sets.hide doesnt match the specification and could not be used");
      } else {
        String[] split = mavenCoordinate.split(":");
        String groupID = split[0];
        String artifactID = split[1];
        String version = split.length > 2 ? split[2] : null;
        result.add(new MavenCoordinate(groupID, artifactID, version));
      }
    }
    return result;
  }

  /**
   * Returns a list of the saved groupIds
   *
   * @return groupIds
   */
  public List<String> getGroupIds() {

    return this.groupIds;
  }

  /**
   * Sets a list of the groupIds from a source
   *
   * @param groupIds new value of groupIds}.
   */
  public void setGroupIds(List<String> groupIds) {

    this.groupIds = groupIds;
  }

  /**
   * Returns a boolean which states if specific Snapshots should be allowed.
   *
   * @return allowSnapshots
   */
  public boolean isAllowSnapshots() {

    return this.allowSnapshots;
  }

  /**
   * Sets a value on the snapshot
   *
   * @param allowSnapshots new value of getallowSnapshots}.
   */
  public void setAllowSnapshots(boolean allowSnapshots) {

    this.allowSnapshots = allowSnapshots;
  }

  /**
   * Returns a list of the saved templates to be hidden
   *
   * @return hideTemplates
   */
  public List<MavenCoordinate> getHideTemplates() {

    return this.hideTemplates;
  }

  /**
   * Sets a list of the HideTemplate from a source
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
   * Returns a list of maven coordinates for the download of template sets
   *
   * @return maven coordinates
   */
  public List<MavenCoordinate> getMavenCoordinates() {

    return this.mavenCoordinates;
  }

}
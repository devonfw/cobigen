package com.devonfw.cobigen.impl.config;

import java.util.List;

import com.devonfw.cobigen.api.util.MavenCoordinate;

/**
 * Data wrapper for configuration Properties. The properties are groupIds, hideTemplates, allowSnapshots and
 * templateSetsInstalled
 */
public class ConfigurationProperties {

  /** variable for template-set artifacts */
  private final List<String> groupIds;

  /** allow snapshots of template-sets */
  private final boolean allowSnapshots;

  /** List of mavenCoordinates for the template sets that should be installed at CobiGen startup */
  private final List<MavenCoordinate> templateSetsInstalled;

  /** variable to hide very specific template sets or versions of template sets */
  private final List<MavenCoordinate> hideTemplateSets;

  /**
   * The constructor. Loads properties from a given source
   *
   * @param groupIds groupID from key template-sets.groupIds
   * @param allowSnapshots from key template-sets.allow-snapshot
   * @param hideTemplates from key template-set.hide
   * @param templateSetsInstalled list of mavenCoordinate that define the templates sets that should be installed
   */
  public ConfigurationProperties(List<String> groupIds, boolean allowSnapshots, List<MavenCoordinate> hideTemplates,
      List<MavenCoordinate> templateSetsInstalled) {

    this.groupIds = groupIds;
    this.allowSnapshots = allowSnapshots;
    this.hideTemplateSets = hideTemplates;
    this.templateSetsInstalled = templateSetsInstalled;
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
   * Returns a boolean which states if specific Snapshots should be allowed.
   *
   * @return allowSnapshots
   */
  public boolean isAllowSnapshots() {

    return this.allowSnapshots;
  }

  /**
   * Returns a list of the saved templates to be hidden
   *
   * @return hideTemplateSets
   */
  public List<MavenCoordinate> getHideTemplateSets() {

    return this.hideTemplateSets;
  }

  /**
   * Returns a list of maven coordinates for the download of template sets
   *
   * @return maven coordinates
   */
  public List<MavenCoordinate> getTemplateSetsInstalled() {

    return this.templateSetsInstalled;
  }

}

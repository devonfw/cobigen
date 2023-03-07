package com.devonfw.cobigen.impl.config;

import java.util.List;

import com.devonfw.cobigen.api.util.mavencoordinate.MavenCoordinate;

/**
 * Data wrapper for configuration Properties. The properties are groupIds, hideTemplates and allowSnapshots
 *
 */
public class ConfigurationProperties {

  /** variable for template-set artifacts */
  private List<String> groupIds;

  /** allow snapshots of template-sets */
  private boolean allowSnapshots;

  /** List of mavenCoordinates for the template sets that should be installed at cobigen startup */
  private List<MavenCoordinate> templatesInstalled;

  /** variable to hide very specific template sets or versions of template sets */
  private List<MavenCoordinate> hideTemplates;

  /**
   * The constructor. load properties from a given source
   *
   * @param groupIds groupID from key template-sets.groupIds
   * @param allowSnapshots from key template-sets.allow-snapshot
   * @param hideTemplates from key template-set.hide
   * @param mavenCoordinates list of mavenCoordinate that define the templates that should be installed
   */
  public ConfigurationProperties(List<String> groupIds, boolean allowSnapshots, List<MavenCoordinate> hideTemplates,
      List<MavenCoordinate> mavenCoordinates) {

    this.groupIds = groupIds;
    this.allowSnapshots = allowSnapshots;
    this.hideTemplates = hideTemplates;
    this.templatesInstalled = mavenCoordinates;
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
   * @param groupIds new value of {@link #getgroupIds}.
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
   * @param allowSnapshots new value of {@link #getallowSnapshots}.
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
   * @param hideTemplates new value of {@link #gethideTemplates}.
   */
  public void setHideTemplates(List<MavenCoordinate> hideTemplates) {

    this.hideTemplates = hideTemplates;
  }

  /**
   * Returns a list of maven coordinates for the download of template sets
   *
   * @return maven coordinates
   */
  public List<MavenCoordinate> getMavenCoordinates() {

    return this.templatesInstalled;
  }

  /**
   * Set a list of templates that should be installed at startup
   *
   * @param mavenCoordinates new value of {@link #getmavenCoordinates}.
   */
  public void setMavenCoordinates(List<MavenCoordinate> mavenCoordinates) {

    this.templatesInstalled = mavenCoordinates;
  }

}

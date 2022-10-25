package com.devonfw.cobigen.impl.config;

import java.util.List;

import com.devonfw.cobigen.api.util.MavenCoordinate;

/**
 * Data wrapper for configuration Properties. The properties are groupIds, hideTemplates and allowSnapshots
 *
 */
public class ConfigurationProperties {

  /** variable for template-set artifacts */
  private List<String> groupIds;

  /** allow snapshots of template-sets */
  private boolean allowSnapshots;

  /** variable to hide very specific template sets or versions of template sets */
  private List<MavenCoordinate> hideTemplates;

  public ConfigurationProperties(List<String> groupIds, boolean allowSnapshots, List<MavenCoordinate> hideTemplates) {

    this.groupIds = groupIds;
    this.allowSnapshots = allowSnapshots;
    this.hideTemplates = hideTemplates;
  }

  /**
   * @return groupIds
   */
  public List<String> getGroupIds() {

    return this.groupIds;
  }

  /**
   * @param groupIds new value of {@link #getgroupIds}.
   */
  public void setGroupIds(List<String> groupIds) {

    this.groupIds = groupIds;
  }

  /**
   * @return allowSnapshots
   */
  public boolean isAllowSnapshots() {

    return this.allowSnapshots;
  }

  /**
   * @param allowSnapshots new value of {@link #getallowSnapshots}.
   */
  public void setAllowSnapshots(boolean allowSnapshots) {

    this.allowSnapshots = allowSnapshots;
  }

  /**
   * @return hideTemplates
   */
  public List<MavenCoordinate> getHideTemplates() {

    return this.hideTemplates;
  }

  /**
   * @param hideTemplates new value of {@link #gethideTemplates}.
   */
  public void setHideTemplates(List<MavenCoordinate> hideTemplates) {

    this.hideTemplates = hideTemplates;
  }

}

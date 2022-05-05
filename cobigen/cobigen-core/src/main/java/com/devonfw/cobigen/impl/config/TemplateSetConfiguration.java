package com.devonfw.cobigen.impl.config;

import java.util.List;

/**
 * mdukhan This Class is used to set specific properties if not found, or save them if correctly found. These properties
 * are groupIds, allowSnapshots and hideTemplates.
 */
public class TemplateSetConfiguration {

  /** variable for template-set artifacts */
  private List<String> groupIds;

  /** allow snapshots of template-sets */
  private boolean allowSnapshots;

  /** variable to hide very specific template sets or versions of template sets */
  private List<String> hideTemplates;

  /**
   * The constructor. load properties from a given source
   *
   * @param groupIds
   * @param allowSnapshots
   * @param hideTemplates
   */
  public TemplateSetConfiguration(List<String> groupIds, boolean allowSnapshots, List<String> hideTemplates) {

    super();
    this.groupIds = groupIds;
    this.allowSnapshots = allowSnapshots;
    this.hideTemplates = hideTemplates;
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
   * @param groupIds new value of {@link #getgroupIds}.
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
   * @param allowSnapshots new value of {@link #getallowSnapshots}.
   */
  public void setAllowSnapshots(boolean allowSnapshots) {

    this.allowSnapshots = allowSnapshots;
  }

  /**
   * return a list of the saved templates to be hidden
   *
   * @return hideTemplates
   */
  public List<String> getHideTemplates() {

    return this.hideTemplates;
  }

  /**
   * set a list of the HideTemplate from a source
   *
   * @param hideTemplates new value of {@link #gethideTemplates}.
   */
  public void setHideTemplates(List<String> hideTemplates) {

    this.hideTemplates = hideTemplates;
  }

}
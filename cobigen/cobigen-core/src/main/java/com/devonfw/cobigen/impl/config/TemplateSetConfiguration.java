package com.devonfw.cobigen.impl.config;

import java.util.List;

/**
 * mdukhan This Class is used to save specific properties
 *
 */
public class TemplateSetConfiguration {

  private List<String> groupIds;

  private boolean allowSnapshots;

  private boolean disableLookup;

  private String hideTemplates;

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
   * @return disableLookup
   */
  public boolean isDisableLookup() {

    return this.disableLookup;
  }

  /**
   * @param disableLookup new value of {@link #getdisableLookup}.
   */
  public void setDisableLookup(boolean disableLookup) {

    this.disableLookup = disableLookup;
  }

  /**
   * @return hideTemplates
   */
  public String getHideTemplates() {

    return this.hideTemplates;
  }

  /**
   * @param hideTemplates new value of {@link #gethideTemplates}.
   */
  public void setHideTemplates(String hideTemplates) {

    this.hideTemplates = hideTemplates;
  }

}
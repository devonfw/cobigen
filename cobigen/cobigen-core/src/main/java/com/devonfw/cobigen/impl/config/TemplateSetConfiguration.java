package com.devonfw.cobigen.impl.config;

import java.util.List;

/**
 * TODO mdukhan This Class is used to save config.properties file inside .cobigen used
 * in @ConfigurationFinder @readTemplateSetConfiguration
 *
 */
public class TemplateSetConfiguration {

  private List<String> groupIds;

  private boolean allowSnapshots;

  private boolean disableLookup;

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

}
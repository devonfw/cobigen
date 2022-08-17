package com.devonfw.cobigen.impl.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.TemplatesJarConstants;
import com.devonfw.cobigen.api.util.MavenCoordinate;

/**
 * mdukhan This Class is used to set specific properties if not found, or save them if correctly found. These properties
 * are groupIds, allowSnapshots and hideTemplates.
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

  /**
   * The constructor. load properties from a given source
   *
   * @param groupIds groupID from key template-sets.groupIds
   * @param allowSnapshots from key template-sets.allow-snapshot
   * @param hideTemplates from key template-set.hide
   * @param mavenCoordinates list of mavenCoordinate that define the templates that should be installed
   */
  public TemplateSetConfiguration(List<String> groupIds, boolean allowSnapshots, List<String> hideTemplates,
      List<String> mavenCoordinates) {

    super();
    this.groupIds = groupIds;
    this.allowSnapshots = allowSnapshots;
    this.hideTemplates = checkandCovertToMavenCoordinates(hideTemplates);
    this.mavenCoordinates = checkandCovertToMavenCoordinates(mavenCoordinates);
  }

  private List<MavenCoordinate> checkandCovertToMavenCoordinates(List<String> mavenCoordinates) {

    List<MavenCoordinate> result = new ArrayList<>();
    for (String mcoordinate : mavenCoordinates) {
      mcoordinate = mcoordinate.trim();
      if (!mcoordinate.matches(TemplatesJarConstants.MAVEN_COORDINATES_CHECK)) {
        LOG.warn("configuration key:" + mcoordinate + " in .cobigen for "
            + "template-sets.installed or template-sets.hide doesnt match the specification and could not be used");
      } else {
        String[] split = mcoordinate.split(":");
        String groupID = split[0];
        String artifactID = split[1];
        String version = split.length > 2 ? split[2] : null;
        result.add(new MavenCoordinate(groupID, artifactID, version));
      }
    }
    return result;
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
  public List<MavenCoordinate> getHideTemplates() {

    return this.hideTemplates;
  }

  /**
   * set a list of the HideTemplate from a source
   *
   * @param hideTemplates new value of {@link #gethideTemplates}.
   */
  public void setHideTemplates(List<MavenCoordinate> hideTemplates) {

    this.hideTemplates = hideTemplates;
  }

  /**
   * returns a list of maven coordinates for the download of template sets
   *
   * @return maven coordinates
   */
  public List<MavenCoordinate> getMavenCoordinates() {

    return this.mavenCoordinates;
  }

}
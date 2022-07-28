package com.devonfw.cobigen.api.util;

/**
 * TODO
 */
public class MavenCoordinate {

  private String groupID;

  private String artifactID;

  private String version;

  public MavenCoordinate(String groupID, String artifactID, String version) {

    this.groupID = groupID;
    this.artifactID = artifactID;
    this.version = version;
  }

  public String getArtifactID() {

    return this.artifactID;
  }

  public String getGroupID() {

    return this.groupID;
  }

  public String getVersion() {

    return this.version;
  }

}

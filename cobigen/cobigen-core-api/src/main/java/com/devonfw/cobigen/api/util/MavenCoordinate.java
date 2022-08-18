package com.devonfw.cobigen.api.util;

/**
 * This MavenCoordinate class is just a dataholder with maven coordinates.
 */
public class MavenCoordinate {

  /**
   * the groupID of the maven artifact
   */
  private String groupID;

  /**
   * the artifactID of the maven artifact
   */
  private String artifactID;

  /**
   * the version of the maven artifact
   */
  private String version;

  /**
   * Creates a new {@link MavenCoordinate} object with the given properties
   *
   * @param groupID a {@link String} with the groupID of the maven artifact
   * @param artifactID {@link String} with the artifactID of the maven artifact
   * @param version {@link String} with the version of the maven artifact
   */
  public MavenCoordinate(String groupID, String artifactID, String version) {

    this.groupID = groupID;
    this.artifactID = artifactID;
    this.version = version;
  }

  /**
   * Returns the value of the artifactID
   *
   * @return {@link String} artifactID
   */
  public String getArtifactID() {

    return this.artifactID;
  }

  /**
   * Returns the value of the groupID
   *
   * @return {@link String} groupID
   */
  public String getGroupID() {

    return this.groupID;
  }

  /**
   * Returns the value of the version
   *
   * @return {@link String} version
   */
  public String getVersion() {

    return this.version;
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    MavenCoordinate mavenCoordinate = (MavenCoordinate) obj;

    if (this.artifactID != mavenCoordinate.getArtifactID()) {
      return false;
    } else if (this.groupID != mavenCoordinate.getGroupID()) {
      return false;
    } else if (this.version != mavenCoordinate.getVersion()) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {

    return this.artifactID.hashCode() + this.groupID.hashCode() + this.version.hashCode();
  }

}

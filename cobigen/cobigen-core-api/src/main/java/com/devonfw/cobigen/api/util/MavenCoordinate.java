package com.devonfw.cobigen.api.util;

/**
 * This MavenCoordinate class is just a dataholder with maven coordinates.
 */
public class MavenCoordinate {

  /**
   * the groupId of the maven artifact
   */
  private String groupId;

  /**
   * the artifactId of the maven artifact
   */
  private String artifactId;

  /**
   * the version of the maven artifact
   */
  private String version;

  /**
   * Creates a new {@link MavenCoordinate} object with the given properties
   *
   * @param groupId a {@link String} with the groupId of the maven artifact
   * @param artifactId {@link String} with the artifactId of the maven artifact
   * @param version {@link String} with the version of the maven artifact
   */
  public MavenCoordinate(String groupId, String artifactId, String version) {

    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }

  /**
   * Returns the value of the artifactID
   *
   * @return {@link String} artifactID
   */
  public String getArtifactId() {

    return this.artifactId;
  }

  /**
   * Returns the value of the groupID
   *
   * @return {@link String} groupID
   */
  public String getGroupId() {

    return this.groupId;
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

    if (this.artifactId != mavenCoordinate.getArtifactId()) {
      return false;
    } else if (this.groupId != mavenCoordinate.getGroupId()) {
      return false;
    } else if (this.version != mavenCoordinate.getVersion()) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {

    return this.artifactId.hashCode() + this.groupId.hashCode() + this.version.hashCode();
  }

}

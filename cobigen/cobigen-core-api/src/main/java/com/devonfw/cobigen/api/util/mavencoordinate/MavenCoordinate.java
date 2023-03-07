package com.devonfw.cobigen.api.util.mavencoordinate;

import java.util.Objects;

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
  public int hashCode() {

    return Objects.hash(this.artifactId, this.groupId, this.version);
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (!(obj instanceof MavenCoordinate)) {
      return false;
    }
    MavenCoordinate other = (MavenCoordinate) obj;
    return Objects.equals(this.artifactId, other.artifactId) && Objects.equals(this.groupId, other.groupId)
        && Objects.equals(this.version, other.version);
  }

}

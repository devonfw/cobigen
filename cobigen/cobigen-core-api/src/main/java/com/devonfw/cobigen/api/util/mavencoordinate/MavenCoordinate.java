package com.devonfw.cobigen.api.util.mavencoordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.TemplatesJarConstants;

/**
 * This MavenCoordinate class is just a dataholder with maven coordinates.
 */
public class MavenCoordinate {
  /**
   * Constants needed for handling the template set jars
   */
  public static class TemplateSetsJarConstants {

    /**
     * Used to denote the first regex group index of the artifactId
     */
    public static final int ARTIFACT_ID_REGEX_GROUP = 1;

    /**
     * Used to denote the second regex group index of the version
     */
    public static final int VERSION_REGEX_GROUP = 2;

    //@formatter:off
    /**
     * Pattern to match artifact id and versions for template set jars:
     * Group 1: artifact id
     * Group 2: version
     * Group 3: snapshot or latest
     */
    //@formatter:on
    public static final String MAVEN_COORDINATE_JAR_PATTERN = "([a-zA-Z-]+)-([\\d.]+)(-SNAPSHOT|-LATEST)?\\.jar";

    //@formatter:off
    /**
     * Pattern to match artifact id and versions for template set source jars:
     * Group 1: artifact id
     * Group 2: version
     * Group 3: snapshot or latest
     */
    //@formatter:on
    public static final String MAVEN_COORDINATE_SOURCES_JAR_PATTERN = "([a-zA-Z-]+)-([\\d.]+)(-sources)\\.jar";

  }

  private static final Logger LOG = LoggerFactory.getLogger(MavenCoordinate.class);

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

  /**
   * Takes a string with multiple maven coordinates separates them and checks if they meet the maven naming conventions
   * and are therefore valid.
   *
   * @param mavenCoordinatesString a String that contains maven coordinates
   * @return List with {@link MavenCoordinate}
   */
  public static List<MavenCoordinate> convertToMavenCoordinates(List<String> mavenCoordinatesString) {

    List<MavenCoordinate> result = new ArrayList<>();
    for (String mavenCoordinate : mavenCoordinatesString) {
      mavenCoordinate = mavenCoordinate.trim();
      if (!mavenCoordinate.matches(TemplatesJarConstants.MAVEN_COORDINATES_CHECK)) {
        LOG.warn("configuration key:" + mavenCoordinate + " in .cobigen for "
            + "template-sets.installed or template-sets.hide doesnt match the specification and could not be used");
      } else {
        String[] split = mavenCoordinate.split(":");
        String groupID = split[0];
        String artifactID = split[1];
        String version = split.length > 2 ? split[2] : null;
        result.add(new MavenCoordinate(groupID, artifactID, version));
      }
    }
    return result;
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

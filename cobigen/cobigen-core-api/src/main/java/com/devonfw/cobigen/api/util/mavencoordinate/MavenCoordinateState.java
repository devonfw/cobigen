package com.devonfw.cobigen.api.util.mavencoordinate;

import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.devonfw.cobigen.api.constants.TemplateSetsJarConstants;

/**
 * This MavenCoordinateState extends the dataholder MavenCoordinate to process information about a MavenCoordinate
 * across Cobigen.
 */
public class MavenCoordinateState extends MavenCoordinate {

  /**
   * source status of MavenCoordinate
   */
  private boolean isSource;

  /**
   * present status of MavenCoordinate
   */
  private boolean isPresent;

  /**
   * adapted status of MavenCoordinate
   */
  private boolean isAdapted;

  /**
   * the local path to a MavenCoordinate
   */
  private Path mavenCoordinateLocalPath;

  /**
   * status if this MavenCoordinate should be adapted during the adapted process
   */
  private boolean toBeAdapted;

  /**
   * Whether the object is a valid MavenCoordinate or not
   */
  private boolean isValidMavenCoordinate;

  /**
   * The default constructor. By default all MavenCoordinates are neither source, present nor adapted.
   *
   * @param groupId the groupId of the maven artifact
   * @param artifactId the artifactId of the maven artifact
   * @param version the version of the maven artifact
   */
  public MavenCoordinateState(String groupId, String artifactId, String version) {

    super(groupId, artifactId, version);
    this.isSource = false;
    this.isPresent = false;
    this.isAdapted = false;
    setToBeAdapted(false);
    setValidMavenCoordinate(false);
  }

  /**
   * The constructor with a local path to a MavenCoordinate. By default all MavenCoordinates are neither source, present
   * nor adapted.
   *
   * @param mavenCoordinatePath the local path to a MavenCoordinate
   * @param groupId the groupId of the maven artifact
   * @param artifactId the artifactId of the maven artifact
   * @param version the version of the maven artifact
   */
  public MavenCoordinateState(Path mavenCoordinatePath, String groupId, String artifactId, String version) {

    super(groupId, artifactId, version);
    this.mavenCoordinateLocalPath = mavenCoordinatePath;
    this.isSource = false;
    this.isPresent = false;
    this.isAdapted = false;
    setToBeAdapted(false);
    setValidMavenCoordinate(false);

  }

  /**
   * The constructor with a local path to a MavenCoordinate. By default all MavenCoordinates are neither present nor
   * adapted.
   *
   * @param mavenCoordinatePath the local path to a MavenCoordinate
   * @param groupId the groupId of the maven artifact
   * @param artifactId the artifactId of the maven artifact
   * @param version the version of the maven artifact
   * @param isSource whether the MavenCoordinate describes a source or not
   */
  public MavenCoordinateState(Path mavenCoordinatePath, String groupId, String artifactId, String version,
      boolean isSource) {

    super(groupId, artifactId, version);

    this.mavenCoordinateLocalPath = mavenCoordinatePath;
    this.isSource = isSource;
    this.isPresent = false; // this field might be obsolete
    this.isAdapted = false;
    setToBeAdapted(false);
    setValidMavenCoordinate(false);

  }

  /**
   * @return whether the MavenCoordinate is a source or not
   */
  public boolean isSource() {

    return this.isSource;
  }

  /**
   * @return get the local path to a MavenCoordinate
   */
  public Path getMavenCoordinateLocalPath() {

    return this.mavenCoordinateLocalPath;
  }

  /**
   * @return whether the MavenCoordinate is present or not
   */
  public boolean isPresent() {

    return this.isPresent;
  }

  /**
   * @return whether the MavenCoordinate is adapted or not
   */
  public boolean isAdapted() {

    return this.isAdapted;
  }

  /**
   * @param mavenCoordinateLocalPath the local path to a MavenCoordinate
   */
  public void setMavenCoordinateLocalPath(Path mavenCoordinateLocalPath) {

    this.mavenCoordinateLocalPath = mavenCoordinateLocalPath;
  }

  /**
   * @param isAdapted updates the adapted state of MavenCoordinate
   */
  public void setAdapted(boolean isAdapted) {

    this.isAdapted = isAdapted;
  }

  /**
   * @param isPresent updates the present state of MavenCoordinate
   */
  public void setPresent(boolean isPresent) {

    this.isPresent = isPresent;
  }

  /**
   * @return isValidMavenCoordinate
   */
  public boolean isValidMavenCoordinate() {

    return this.isValidMavenCoordinate;
  }

  /**
   * @param isValidMavenCoordinate new value of {@link #isValidMavenCoordinate()}.
   */
  public void setValidMavenCoordinate(boolean isValidMavenCoordinate) {

    this.isValidMavenCoordinate = isValidMavenCoordinate;
  }

  /**
   * @return returns a string that reflects a directory name of the given MavenCoordinate
   */
  public String getRealDirectoryName() {

    String name = getArtifactId() + "-" + getVersion();
    return isSource() ? name + "-sources" : name;
  }

  /**
   * @return an identifier for maps
   */
  public String getGroupArtifactVersion() {

    return getGroupId() + ":" + getArtifactId() + ":" + getVersion();
  }

  /**
   * @return toBeAdapted
   */
  public boolean isToBeAdapted() {

    return this.toBeAdapted;
  }

  /**
   * @param toBeAdapted new value of {@link #isToBeAdapted()}.
   */
  public void setToBeAdapted(boolean toBeAdapted) {

    this.toBeAdapted = toBeAdapted;
  }

  /**
   * Uses the {@linkplain Objects#equals(Object)} method to enhance the identity of {@linkplain MavenCoordinate} by the
   * {@linkplain #isSource()} attribute.
   *
   */
  @Override
  public boolean equals(Object obj) {

    if (this == obj)
      return true;

    if (!(obj instanceof MavenCoordinateState))
      return false;

    if (!super.equals(obj))
      return false;

    MavenCoordinateState other = (MavenCoordinateState) obj;
    return Objects.equals(this.isSource, other.isSource());

  }

  /**
   * Creates a MavenCoordinateState object that exposes situational information.
   *
   * @param jar the local path to a jar file
   * @param regex the pattern that is supposed to match the path
   * @param isSource whether the jar is meant to be a sources jar or not
   * @return mavenCoordinateState or null when the pattern does not match the given path
   */
  public static MavenCoordinateState createMavenCoordinateState(Path jar, String regex, boolean isSource) {

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(jar.getFileName().toString().strip());

    if (matcher.find()) {
      String artifactID = matcher.group(TemplateSetsJarConstants.ARTIFACT_ID_REGEX_GROUP);
      String version = matcher.group(TemplateSetsJarConstants.VERSION_REGEX_GROUP);
      MavenCoordinateState mavenCoordinateState = new MavenCoordinateState(jar, null, artifactID, version, isSource);
      if (artifactID != null && version != null) {
        mavenCoordinateState.setPresent(true);
        mavenCoordinateState.setValidMavenCoordinate(true);
      }
      return mavenCoordinateState;
    } else {
      return null;
    }

  }

}
package com.devonfw.cobigen.api.util.mavencoordinate;

import java.nio.file.Path;
import java.util.Objects;

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
    this.isPresent = false;
    this.isAdapted = false;
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
   * @return
   */
  public String getGroupArtifactVersion() {

    return getGroupId() + ":" + getArtifactId() + ":" + getVersion();
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

}
package com.devonfw.cobigen.api.util.to.maven;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * Maven search response doc model
 *
 */
@JsonIgnoreProperties(value = { "p", "timestamp", "versionCount", "text" })
class MavenSearchResponseDoc {

  /** id */
  @JsonProperty("id")
  private String id;

  /** group */
  @JsonProperty("g")
  private String group;

  /** artifact */
  @JsonProperty("a")
  private String artifact;

  /** latest version */
  @JsonProperty("latestVersion")
  private String latestVersion;

  /** repository ID */
  @JsonProperty("repositoryId")
  private String repositoryId;

  /** ec (file ending) */
  @JsonProperty("ec")
  private List<String> ec;

  /**
   * @return ec
   */
  @JsonIgnore
  public List<String> getEc() {

    return this.ec;
  }

  /**
   * @return id
   */
  @JsonIgnore
  public String getId() {

    return this.id;
  }

  /**
   * @return group
   */
  @JsonIgnore
  public String getGroup() {

    return this.group;
  }

  /**
   * @return artifact
   */
  @JsonIgnore
  public String getArtifact() {

    return this.artifact;
  }

  /**
   * @return latestVersion
   */
  @JsonIgnore
  public String getLatestVersion() {

    return this.latestVersion;
  }

  /**
   * @return repositoryId
   */
  @JsonIgnore
  public String getRepositoryId() {

    return this.repositoryId;
  }

}

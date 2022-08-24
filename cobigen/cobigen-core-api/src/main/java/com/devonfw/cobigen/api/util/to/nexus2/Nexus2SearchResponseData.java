package com.devonfw.cobigen.api.util.to.nexus2;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * Nexus2 search response data model
 *
 */
@JsonIgnoreProperties(value = { "latestRelease", "latestReleaseRepositoryId", "highlightedFragment" })
class Nexus2SearchResponseData {

  /** groupId */
  @JsonProperty("groupId")
  private String groupId;

  /** artifactId */
  @JsonProperty("artifactId")
  private String artifactId;

  /** version */
  @JsonProperty("version")
  private String version;

  /** artifactHits */
  @JsonProperty("artifactHits")
  public List<Nexus2SearchResponseArtifactHits> artifactHits;

  /**
   * @return groupId
   */
  public String getGroupId() {

    return this.groupId;
  }

  /**
   * @return artifactId
   */
  public String getArtifactId() {

    return this.artifactId;
  }

  /**
   * @return version
   */
  public String getVersion() {

    return this.version;
  }

}

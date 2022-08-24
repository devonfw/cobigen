package com.devonfw.cobigen.api.util.to.nexus2;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * Nexus search response artifacthits model
 *
 */
@JsonIgnoreProperties(value = { "repositoryId" })
class Nexus2SearchResponseArtifactHits {

  /**
   * artifactLinks
   */
  @JsonProperty("artifactLinks")
  public List<Nexus2SearchResponeArtifactLinks> artifactLinks;

}

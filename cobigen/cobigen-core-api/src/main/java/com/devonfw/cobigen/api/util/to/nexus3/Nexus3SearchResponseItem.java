package com.devonfw.cobigen.api.util.to.nexus3;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * Nexus3 search response item model
 *
 */
@JsonIgnoreProperties(value = { "id", "repository", "format", "group", "name", "version" })
class Nexus3SearchResponseItem {

  /**
   * artifactHits
   */
  @JsonProperty("assets")
  public List<Nexus3SearchResponseAsset> assets;

}

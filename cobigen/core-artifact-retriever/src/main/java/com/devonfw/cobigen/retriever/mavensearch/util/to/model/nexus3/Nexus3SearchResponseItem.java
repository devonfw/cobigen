package com.devonfw.cobigen.retriever.mavensearch.util.to.model.nexus3;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * Nexus3 search response item model
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Nexus3SearchResponseItem {

  /** artifactHits */
  @JsonProperty("assets")
  public List<Nexus3SearchResponseAsset> assets;

}

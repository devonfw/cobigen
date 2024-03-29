package com.devonfw.cobigen.retriever.mavensearch.to.model.nexus3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Nexus3 search response asset model
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Nexus3SearchResponseAsset {

  /** downloadUrl */
  @JsonProperty("downloadUrl")
  public String downloadUrl;

}

package com.devonfw.cobigen.api.util.to.nexus3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * Nexus3 search response asset model
 *
 */
@JsonIgnoreProperties(value = { "path", "id", "repository", "format", "checksum" })
class Nexus3SearchResponseAsset {

  /** downloadUrl */
  @JsonProperty("downloadUrl")
  public String downloadUrl;

}

package com.devonfw.cobigen.api.util.to.nexus2;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * Nexus 2 search response artifact links model
 *
 */
class Nexus2SearchResponeArtifactLinks {

  @JsonProperty("extension")
  private String extension;

  /**
   * @return extension
   */
  public String getExtension() {

    return this.extension;
  }

  @JsonProperty("classifier")
  private String classifier;
}

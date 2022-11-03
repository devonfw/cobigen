package com.devonfw.cobigen.retriever.mavensearch.to.model.nexus2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * Nexus 2 search response artifact links model
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

  /**
   * @return classifier
   */
  public String getClassifier() {

    return this.classifier;
  }
}

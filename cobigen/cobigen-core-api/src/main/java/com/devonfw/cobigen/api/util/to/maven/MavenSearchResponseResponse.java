package com.devonfw.cobigen.api.util.to.maven;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * Maven search response model
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class MavenSearchResponseResponse {

  /**
   * found results
   */
  @JsonProperty("numFound")
  private int numFound;

  /** docs */
  @JsonProperty("docs")
  private List<MavenSearchResponseDoc> docs;

  /**
   * @return numFound
   */
  @JsonIgnore
  public int getNumFound() {

    return this.numFound;
  }

  /**
   * @return docs
   */
  @JsonIgnore
  public List<MavenSearchResponseDoc> getDocs() {

    return this.docs;
  }

}

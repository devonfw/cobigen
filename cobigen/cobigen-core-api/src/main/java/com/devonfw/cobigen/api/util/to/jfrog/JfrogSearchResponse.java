package com.devonfw.cobigen.api.util.to.jfrog;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.api.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.api.exception.RestSearchResponseException;
import com.devonfw.cobigen.api.util.to.AbstractSearchResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Json model for jfrog Search REST API response
 *
 */
public class JfrogSearchResponse extends AbstractSearchResponse {

  /** Logger instance. */
  @JsonIgnore
  private static final Logger LOG = LoggerFactory.getLogger(JfrogSearchResponse.class);

  /** results */
  @JsonProperty("results")
  private List<JfrogSearchResponseResult> results;

  /**
   * @return results
   */
  @JsonIgnore
  public List<JfrogSearchResponseResult> getResults() {

    return this.results;
  }

  @Override
  @JsonIgnore
  public String retrieveJsonResponse(String repositoryUrl, String groupId, String password)
      throws RestSearchResponseException {

    String targetLink = repositoryUrl + "/" + MavenSearchRepositoryConstants.JFROG_TARGET_LINK + "?g=" + groupId;

    return retrieveJsonResponseWithAuthenticationToken(targetLink, null, password, getRepositoryType());
  }

  @Override
  @JsonIgnore
  public List<URL> retrieveDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();

    for (JfrogSearchResponseResult result : getResults()) {
      downloadLinks.add(new URL(result.getUri()));
    }

    return downloadLinks;
  }

  @Override
  public MavenSearchRepositoryType getRepositoryType() {

    return MavenSearchRepositoryType.JFROG;
  }
}

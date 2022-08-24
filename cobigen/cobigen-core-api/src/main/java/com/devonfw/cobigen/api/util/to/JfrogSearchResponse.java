package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.api.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.api.exception.RestSearchResponseException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Json model for jfrog Search REST API response
 *
 */
public class JfrogSearchResponse implements SearchResponse {

  /** Logger instance. */
  @JsonIgnore
  private static final Logger LOG = LoggerFactory.getLogger(JfrogSearchResponse.class);

  /**
   * results
   */
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
  public String getJsonResponse(String repositoryUrl, String groupId) throws RestSearchResponseException {

    return getJsonResponse(repositoryUrl, groupId, null);
  }

  @Override
  @JsonIgnore
  public String getJsonResponse(String repositoryUrl, String groupId, String authToken)
      throws RestSearchResponseException {

    String targetLink = repositoryUrl + "/" + MavenSearchRepositoryConstants.JFROG_TARGET_LINK + "?g=" + groupId;
    LOG.info("Starting {} search REST API request with URL: {}.", getRepositoryType(), targetLink);

    String jsonResponse;

    jsonResponse = SearchResponseUtil.getJsonResponseStringByTargetLink(targetLink, authToken);

    return jsonResponse;
  }

  @Override
  @JsonIgnore
  public List<URL> getDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();

    for (JfrogSearchResponseResult result : getResults()) {
      downloadLinks.add(new URL(result.getUri()));
    }

    return downloadLinks;
  }

  @Override
  public MavenSearchRepositoryType getRepositoryType() {

    return MavenSearchRepositoryType.jfrog;
  }
}

/**
 *
 * Jfrog search response result model
 *
 */
class JfrogSearchResponseResult {

  /**
   * uri
   */
  @JsonProperty("uri")
  private String uri;

  /**
   * @return uri
   */
  @JsonIgnore
  public String getUri() {

    return this.uri;
  }

}

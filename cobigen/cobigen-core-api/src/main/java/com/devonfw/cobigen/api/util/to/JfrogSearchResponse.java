package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.RESTSearchResponseException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

class Result {

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

/**
 * Json model for jfrog Search REST API response
 *
 */
public class JfrogSearchResponse implements AbstractRESTSearchResponse {

  /** Logger instance. */
  @JsonIgnore
  private static final Logger LOG = LoggerFactory.getLogger(JfrogSearchResponse.class);

  @JsonProperty("results")
  private List<Result> results;

  /**
   * @return results
   */
  @JsonIgnore
  public List<Result> getResults() {

    return this.results;
  }

  @Override
  @JsonIgnore
  public String getJsonResponse(String repositoryUrl, String groupId) throws RESTSearchResponseException {

    String targetLink = repositoryUrl + "/" + "solrsearch/select?q=g:" + groupId + "&wt=json";
    LOG.info("Starting Jfrog Search REST API request with URL: {}.", targetLink);

    String jsonResponse;

    jsonResponse = AbstractRESTSearchResponse.getJsonResponseStringByTargetLink(targetLink);

    return jsonResponse;
  }

  @Override
  @JsonIgnore
  public List<URL> getDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();

    for (Result result : getResults()) {
      downloadLinks.add(new URL(result.getUri()));
    }

    return downloadLinks;
  }
}

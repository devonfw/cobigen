package com.devonfw.cobigen.retriever.mavensearch.util.to.model.jfrog;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.retriever.mavensearch.exception.RestSearchResponseException;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.AbstractSearchResponse;
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
  public String retrieveJsonResponse(String repositoryUrl, String username, String password, String groupId,
      String proxyAddress, int proxyPort) throws RestSearchResponseException {

    String targetLink = retrieveRestSearchApiTargetLink(repositoryUrl, groupId);

    return retrieveJsonResponseWithAuthentication(targetLink, username, password, getRepositoryType(), proxyAddress,
        proxyPort);
  }

  @Override
  @JsonIgnore
  public List<URL> retrieveTemplateSetXmlDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();

    for (JfrogSearchResponseResult result : getResults()) {
      if (result.getUri().endsWith(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME)) {
        downloadLinks.add(new URL(result.getUri()));
      }
    }

    return downloadLinks;
  }

  @Override
  public MavenSearchRepositoryType getRepositoryType() {

    return MavenSearchRepositoryType.JFROG;
  }

  @Override
  public String retrieveRestSearchApiTargetLink(String repositoryUrl, String groupId) {

    return repositoryUrl + "/" + MavenSearchRepositoryConstants.JFROG_REST_SEARCH_API_PATH + "?g=" + groupId;
  }
}

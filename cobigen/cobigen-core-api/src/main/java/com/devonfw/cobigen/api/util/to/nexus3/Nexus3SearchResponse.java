package com.devonfw.cobigen.api.util.to.nexus3;

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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Json model for nexus3 Search REST API response
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Nexus3SearchResponse extends AbstractSearchResponse {

  /** Logger instance. */
  @JsonIgnore
  private static final Logger LOG = LoggerFactory.getLogger(Nexus3SearchResponse.class);

  /** items */
  @JsonProperty("items")
  private List<Nexus3SearchResponseItem> items;

  @Override
  @JsonIgnore
  public List<URL> retrieveDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();

    for (Nexus3SearchResponseItem item : this.items) {
      for (Nexus3SearchResponseAsset asset : item.assets) {
        downloadLinks.add(new URL(asset.downloadUrl));
      }
    }

    return removeDuplicatedDownloadURLs(downloadLinks);
  }

  @Override
  @JsonIgnore
  public String retrieveJsonResponse(String repositoryUrl, String groupId, String password)
      throws RestSearchResponseException {

    String targetLink = repositoryUrl + "/" + MavenSearchRepositoryConstants.NEXUS3_REST_SEARCH_API_PATH
        + "?repository=maven-central" + "&group=" + groupId;

    return retrieveJsonResponseWithAuthenticationToken(targetLink, null, password, getRepositoryType());
  }

  @Override
  public MavenSearchRepositoryType getRepositoryType() {

    return MavenSearchRepositoryType.NEXUS3;
  }
}

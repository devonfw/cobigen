package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.api.exception.RESTSearchResponseException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Json model for nexus3 Search REST API response
 *
 */
@JsonIgnoreProperties(value = { "continuationToken" })
public class Nexus3SearchResponse implements AbstractRESTSearchResponse {

  /** Logger instance. */
  @JsonIgnore
  private static final Logger LOG = LoggerFactory.getLogger(Nexus3SearchResponse.class);

  @JsonProperty("items")
  private List<Nexus3SearchResponseItem> items;

  @Override
  @JsonIgnore
  public List<URL> getDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();

    for (Nexus3SearchResponseItem item : this.items) {
      for (Nexus3SearchResponseAsset asset : item.assets) {
        downloadLinks.add(new URL(asset.downloadUrl));
      }
    }

    // removes duplicates
    List<URL> newDownloadList = downloadLinks.stream().distinct().collect(Collectors.toList());

    return newDownloadList;
  }

  @Override
  @JsonIgnore
  public String getJsonResponse(String repositoryUrl, String groupId) throws RESTSearchResponseException {

    return getJsonResponse(repositoryUrl, groupId, null);
  }

  @Override
  @JsonIgnore
  public String getJsonResponse(String repositoryUrl, String groupId, String authToken)
      throws RESTSearchResponseException {

    String targetLink = repositoryUrl + "/" + MavenSearchRepositoryConstants.NEXUS3_TARGET_LINK
        + "?repository=maven-central" + "&group=" + groupId;
    LOG.info("Starting Nexus Search REST API request with URL: {}.", targetLink);

    String jsonResponse;

    jsonResponse = AbstractRESTSearchResponse.getJsonResponseStringByTargetLink(targetLink, authToken);

    return jsonResponse;
  }
}

/**
 *
 * Nexus3 search response asset model
 *
 */
@JsonIgnoreProperties(value = { "path", "id", "repository", "format", "checksum" })
class Nexus3SearchResponseAsset {

  /**
   * downloadUrl
   */
  @JsonProperty("downloadUrl")
  public String downloadUrl;

}

/**
 *
 * Nexus3 search response item model
 *
 */
@JsonIgnoreProperties(value = { "id", "repository", "format", "group", "name", "version" })
class Nexus3SearchResponseItem {

  /**
   * artifactHits
   */
  @JsonProperty("assets")
  public List<Nexus3SearchResponseAsset> assets;

}

package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.api.exception.RESTSearchResponseException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Json model for nexus Search REST API response
 *
 */
@JsonIgnoreProperties(value = { "continuationToken" })
public class NexusSearchResponse implements AbstractRESTSearchResponse {

  /** Logger instance. */
  @JsonIgnore
  private static final Logger LOG = LoggerFactory.getLogger(NexusSearchResponse.class);

  @JsonProperty("items")
  private List<NexusSearchResponseItem> items;

  @Override
  @JsonIgnore
  public List<URL> getDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();

    for (NexusSearchResponseItem item : this.items) {
      for (NexusSearchResponseAsset asset : item.assets) {
        downloadLinks.add(new URL(asset.downloadUrl));
      }

    }

    return downloadLinks;
  }

  @Override
  @JsonIgnore
  public String getJsonResponse(String repositoryUrl, String groupId) throws RESTSearchResponseException {

    String targetLink = repositoryUrl + "/" + MavenSearchRepositoryConstants.NEXUS_TARGET_LINK
        + "?repository=maven-central" + "&group=" + groupId;
    LOG.info("Starting Nexus Search REST API request with URL: {}.", targetLink);

    String jsonResponse;

    jsonResponse = AbstractRESTSearchResponse.getJsonResponseStringByTargetLink(targetLink);

    return jsonResponse;
  }
}

/**
 *
 * Nexus search response asset model
 *
 */
@JsonIgnoreProperties(value = { "id", "format", "checksum" })
class NexusSearchResponseAsset {

  /**
   * downloadUrl
   */
  @JsonProperty("downloadUrl")
  public String downloadUrl;

  /**
   * path
   */
  @JsonProperty("path")
  public String path;

  /**
   * repository
   */
  @JsonProperty("repository")
  public String repository;

  /**
   * latest version
   */
  @JsonProperty("latestVersion")
  public String latestVersion;

  /**
   * repositoryId
   */
  @JsonProperty("repositoryId")
  public String repositoryId;
}

/**
 *
 * Nexus search response item model
 *
 */
@JsonIgnoreProperties(value = { "id", "format" })
class NexusSearchResponseItem {

  /**
   * repository
   */
  @JsonProperty("repository")
  private String repository;

  /**
   * group
   */
  @JsonProperty("group")
  private String group;

  /**
   * name
   */
  @JsonProperty("name")
  private String name;

  /**
   * version
   */
  @JsonProperty("version")
  private String version;

  /**
   * assets
   */
  @JsonProperty("assets")
  public List<NexusSearchResponseAsset> assets;
}

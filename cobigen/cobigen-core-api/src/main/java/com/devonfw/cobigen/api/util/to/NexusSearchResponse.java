package com.devonfw.cobigen.api.util.to;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = { "id", "format", "checksum" })
class Asset {
  @JsonProperty("downloadUrl")
  public String downloadUrl;

  @JsonProperty("path")
  public String path;

  @JsonProperty("repository")
  public String repository;

  @JsonProperty("latestVersion")
  public String latestVersion;

  @JsonProperty("repositoryId")
  public String repositoryId;
}

@JsonIgnoreProperties(value = { "id", "format" })
class Item {

  @JsonProperty("repository")
  private String repository;

  @JsonProperty("group")
  private String group;

  @JsonProperty("name")
  private String name;

  @JsonProperty("version")
  private String version;

  @JsonProperty("assets")
  public List<Asset> assets;
}

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
  private List<Item> items;

  @Override
  @JsonIgnore
  public List<URL> getDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();

    for (Item item : this.items) {
      for (Asset asset : item.assets) {
        downloadLinks.add(new URL(asset.downloadUrl));
      }

    }

    return downloadLinks;
  }

  @Override
  @JsonIgnore
  public String getJsonResponse(String repositoryUrl, String groupId) throws IOException {

    String targetLink = repositoryUrl + "/" + "service/rest/v1/search?repository=maven-central&group=" + groupId;
    LOG.info("Starting Nexus Search REST API request with URL: {}.", targetLink);

    String jsonResponse;

    try {
      jsonResponse = AbstractRESTSearchResponse.getJsonResponseStringByTargetLink(targetLink);
    } catch (IOException e) {
      LOG.error("Nexus Search REST API request was not successful, status code was: {}!", e.getMessage());
      throw new IOException();
    }

    return jsonResponse;
  }
}

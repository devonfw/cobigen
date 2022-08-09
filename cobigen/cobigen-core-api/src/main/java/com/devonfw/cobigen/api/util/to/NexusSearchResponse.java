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
 * Json model for nexus Search REST API response
 *
 */
@JsonIgnoreProperties(value = { "totalCount", "from", "count", "tooManyResults", "collapsed", "repoDetails" })
public class NexusSearchResponse implements AbstractRESTSearchResponse {

  /** Logger instance. */
  @JsonIgnore
  private static final Logger LOG = LoggerFactory.getLogger(NexusSearchResponse.class);

  @JsonProperty("data")
  private List<NexusSearchResponseData> data;

  @Override
  @JsonIgnore
  public List<URL> getDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();

    for (NexusSearchResponseData item : this.data) {
      for (NexusSearchResponseArtifactHits artifactHit : item.artifactHits) {
        for (NexusSearchResponeArtifactLinks artifactLink : artifactHit.artifactLinks) {
          downloadLinks.add(AbstractRESTSearchResponse.createDownloadLink(
              MavenSearchRepositoryConstants.NEXUS_REPOSITORY_URL + "/"
                  + MavenSearchRepositoryConstants.NEXUS_REPOSITORY_LINK,
              item.getGroupId(), item.getArtifactId(), item.getVersion(), "." + artifactLink.getExtension()));

        }
      }
    }

    // removes duplicates
    List<URL> newDownloadList = downloadLinks.stream().distinct().collect(Collectors.toList());

    return newDownloadList;
  }

  @Override
  @JsonIgnore
  public String getJsonResponse(String repositoryUrl, String groupId) throws RESTSearchResponseException {

    String targetLink = repositoryUrl + "/" + MavenSearchRepositoryConstants.NEXUS_TARGET_LINK + "?_dc="
        + MavenSearchRepositoryConstants.NEXUS_DC_ID + "&q=" + groupId;
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
@JsonIgnoreProperties(value = { "repositoryId" })
class NexusSearchResponseArtifactHits {

  /**
   * artifactLinks
   */
  @JsonProperty("artifactLinks")
  public List<NexusSearchResponeArtifactLinks> artifactLinks;

}

class NexusSearchResponeArtifactLinks {

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
}

/**
 *
 * Nexus search response item model
 *
 */
@JsonIgnoreProperties(value = { "latestRelease", "latestReleaseRepositoryId", "highlightedFragment" })
class NexusSearchResponseData {

  /**
   * groupId
   */
  @JsonProperty("groupId")
  private String groupId;

  /**
   * @return groupId
   */
  public String getGroupId() {

    return this.groupId;
  }

  /**
   * @return artifactId
   */
  public String getArtifactId() {

    return this.artifactId;
  }

  /**
   * @return version
   */
  public String getVersion() {

    return this.version;
  }

  /**
   * artifactId
   */
  @JsonProperty("artifactId")
  private String artifactId;

  /**
   * version
   */
  @JsonProperty("version")
  private String version;

  /**
   * artifactHits
   */
  @JsonProperty("artifactHits")
  public List<NexusSearchResponseArtifactHits> artifactHits;

}

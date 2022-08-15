package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.api.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.api.exception.RESTSearchResponseException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Json model for nexus2 Search REST API response
 *
 */
@JsonIgnoreProperties(value = { "totalCount", "from", "count", "tooManyResults", "collapsed", "repoDetails" })
public class Nexus2SearchResponse implements SearchResponse {

  /** Logger instance. */
  @JsonIgnore
  private static final Logger LOG = LoggerFactory.getLogger(Nexus2SearchResponse.class);

  @JsonProperty("data")
  private List<Nexus2SearchResponseData> data;

  @Override
  @JsonIgnore
  public List<URL> getDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();

    for (Nexus2SearchResponseData item : this.data) {
      for (Nexus2SearchResponseArtifactHits artifactHit : item.artifactHits) {
        for (Nexus2SearchResponeArtifactLinks artifactLink : artifactHit.artifactLinks) {
          downloadLinks.add(SearchResponseUtil.createDownloadLink(
              MavenSearchRepositoryConstants.NEXUS2_REPOSITORY_URL + "/"
                  + MavenSearchRepositoryConstants.NEXUS2_REPOSITORY_LINK,
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

    return getJsonResponse(repositoryUrl, groupId, null);
  }

  @Override
  @JsonIgnore
  public String getJsonResponse(String repositoryUrl, String groupId, String authToken)
      throws RESTSearchResponseException {

    String targetLink = repositoryUrl + "/" + MavenSearchRepositoryConstants.NEXUS2_TARGET_LINK + "?_dc="
        + MavenSearchRepositoryConstants.NEXUS2_DC_ID + "&q=" + groupId;
    LOG.info("Starting {} search REST API request with URL: {}.", getRepositoryType(), targetLink);

    String jsonResponse;

    jsonResponse = SearchResponseUtil.getJsonResponseStringByTargetLink(targetLink, authToken);

    return jsonResponse;
  }

  @Override
  public MavenSearchRepositoryType getRepositoryType() {

    return MavenSearchRepositoryType.nexus2;
  }
}

/**
 *
 * Nexus search response asset model
 *
 */
@JsonIgnoreProperties(value = { "repositoryId" })
class Nexus2SearchResponseArtifactHits {

  /**
   * artifactLinks
   */
  @JsonProperty("artifactLinks")
  public List<Nexus2SearchResponeArtifactLinks> artifactLinks;

}

class Nexus2SearchResponeArtifactLinks {

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
 * Nexus2 search response item model
 *
 */
@JsonIgnoreProperties(value = { "latestRelease", "latestReleaseRepositoryId", "highlightedFragment" })
class Nexus2SearchResponseData {

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
  public List<Nexus2SearchResponseArtifactHits> artifactHits;

}

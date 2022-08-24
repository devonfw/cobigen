package com.devonfw.cobigen.api.util.to.nexus2;

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
import com.devonfw.cobigen.api.util.to.SearchResponseUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Json model for nexus2 Search REST API response
 *
 */
@JsonIgnoreProperties(value = { "totalCount", "from", "count", "tooManyResults", "collapsed", "repoDetails" })
public class Nexus2SearchResponse extends AbstractSearchResponse {

  /** Logger instance. */
  @JsonIgnore
  private static final Logger LOG = LoggerFactory.getLogger(Nexus2SearchResponse.class);

  /** data */
  @JsonProperty("data")
  private List<Nexus2SearchResponseData> data;

  @Override
  @JsonIgnore
  public List<URL> retrieveDownloadURLs() throws MalformedURLException {

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

    return removeDuplicatedDownloadURLs(downloadLinks);
  }

  @Override
  @JsonIgnore
  public String retrieveJsonResponse(String repositoryUrl, String groupId, String authToken)
      throws RestSearchResponseException {

    String targetLink = repositoryUrl + "/" + MavenSearchRepositoryConstants.NEXUS2_TARGET_LINK + "?q=" + groupId;

    return retrieveJsonResponseWithAuthenticationToken(targetLink, authToken);
  }

  @Override
  public MavenSearchRepositoryType getRepositoryType() {

    return MavenSearchRepositoryType.nexus2;
  }
}

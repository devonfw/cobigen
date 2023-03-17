package com.devonfw.cobigen.retriever.mavensearch.to.model.nexus2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.retriever.mavensearch.exception.RestSearchResponseException;
import com.devonfw.cobigen.retriever.mavensearch.to.model.AbstractSearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.to.model.ServerCredentials;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Json model for nexus2 Search REST API response
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Nexus2SearchResponse extends AbstractSearchResponse {

  /** Logger instance. */
  @JsonIgnore
  private static final Logger LOG = LoggerFactory.getLogger(Nexus2SearchResponse.class);

  /** data */
  @JsonProperty("data")
  private List<Nexus2SearchResponseData> data;

  @Override
  @JsonIgnore
  public List<URL> retrieveTemplateSetXmlDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();

    if (this.data == null) {
      LOG.debug("The {} response was empty.", getRepositoryType());
      return downloadLinks;
    }

    for (Nexus2SearchResponseData item : this.data) {
      for (Nexus2SearchResponseArtifactHits artifactHit : item.artifactHits) {
        for (Nexus2SearchResponseArtifactLinks artifactLink : artifactHit.artifactLinks) {
          if (artifactLink.getClassifier() != null && artifactLink.getClassifier().equals("template-set")) {
            downloadLinks.add(AbstractSearchResponse.createDownloadLink(
                MavenSearchRepositoryConstants.NEXUS2_REPOSITORY_URL + "/"
                    + MavenSearchRepositoryConstants.NEXUS2_REPOSITORY_LINK,
                item.getGroupId(), item.getArtifactId(), item.getVersion(),
                "-" + artifactLink.getClassifier() + "." + artifactLink.getExtension()));
          }
        }
      }
    }

    return removeDuplicatedDownloadURLs(downloadLinks);
  }

  @Override
  @JsonIgnore
  public String retrieveJsonResponse(ServerCredentials serverCredentials, String groupId)
      throws RestSearchResponseException {

    String targetLink = retrieveRestSearchApiTargetLink(serverCredentials.getBaseUrl(), groupId);

    return retrieveJsonResponseWithAuthentication(targetLink, getRepositoryType(), serverCredentials);
  }

  @Override
  public MavenSearchRepositoryType getRepositoryType() {

    return MavenSearchRepositoryType.NEXUS2;
  }

  @Override
  public String retrieveRestSearchApiTargetLink(String repositoryUrl, String groupId) {

    return repositoryUrl + "/" + MavenSearchRepositoryConstants.NEXUS2_REST_SEARCH_API_PATH + "?q=" + groupId;
  }
}

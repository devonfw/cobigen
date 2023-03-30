package com.devonfw.cobigen.retriever.mavensearch.to.model.nexus2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            // Check for full SNAPSHOT version link
            if (item.getVersion().contains("-SNAPSHOT")) {

              String fullSnapshotVersion = determineSnapshotVersionFromResource(item.getGroupId(), item.getArtifactId(),
                  item.getVersion());

              URL snapshotUrl = AbstractSearchResponse.createDownloadLink(
                  MavenSearchRepositoryConstants.NEXUS2_REPOSITORY_URL + "/"
                      + MavenSearchRepositoryConstants.NEXUS2_SNAPSHOT_REPOSITORY_LINK,
                  item.getGroupId(), item.getArtifactId(), item.getVersion(),
                  "-" + artifactLink.getClassifier() + "." + artifactLink.getExtension(), fullSnapshotVersion);

              downloadLinks.add(snapshotUrl);
            } else {
              downloadLinks.add(AbstractSearchResponse.createDownloadLink(
                  MavenSearchRepositoryConstants.NEXUS2_REPOSITORY_URL + "/"
                      + MavenSearchRepositoryConstants.NEXUS2_REPOSITORY_LINK,
                  item.getGroupId(), item.getArtifactId(), item.getVersion(),
                  "-" + artifactLink.getClassifier() + "." + artifactLink.getExtension(), ""));
            }
          }
        }
      }
    }

    return removeDuplicatedDownloadURLs(downloadLinks);
  }

  /**
   * Determines the full snapshot version number from resource xml
   *
   * @param groupId String of the group ID
   * @param artifactId String of the artifact ID
   * @param version String of the version
   * @return String of full snapshot version number
   */
  @JsonIgnore
  public String determineSnapshotVersionFromResource(String groupId, String artifactId, String version) {

    String parsedGroupId = groupId.replace(".", "/");
    String snapshotUrl = MavenSearchRepositoryConstants.NEXUS2_REPOSITORY_URL + "/"
        + MavenSearchRepositoryConstants.NEXUS2_SNAPSHOT_REPOSITORY_LINK + "/" + parsedGroupId + "/" + artifactId + "/"
        + version;
    String response = AbstractSearchResponse.retrieveJsonResponseWithAuthentication(snapshotUrl, getRepositoryType(),
        null);
    Pattern pattern = Pattern.compile("(\\d+\\.)?(\\d+\\.)?(\\*|\\d+)-(\\d+)-template-set\\.xml");
    Matcher matcher = pattern.matcher(response);
    String versionNumber = "";
    if (matcher.find()) {
      versionNumber = matcher.group();
    }
    return versionNumber.replace("-template-set.xml", "");
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

    String rootUrl = AbstractSearchResponse.createRootURL(repositoryUrl);
    if (rootUrl != null) {
      return rootUrl + "/" + MavenSearchRepositoryConstants.NEXUS2_REST_SEARCH_API_PATH + "?q=" + groupId;
    } else {
      return null;
    }

  }
}

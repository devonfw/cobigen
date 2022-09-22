package com.devonfw.cobigen.api.util.to.maven;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.api.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.api.exception.RestSearchResponseException;
import com.devonfw.cobigen.api.util.to.AbstractSearchResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Json model for maven Search REST API response
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MavenSearchResponse extends AbstractSearchResponse {

  /** Logger instance. */
  @JsonIgnore
  private static final Logger LOG = LoggerFactory.getLogger(MavenSearchResponse.class);

  @JsonProperty("response")
  private MavenSearchResponseResponse response;

  /**
   * @return response
   */
  @JsonIgnore
  public MavenSearchResponseResponse getResponse() {

    return this.response;
  }

  @Override
  @JsonIgnore
  public String retrieveJsonResponse(String repositoryUrl, String username, String password, String groupId)
      throws RestSearchResponseException {

    String targetLink = repositoryUrl + "/" + MavenSearchRepositoryConstants.MAVEN_REST_SEARCH_API_PATH + "?q=g:"
        + groupId + "&wt=json";

    return retrieveJsonResponseWithAuthentication(targetLink, username, password, getRepositoryType());
  }

  @Override
  @JsonIgnore
  public List<URL> retrieveTemplateSetXmlDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();
    List<MavenSearchResponseDoc> docs = getResponse().getDocs();

    for (MavenSearchResponseDoc doc : docs) {
      for (String fileEnding : doc.getEc()) {
        if (fileEnding.endsWith(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME)) {
          downloadLinks.add(
              AbstractSearchResponse.createDownloadLink(MavenSearchRepositoryConstants.MAVEN_REPOSITORY_DOWNLOAD_LINK,
                  doc.getGroup(), doc.getArtifact(), doc.getLatestVersion(), fileEnding));
        }

      }

    }

    return downloadLinks;
  }

  @Override
  public MavenSearchRepositoryType getRepositoryType() {

    return MavenSearchRepositoryType.MAVEN;
  }

}
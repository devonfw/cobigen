package com.devonfw.cobigen.api.util.to.maven;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.api.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.api.exception.RestSearchResponseException;
import com.devonfw.cobigen.api.util.to.SearchResponse;
import com.devonfw.cobigen.api.util.to.SearchResponseUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Json model for maven Search REST API response
 *
 */
@JsonIgnoreProperties(value = { "responseHeader", "spellcheck" })
public class MavenSearchResponse implements SearchResponse {

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
  public String getJsonResponse(String repositoryUrl, String groupId) throws RestSearchResponseException {

    return getJsonResponse(repositoryUrl, groupId, null);
  }

  @Override
  @JsonIgnore
  public String getJsonResponse(String repositoryUrl, String groupId, String authToken)
      throws RestSearchResponseException {

    String targetLink = repositoryUrl + "/" + MavenSearchRepositoryConstants.MAVEN_TARGET_LINK + "?q=g:" + groupId
        + "&wt=json";
    LOG.info("Starting {} search REST API request with URL: {}.", getRepositoryType(), targetLink);

    String jsonResponse;

    jsonResponse = SearchResponseUtil.getJsonResponseStringByTargetLink(targetLink, authToken);

    return jsonResponse;

  }

  @Override
  @JsonIgnore
  public List<URL> getDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();
    List<MavenSearchResponseDoc> docs = getResponse().getDocs();

    for (MavenSearchResponseDoc doc : docs) {
      for (String fileEnding : doc.getEc()) {
        String newFileEnding = fileEnding;
        downloadLinks
            .add(SearchResponseUtil.createDownloadLink(MavenSearchRepositoryConstants.MAVEN_REPOSITORY_DOWNLOAD_LINK,
                doc.getGroup(), doc.getArtifact(), doc.getLatestVersion(), newFileEnding));
      }

    }

    return downloadLinks;
  }

  @Override
  public MavenSearchRepositoryType getRepositoryType() {

    return MavenSearchRepositoryType.maven;
  }

}
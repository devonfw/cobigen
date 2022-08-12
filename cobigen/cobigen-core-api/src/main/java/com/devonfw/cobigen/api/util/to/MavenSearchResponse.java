package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.api.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.api.exception.RESTSearchResponseException;
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
  public String getJsonResponse(String repositoryUrl, String groupId) throws RESTSearchResponseException {

    return getJsonResponse(repositoryUrl, groupId, null);
  }

  @Override
  @JsonIgnore
  public String getJsonResponse(String repositoryUrl, String groupId, String authToken)
      throws RESTSearchResponseException {

    String targetLink = repositoryUrl + "/" + MavenSearchRepositoryConstants.MAVEN_TARGET_LINK + "?q=g:" + groupId
        + "&wt=json";
    LOG.info("Starting {} search REST API request with URL: {}.", getRepositoryType(), targetLink);

    int limitRows = MavenSearchRepositoryConstants.MAVEN_MAX_RESPONSE_ROWS;
    if (limitRows > 0) {
      targetLink += "&rows=" + limitRows;
      LOG.info("Limiting {} search REST API request to: {} rows.", getRepositoryType(), limitRows);
    }

    String jsonResponse;

    jsonResponse = SearchResponseFactory.getJsonResponseStringByTargetLink(targetLink, authToken);

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
            .add(SearchResponseFactory.createDownloadLink(MavenSearchRepositoryConstants.MAVEN_REPOSITORY_DOWNLOAD_LINK,
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

/**
 *
 * Maven search response doc model
 *
 */
@JsonIgnoreProperties(value = { "p", "timestamp", "versionCount", "text" })
class MavenSearchResponseDoc {
  /**
   * id
   */
  @JsonProperty("id")
  private String id;

  /**
   * group
   */
  @JsonProperty("g")
  private String group;

  /**
   * artifact
   */
  @JsonProperty("a")
  private String artifact;

  /**
   * latest version
   */
  @JsonProperty("latestVersion")
  private String latestVersion;

  /**
   * repository ID
   */
  @JsonProperty("repositoryId")
  private String repositoryId;

  /**
   * ec (file ending)
   */
  @JsonProperty("ec")
  private List<String> ec;

  /**
   * @return ec
   */
  @JsonIgnore
  public List<String> getEc() {

    return this.ec;
  }

  /**
   * @return id
   */
  @JsonIgnore
  public String getId() {

    return this.id;
  }

  /**
   * @return group
   */
  @JsonIgnore
  public String getGroup() {

    return this.group;
  }

  /**
   * @return artifact
   */
  @JsonIgnore
  public String getArtifact() {

    return this.artifact;
  }

  /**
   * @return latestVersion
   */
  @JsonIgnore
  public String getLatestVersion() {

    return this.latestVersion;
  }

  /**
   * @return repositoryId
   */
  @JsonIgnore
  public String getRepositoryId() {

    return this.repositoryId;
  }

}

/**
 *
 * Maven search response model
 *
 */
@JsonIgnoreProperties(value = { "start" })
class MavenSearchResponseResponse {

  /**
   * found results
   */
  @JsonProperty("numFound")
  private int numFound;

  /**
   * docs
   */
  @JsonProperty("docs")
  private List<MavenSearchResponseDoc> docs;

  /**
   * @return numFound
   */
  @JsonIgnore
  public int getNumFound() {

    return this.numFound;
  }

  /**
   * @return docs
   */
  @JsonIgnore
  public List<MavenSearchResponseDoc> getDocs() {

    return this.docs;
  }

}

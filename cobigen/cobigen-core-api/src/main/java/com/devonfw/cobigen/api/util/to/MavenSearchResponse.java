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
 * Json model for maven Search REST API response
 *
 */
@JsonIgnoreProperties(value = { "responseHeader", "spellcheck" })
public class MavenSearchResponse implements AbstractRESTSearchResponse {

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

    String targetLink = repositoryUrl + "/" + MavenSearchRepositoryConstants.MAVEN_TARGET_LINK + "?q=g:" + groupId
        + "&wt=json";
    LOG.info("Starting Maven Search REST API request with URL: {}.", targetLink);

    int limitRows = MavenSearchRepositoryConstants.MAVEN_MAX_RESPONSE_ROWS;
    if (limitRows > 0) {
      targetLink += "&rows=" + limitRows;
      LOG.info("Limiting Maven Search REST API request to: {} rows.", limitRows);
    }

    String jsonResponse;

    jsonResponse = AbstractRESTSearchResponse.getJsonResponseStringByTargetLink(targetLink);

    return jsonResponse;

  }

  @Override
  @JsonIgnore
  public List<URL> getDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();
    List<MavenSearchResponseDoc> docs = getResponse().getDocs();

    for (MavenSearchResponseDoc doc : docs) {
      downloadLinks.add(doc.createDownloadLink("https://repo1.maven.org/maven2"));
    }

    return downloadLinks;
  }

}

/**
 *
 * Maven search response doc model
 *
 */
@JsonIgnoreProperties(value = { "p", "timestamp", "versionCount", "text", "ec" })
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

  /**
   * Creates a download link (concatenates maven repository link with groupId, artifact and version)
   *
   * @param mavenRepo link to the maven repository to use
   * @return concatenated download link
   * @throws MalformedURLException if the URL was not valid
   */
  @JsonIgnore
  public URL createDownloadLink(String mavenRepo) throws MalformedURLException {

    String parsedGroupId = getGroup().replace(".", "/");
    String downloadFile = getArtifact() + "-" + getLatestVersion() + ".jar";
    String downloadLink = mavenRepo + "/" + parsedGroupId + "/" + getArtifact() + "/" + getLatestVersion() + "/"
        + downloadFile;
    URL url = new URL(downloadLink);
    return url;
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

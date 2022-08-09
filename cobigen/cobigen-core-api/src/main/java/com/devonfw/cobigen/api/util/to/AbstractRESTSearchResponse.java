package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.devonfw.cobigen.api.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.api.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.api.exception.RESTSearchResponseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Handles the responses from various search REST API's
 *
 */
public interface AbstractRESTSearchResponse {

  /**
   * Creates a list of download links
   *
   * @return List of download links
   * @throws MalformedURLException if an URL was not valid
   */
  public List<URL> getDownloadURLs() throws MalformedURLException;

  /**
   * Gets the json response
   *
   * @param repositoryUrl URL of the repository
   * @param groupId to search for
   * @return String of json response
   * @throws RESTSearchResponseException if the request did not return status 200
   */
  public String getJsonResponse(String repositoryUrl, String groupId) throws RESTSearchResponseException;

  /**
   * Gets the download links by given repository type
   *
   * @param repositoryType String of the type of the repository e.g. maven, jfrog, nexus
   * @param groupId the groupId to search for
   *
   * @return List of download links
   * @throws RESTSearchResponseException if an error occurred
   * @throws JsonProcessingException if the json processing was not possible
   * @throws JsonMappingException if the json mapping was not possible
   * @throws MalformedURLException if an URL was malformed
   *
   */
  public static List<URL> getArtifactDownloadLinks(MavenSearchRepositoryType repositoryType, String groupId)
      throws RESTSearchResponseException, JsonMappingException, JsonProcessingException, MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();
    String jsonResponse = "";

    if (repositoryType == MavenSearchRepositoryType.maven) {
      MavenSearchResponse response = new MavenSearchResponse();
      String mavenRepositoryURL = MavenSearchRepositoryConstants.MAVEN_REPOSITORY_URL;
      jsonResponse = response.getJsonResponse(mavenRepositoryURL, groupId);
      response = mapper.readValue(jsonResponse, MavenSearchResponse.class);
      downloadLinks = response.getDownloadURLs();
      return downloadLinks;
    }

    if (repositoryType == MavenSearchRepositoryType.jfrog) {
      JfrogSearchResponse response = new JfrogSearchResponse();
      String jfrogRepositoryURL = MavenSearchRepositoryConstants.JFROG_REPOSITORY_URL;
      jsonResponse = response.getJsonResponse(jfrogRepositoryURL, groupId);
      response = mapper.readValue(jsonResponse, JfrogSearchResponse.class);
      downloadLinks = response.getDownloadURLs();
      return downloadLinks;
    }

    if (repositoryType == MavenSearchRepositoryType.nexus) {
      NexusSearchResponse response = new NexusSearchResponse();
      String nexusRepositoryURL = MavenSearchRepositoryConstants.NEXUS_REPOSITORY_URL;
      jsonResponse = response.getJsonResponse(nexusRepositoryURL, groupId);
      response = mapper.readValue(jsonResponse, NexusSearchResponse.class);
      downloadLinks = response.getDownloadURLs();
      return downloadLinks;
    }

    return null;
  }

  /**
   * Gets a json response by given REST API target link
   *
   * @param targetLink link to get response from
   * @return String of json response
   * @throws RESTSearchResponseException if the returned status code was not 200 OK
   */
  public static String getJsonResponseStringByTargetLink(String targetLink) throws RESTSearchResponseException {

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(targetLink);
    Invocation.Builder request = target.request(MediaType.APPLICATION_JSON);
    Response response = request.get();
    int status = response.getStatus();
    String jsonResponse = "";
    if (status == 200) {
      jsonResponse = response.readEntity(String.class);
    } else {
      throw new RESTSearchResponseException("The search REST API returned the unexpected status code",
          String.valueOf(status));
    }
    return jsonResponse;
  }

  /**
   * Creates a download link (concatenates maven repository link with groupId, artifact and version)
   *
   * @param mavenRepo link to the maven repository to use
   * @param groupId for the download link
   * @param artifactId for the download link
   * @param version for the download link
   * @param fileEnding file ending for the download link
   * @return concatenated download link
   * @throws MalformedURLException if the URL was not valid
   */
  public static URL createDownloadLink(String mavenRepo, String groupId, String artifactId, String version,
      String fileEnding) throws MalformedURLException {

    String parsedGroupId = groupId.replace(".", "/");
    String downloadFile = artifactId + "-" + version + fileEnding;
    String downloadLink = mavenRepo + "/" + parsedGroupId + "/" + artifactId + "/" + version + "/" + downloadFile;
    URL url = new URL(downloadLink);
    return url;
  }

}
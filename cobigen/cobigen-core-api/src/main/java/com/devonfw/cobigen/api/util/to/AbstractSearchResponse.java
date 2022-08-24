package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.api.exception.RestSearchResponseException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * This interface should be inherited for all maven REST search API responses to properly convert {@link JsonProperty}
 * from responses to valid download URLs
 */
public abstract class AbstractSearchResponse {

  /** Logger instance. */
  @JsonIgnore
  private static final Logger LOG = LoggerFactory.getLogger(AbstractSearchResponse.class);

  /**
   * @return the {@link MavenSearchRepositoryType} type
   */
  public abstract MavenSearchRepositoryType getRepositoryType();

  /**
   * Creates a list of download URLs
   *
   * @return List of download links
   * @throws MalformedURLException if an URL was not valid
   */
  public abstract List<URL> retrieveDownloadURLs() throws MalformedURLException;

  /**
   * Removes duplicates from list of download URLs
   *
   * @param downloadUrls list of download URLs
   * @return List of download links
   * @throws MalformedURLException if an URL was not valid
   */
  public List<URL> removeDuplicatedDownloadURLs(List<URL> downloadUrls) throws MalformedURLException {

    return downloadUrls.stream().distinct().collect(Collectors.toList());
  }

  /**
   * Retrieves the json response from a repository URL and a group ID
   *
   * @param repositoryUrl URL of the repository
   * @param groupId to search for
   * @return String of json response
   * @throws RestSearchResponseException if the request did not return status 200
   */
  public String retrieveJsonResponse(String repositoryUrl, String groupId) throws RestSearchResponseException {

    return retrieveJsonResponse(repositoryUrl, groupId, null);
  }

  /**
   * Retrieves the json response from a repository URL, a group ID and a bearer authentication token
   *
   * @param repositoryUrl URL of the repository
   * @param groupId to search for
   * @param authToken bearer token to use for authentication
   * @return String of json response
   * @throws RestSearchResponseException if the request did not return status 200
   */
  public abstract String retrieveJsonResponse(String repositoryUrl, String groupId, String authToken)
      throws RestSearchResponseException;

  /**
   * Creates a @WebTarget with provided authentication token
   *
   * @param targetLink link to get response from
   * @param token bearer token to use for authentication
   * @return WebTarget to use as resource
   */
  private static WebTarget bearerAuthenticationWithOAuth2AtClientLevel(String targetLink, String token) {

    Feature feature = OAuth2ClientSupport.feature(token);
    Client client = ClientBuilder.newBuilder().register(feature).build();

    WebTarget target = client.target(targetLink);
    return target;
  }

  /**
   * Retrieves a json response by given REST API target link using bearer authentication token
   *
   * @param targetLink link to get response from
   * @param authToken bearer token to use for authentication
   * @param searchRepositoryType the type of the search repository
   * @return String of json response
   * @throws RestSearchResponseException if the returned status code was not 200 OK
   */
  public static String retrieveJsonResponseWithAuthenticationToken(String targetLink, String authToken,
      MavenSearchRepositoryType searchRepositoryType) throws RestSearchResponseException {

    LOG.info("Starting {} search REST API request with URL: {}.", searchRepositoryType, targetLink);

    WebTarget target = null;

    if (authToken != null) {
      target = bearerAuthenticationWithOAuth2AtClientLevel(targetLink, authToken);
    } else {
      Client client = ClientBuilder.newClient();
      target = client.target(targetLink);
    }

    Response response = null;
    Invocation.Builder request = target.request(MediaType.APPLICATION_JSON);
    response = request.get();

    int status = response.getStatus();
    String jsonResponse = "";
    if (status == 200) {
      jsonResponse = response.readEntity(String.class);
    } else {
      throw new RestSearchResponseException("The search REST API returned the unexpected status code: ",
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
  protected static URL createDownloadLink(String mavenRepo, String groupId, String artifactId, String version,
      String fileEnding) throws MalformedURLException {

    String parsedGroupId = groupId.replace(".", "/");
    String downloadFile = artifactId + "-" + version + fileEnding;
    String downloadLink = mavenRepo + "/" + parsedGroupId + "/" + artifactId + "/" + version + "/" + downloadFile;
    URL url = new URL(downloadLink);
    return url;
  }

}
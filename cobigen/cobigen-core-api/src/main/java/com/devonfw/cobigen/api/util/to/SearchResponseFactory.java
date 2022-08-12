package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.RESTSearchResponseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Handles the responses from various search REST API's
 *
 */
public class SearchResponseFactory {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(SearchResponseFactory.class);

  /**
   * Gets a list of available search REST APIs (register a new search interface type here)
   *
   * @return list of available {@link SearchResponse}
   */
  private static List<SearchResponse> getAvailableSearchInterfaces() {

    List<SearchResponse> availableSearchInterfaces = new ArrayList<>();
    availableSearchInterfaces.add(new MavenSearchResponse());
    availableSearchInterfaces.add(new JfrogSearchResponse());
    availableSearchInterfaces.add(new Nexus2SearchResponse());
    availableSearchInterfaces.add(new Nexus3SearchResponse());
    return availableSearchInterfaces;
  }

  /**
   * Gets the download links by given repository type
   *
   * @param baseURL String of the repository server URL
   * @param groupId the groupId to search for
   * @param authToken bearer token to use for authentication
   * @return List of download links
   * @throws RESTSearchResponseException if an error occurred
   * @throws JsonProcessingException if the json processing was not possible
   * @throws JsonMappingException if the json mapping was not possible
   * @throws MalformedURLException if an URL was malformed
   *
   */
  public static List<URL> getArtifactDownloadLinks(String baseURL, String groupId, String authToken)
      throws RESTSearchResponseException, JsonMappingException, JsonProcessingException, MalformedURLException {

    ObjectMapper mapper = new ObjectMapper();
    List<URL> downloadLinks = null;
    List<SearchResponse> availableSearchInterfaces = getAvailableSearchInterfaces();

    for (SearchResponse searchResponse : availableSearchInterfaces) {
      try {
        LOG.debug("Trying to get a response from {} with server URL: {} ...", searchResponse.getRepositoryType(),
            baseURL);
        String jsonResponse = searchResponse.getJsonResponse(baseURL, groupId, authToken);
        SearchResponse response = mapper.readValue(jsonResponse, searchResponse.getClass());
        return response.getDownloadURLs();
      } catch (RESTSearchResponseException e) {
        LOG.debug("It was not possible to get a response from {} using the URL: {}.\n Following error occured:\n {}",
            searchResponse.getRepositoryType(), baseURL, e.getMessage());
      } catch (ProcessingException e) {
        String errorMsg = "The search REST API was not able to process the URL: " + baseURL;
        LOG.error(errorMsg, e);
        throw new CobiGenRuntimeException(errorMsg, e);
      }
    }

    return downloadLinks;
  }

  /**
   * Creates a @WebTarget with provided authentication token
   *
   * @param targetLink link to get response from
   * @param token bearer token to use for authentication
   * @return WebTarget to use as resource
   */
  public static WebTarget bearerAuthenticationWithOAuth2AtClientLevel(String targetLink, String token) {

    Feature feature = OAuth2ClientSupport.feature(token);
    Client client = ClientBuilder.newBuilder().register(feature).build();

    WebTarget target = client.target(targetLink);
    return target;
  }

  /**
   * Gets a json response by given REST API target link using bearer authentication token
   *
   * @param targetLink link to get response from
   * @param authToken bearer token to use for authentication
   * @return String of json response
   * @throws RESTSearchResponseException if the returned status code was not 200 OK
   */
  public static String getJsonResponseStringByTargetLink(String targetLink, String authToken)
      throws RESTSearchResponseException {

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
      throw new RESTSearchResponseException("The search REST API returned the unexpected status code: ",
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
package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;

import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;

import com.devonfw.cobigen.api.exception.RESTSearchResponseException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * Utility class for search REST API requests handling
 *
 */
public class SearchResponseUtil {

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

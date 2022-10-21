package com.devonfw.cobigen.retriever.mavensearch.util.to.model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.retriever.mavensearch.exception.RestSearchResponseException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This interface should be inherited for all maven REST search API responses to properly convert {@link JsonProperty}
 * from responses to valid template-set.xml download URLs
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
   * Creates a list of download URLs containing only template-set.xml files
   *
   * @return List of download links
   * @throws MalformedURLException if an URL was not valid
   */
  public abstract List<URL> retrieveTemplateSetXmlDownloadURLs() throws MalformedURLException;

  /**
   * Retrieves the target link of the respective REST search API
   *
   * @param repositoryUrl the repository server URL
   * @param groupId the groupId to search for
   * @return the REST search API target link
   */
  public abstract String retrieveRestSearchApiTargetLink(String repositoryUrl, String groupId);

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
   * Retrieves the json response from a repository URL, a group ID and a bearer authentication token
   *
   * @param serverCredentials to use
   * @param groupId to search for
   *
   * @return String of json response
   * @throws RestSearchResponseException if the request did not return status 200
   */
  public abstract String retrieveJsonResponse(ServerCredentials serverCredentials, String groupId)
      throws RestSearchResponseException;

  /**
   * Creates a @Request with provided authentication username and password
   *
   * @param targetLink link to get response from
   * @param username to use for authentication
   * @param password to use for authentication
   * @return Request to use as resource
   */
  private static Request basicUsernamePasswordAuthentication(String targetLink, String username, String password) {

    String credential = Credentials.basic(username, password);
    return new Request.Builder().url(targetLink).addHeader("Authorization", credential).build();

  }

  /**
   * Retrieves a json response by given REST API target link using authentication
   *
   * @param targetLink link to get response from
   * @param searchRepositoryType the type of the search repository
   * @param serverCredentials to use for connection and authentication with the server
   * @return String of json response
   * @throws RestSearchResponseException if the returned status code was not 200 OK
   */
  public static String retrieveJsonResponseWithAuthentication(String targetLink,
      MavenSearchRepositoryType searchRepositoryType, ServerCredentials serverCredentials)
      throws RestSearchResponseException {

    LOG.debug("Starting {} search REST API request with URL: {}.", searchRepositoryType, targetLink);

    OkHttpClient.Builder builder = new OkHttpClient.Builder();

    builder.connectTimeout(10, TimeUnit.SECONDS);
    builder.readTimeout(30, TimeUnit.SECONDS);
    builder.callTimeout(30, TimeUnit.SECONDS);
    builder.writeTimeout(30, TimeUnit.SECONDS);
    builder.retryOnConnectionFailure(true);

    if (serverCredentials.getProxyAddress() != null && serverCredentials.getProxyPort() != 0) {
      LOG.debug("Connecting to REST API using proxy with host address: {} and port: {}",
          serverCredentials.getProxyAddress(), serverCredentials.getProxyPort());
      SocketAddress address = new InetSocketAddress(serverCredentials.getProxyAddress(),
          serverCredentials.getProxyPort());
      Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
      builder.proxy(proxy);
    }

    OkHttpClient httpClient = builder.build();

    String jsonResponse = "";

    try {
      // use no authentication
      Response response = null;

      Request request = new Request.Builder().url(targetLink).get().build();

      boolean usingBasicAuth = false;
      // use basic authentication
      if (serverCredentials.getUsername() != null && serverCredentials.getPassword() != null) {
        LOG.debug("Connecting to REST API using Basic Authentication.");
        request = basicUsernamePasswordAuthentication(targetLink, serverCredentials.getUsername(),
            serverCredentials.getPassword());
        usingBasicAuth = true;
      }

      response = httpClient.newCall(request).execute();

      if (response != null) {
        int statusCode = response.code();
        if (statusCode == 200 || statusCode == 201 || statusCode == 204) {
          if (usingBasicAuth) {
            LOG.debug("The connection to the REST API using Basic Authentication was successful");
          }

          jsonResponse = response.body().string();
        } else if (statusCode == 401) {
          LOG.debug("{} {} {} {}", MavenSearchRepositoryConstants.MAVEN_SEARCH_API_EXCEPTION_AUTH_FAILED_ONE,
              searchRepositoryType, MavenSearchRepositoryConstants.MAVEN_SEARCH_API_EXCEPTION_AUTH_FAILED_TWO,
              targetLink);
          throw new RestSearchResponseException(searchRepositoryType, targetLink, statusCode);
        } else {
          throw new RestSearchResponseException(searchRepositoryType, targetLink, statusCode);
        }

      }

    } catch (IOException e) {
      LOG.debug("{} {}", MavenSearchRepositoryConstants.MAVEN_SEARCH_API_EXCEPTION_REQUEST_FAILED, targetLink, e);
      return jsonResponse;
    } catch (IllegalArgumentException e) {
      LOG.debug("The search REST API recieved the faulty target URL: {}", targetLink, e);
      return jsonResponse;
    }

    if (jsonResponse.isEmpty()) {
      LOG.debug("{} {}", MavenSearchRepositoryConstants.MAVEN_SEARCH_API_EXCEPTION_EMPTY_JSON_RESPONSE, targetLink);
      return jsonResponse;
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
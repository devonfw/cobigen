package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.devonfw.cobigen.api.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.api.exception.RESTSearchResponseException;

/**
 *
 *
 */
public interface SearchResponse {

  /**
   * @return the {@link MavenSearchRepositoryType} type
   */
  public MavenSearchRepositoryType getRepositoryType();

  /**
   * Creates a list of download links
   *
   * @return List of download links
   * @throws MalformedURLException if an URL was not valid
   */
  List<URL> getDownloadURLs() throws MalformedURLException;

  /**
   * Gets the json response
   *
   * @param repositoryUrl URL of the repository
   * @param groupId to search for
   * @return String of json response
   * @throws RESTSearchResponseException if the request did not return status 200
   */
  String getJsonResponse(String repositoryUrl, String groupId) throws RESTSearchResponseException;

  /**
   * Gets the json response using bearer authentication token
   *
   * @param repositoryUrl URL of the repository
   * @param groupId to search for
   * @param authToken bearer token to use for authentication
   * @return String of json response
   * @throws RESTSearchResponseException if the request did not return status 200
   */
  String getJsonResponse(String repositoryUrl, String groupId, String authToken) throws RESTSearchResponseException;

}
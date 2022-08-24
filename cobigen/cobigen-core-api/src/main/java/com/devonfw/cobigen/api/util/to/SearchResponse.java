package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.devonfw.cobigen.api.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.api.exception.RestSearchResponseException;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This interface should be inherited for all maven REST search API responses to properly convert {@link JsonProperty}
 * from responses to valid download URLs
 */
public interface SearchResponse {

  /**
   * @return the {@link MavenSearchRepositoryType} type
   */
  public MavenSearchRepositoryType getRepositoryType();

  /**
   * Creates a list of download URLs
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
   * @throws RestSearchResponseException if the request did not return status 200
   */
  String getJsonResponse(String repositoryUrl, String groupId) throws RestSearchResponseException;

  /**
   * Gets the json response using bearer authentication token
   *
   * @param repositoryUrl URL of the repository
   * @param groupId to search for
   * @param authToken bearer token to use for authentication
   * @return String of json response
   * @throws RestSearchResponseException if the request did not return status 200
   */
  String getJsonResponse(String repositoryUrl, String groupId, String authToken) throws RestSearchResponseException;

}
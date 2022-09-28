package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.api.exception.RestSearchResponseException;
import com.devonfw.cobigen.api.util.to.jfrog.JfrogSearchResponse;
import com.devonfw.cobigen.api.util.to.maven.MavenSearchResponse;
import com.devonfw.cobigen.api.util.to.nexus2.Nexus2SearchResponse;
import com.devonfw.cobigen.api.util.to.nexus3.Nexus3SearchResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

/**
 * Factory to create new instances of {@link AbstractSearchResponse} which handles the responses from various search
 * REST APIs.
 */
public class SearchResponseFactory {

  /** Logger instance. */
  static final Logger LOG = LoggerFactory.getLogger(SearchResponseFactory.class);

  /**
   * List of available {@link AbstractSearchResponse} implementations (add new search REST API responses here)
   */
  private static final List<Object> SEARCH_RESPONSES = Lists.newArrayList(new MavenSearchResponse(),
      new JfrogSearchResponse(), new Nexus2SearchResponse(), new Nexus3SearchResponse());

  /**
   * Searches for the maven artifact download links by given base URL, groupId and optional authentication token
   *
   * @param baseURL String of the repository server URL
   * @param username to use for authentication
   * @param password to use for authentication
   * @param groupId the groupId to search for
   * @return List of download URLs
   * @throws RestSearchResponseException if an error occurred while accessing the server
   * @throws JsonProcessingException if the json processing was not possible
   * @throws MalformedURLException if an URL was malformed
   *
   */
  public static List<URL> searchArtifactDownloadLinks(String baseURL, String username, String password, String groupId)
      throws RestSearchResponseException, JsonProcessingException, MalformedURLException {

    ObjectMapper mapper = new ObjectMapper();
    List<URL> downloadLinks = null;
    MavenSearchRepositoryType searchRepositoryType = null;

    for (Object searchResponse : SEARCH_RESPONSES) {
      searchRepositoryType = ((AbstractSearchResponse) searchResponse).getRepositoryType();
      try {
        LOG.debug("Trying to get a response from {} ...", searchRepositoryType);
        String jsonResponse = ((AbstractSearchResponse) searchResponse).retrieveJsonResponse(baseURL, username,
            password, groupId);
        AbstractSearchResponse response = (AbstractSearchResponse) mapper.readValue(jsonResponse,
            searchResponse.getClass());
        LOG.debug("The search REST API was able to get a response from {}", searchRepositoryType);
        return response.retrieveTemplateSetXmlDownloadURLs();
      } catch (RestSearchResponseException e) {
        LOG.debug("The search REST API was unable to get a response from {}", searchRepositoryType);
      }
    }

    return downloadLinks;
  }

}
package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.RestSearchResponseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import jakarta.ws.rs.ProcessingException;

/**
 * Factory to create new instances of {@link SearchResponse} which handles the responses from various search REST APIs.
 */
public class SearchResponseFactory {

  /** Logger instance. */
  static final Logger LOG = LoggerFactory.getLogger(SearchResponseFactory.class);

  /**
   * List of available {@link SearchResponse} implementations (add new search REST API responses here)
   */
  private static final List<SearchResponse> SEARCH_RESPONSES = Lists.newArrayList(new MavenSearchResponse(),
      new JfrogSearchResponse(), new Nexus2SearchResponse(), new Nexus3SearchResponse());

  /**
   * Searches for the maven artifact download links by given base URL, groupId and optional authentication token
   *
   * @param baseURL String of the repository server URL
   * @param groupId the groupId to search for
   * @param authToken bearer token to use for authentication
   * @return List of download URLs
   * @throws RestSearchResponseException if an error occurred while accessing the server
   * @throws JsonProcessingException if the json processing was not possible
   * @throws JsonMappingException if the json mapping was not possible
   * @throws MalformedURLException if an URL was malformed
   *
   */
  public static List<URL> searchArtifactDownloadLinks(String baseURL, String groupId, String authToken)
      throws RestSearchResponseException, JsonMappingException, JsonProcessingException, MalformedURLException {

    ObjectMapper mapper = new ObjectMapper();
    List<URL> downloadLinks = null;

    for (SearchResponse searchResponse : SEARCH_RESPONSES) {
      try {
        LOG.debug("Trying to get a response from {} with server URL: {} ...", searchResponse.getRepositoryType(),
            baseURL);
        String jsonResponse = searchResponse.getJsonResponse(baseURL, groupId, authToken);
        SearchResponse response = mapper.readValue(jsonResponse, searchResponse.getClass());
        return response.getDownloadURLs();
      } catch (RestSearchResponseException e) {
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

}
package com.devonfw.cobigen.api.util.to;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.RESTSearchResponseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.ProcessingException;

/**
 * Handles the responses from various search REST API's
 *
 */
public class SearchResponseFactory {

  /** Logger instance. */
  static final Logger LOG = LoggerFactory.getLogger(SearchResponseFactory.class);

  /**
   * Gets a list of available search REST APIs (register a new search interface type here)
   *
   * @return list of available {@link SearchResponse}
   */
  static List<SearchResponse> getAvailableSearchInterfaces() {

    List<SearchResponse> availableSearchInterfaces = new ArrayList<>();
    availableSearchInterfaces.add(new MavenSearchResponse());
    availableSearchInterfaces.add(new JfrogSearchResponse());
    availableSearchInterfaces.add(new Nexus2SearchResponse());
    availableSearchInterfaces.add(new Nexus3SearchResponse());
    return availableSearchInterfaces;
  }

  /**
   * Gets the maven artifact download links by given base URL, groupId and optional authentication token
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

}
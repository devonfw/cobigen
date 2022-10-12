package com.devonfw.cobigen.retriever.mavensearch.util.to.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.retriever.mavensearch.exception.RestSearchResponseException;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.jfrog.JfrogSearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.maven.MavenSearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.nexus2.Nexus2SearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.nexus3.Nexus3SearchResponse;
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
   * @throws CobiGenRuntimeException if an unexpected error occurred
   *
   */
  public static List<URL> searchArtifactDownloadLinks(String baseURL, String username, String password, String groupId)
      throws CobiGenRuntimeException {

    ObjectMapper mapper = new ObjectMapper();
    List<URL> downloadLinks = null;
    MavenSearchRepositoryType searchRepositoryType = null;
    String searchRepositoryTargetLink = "";

    LOG.debug("Starting search for REST APIs with repository URL: {} and groupId: {} ...", baseURL, groupId);

    for (Object searchResponse : SEARCH_RESPONSES) {
      searchRepositoryType = ((AbstractSearchResponse) searchResponse).getRepositoryType();
      searchRepositoryTargetLink = ((AbstractSearchResponse) searchResponse).retrieveRestSearchApiTargetLink(baseURL,
          groupId);
      try {
        LOG.debug("Trying to get a response from {} ...", searchRepositoryType);

        String jsonResponse = ((AbstractSearchResponse) searchResponse).retrieveJsonResponse(baseURL, username,
            password, groupId);
        AbstractSearchResponse response = (AbstractSearchResponse) mapper.readValue(jsonResponse,
            searchResponse.getClass());

        LOG.debug("The search REST API was able to get a response from {}", searchRepositoryType);

        downloadLinks = response.retrieveTemplateSetXmlDownloadURLs();

        if (downloadLinks == null || downloadLinks.isEmpty()) {
          throw new CobiGenRuntimeException(
              MavenSearchRepositoryConstants.MAVEN_SEARCH_API_EXCEPTION_ARTIFACT_LIST_EMPTY + " " + searchRepositoryType
                  + " repository matching the group id: " + groupId + " while using the URL: "
                  + searchRepositoryTargetLink);
        }

        return downloadLinks;

      } catch (RestSearchResponseException e) {
        LOG.debug("The search REST API was unable to get a response from {}", searchRepositoryType);

        if (e.getStatusCode() == 401) {
          throw new CobiGenRuntimeException(
              MavenSearchRepositoryConstants.MAVEN_SEARCH_API_EXCEPTION_AUTH_FAILED_ONE + " " + searchRepositoryType
                  + " " + MavenSearchRepositoryConstants.MAVEN_SEARCH_API_EXCEPTION_AUTH_FAILED_TWO + " "
                  + searchRepositoryTargetLink,
              e);
        }

      } catch (JsonProcessingException e) {
        throw new CobiGenRuntimeException(MavenSearchRepositoryConstants.MAVEN_SEARCH_API_EXCEPTION_UNABLE_TO_PARSE_JSON
            + " " + searchRepositoryType + " repository using the URL: " + searchRepositoryTargetLink, e);
      } catch (MalformedURLException e) {
        throw new CobiGenRuntimeException(MavenSearchRepositoryConstants.MAVEN_SEARCH_API_EXCEPTION_FAULTY_TARGET_URL
            + " " + searchRepositoryType + " repository using the URL: " + searchRepositoryTargetLink, e);
      }
    }

    return downloadLinks;
  }

}
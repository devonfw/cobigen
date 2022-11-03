package com.devonfw.cobigen.retriever.mavensearch.to.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.retriever.mavensearch.exception.RestSearchResponseException;
import com.devonfw.cobigen.retriever.mavensearch.to.model.jfrog.JfrogSearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.to.model.maven.MavenSearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.to.model.nexus2.Nexus2SearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.to.model.nexus3.Nexus3SearchResponse;
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
   * Searches for the maven artifact download links by given server credentials and groupId
   *
   * @param serverCredentials to use for connection and authentication
   * @param groupId to search for
   *
   * @return List of download URLs
   */
  public static List<URL> searchArtifactDownloadLinks(ServerCredentials serverCredentials, String groupId) {

    ObjectMapper mapper = new ObjectMapper();
    List<URL> downloadLinks = new ArrayList<>();
    MavenSearchRepositoryType searchRepositoryType = null;
    String searchRepositoryTargetLink = "";

    if (serverCredentials == null) {
      LOG.debug("No server credentials were provided.");
      return downloadLinks;
    }

    if (serverCredentials.getBaseUrl() == null || serverCredentials.getBaseUrl().isEmpty()) {
      LOG.debug("The server credentials are missing the base URL.");
      return downloadLinks;
    }

    String baseUrl = serverCredentials.getBaseUrl();

    LOG.debug("Starting search for REST APIs with repository URL: {} and groupId: {} ...", baseUrl, groupId);

    for (Object searchResponse : SEARCH_RESPONSES) {
      searchRepositoryType = ((AbstractSearchResponse) searchResponse).getRepositoryType();
      searchRepositoryTargetLink = ((AbstractSearchResponse) searchResponse).retrieveRestSearchApiTargetLink(baseUrl,
          groupId);
      try {
        LOG.debug("Trying to get a response from {} ...", searchRepositoryType);

        String jsonResponse = ((AbstractSearchResponse) searchResponse).retrieveJsonResponse(serverCredentials,
            groupId);

        if (jsonResponse == null || jsonResponse.isEmpty()) {
          LOG.debug("The json response was empty.");
          return downloadLinks;
        }

        AbstractSearchResponse response = (AbstractSearchResponse) mapper.readValue(jsonResponse,
            searchResponse.getClass());

        LOG.debug("The search REST API was able to get a response from {}", searchRepositoryType);

        downloadLinks = response.retrieveTemplateSetXmlDownloadURLs();

        if (downloadLinks == null) {
          LOG.debug("{} {} repository matching the group id: {} while using the URL: {}",
              MavenSearchRepositoryConstants.MAVEN_SEARCH_API_EXCEPTION_ARTIFACT_LIST_EMPTY, searchRepositoryType,
              groupId, searchRepositoryTargetLink);
          return new ArrayList<>();
        }

        return downloadLinks;

      } catch (RestSearchResponseException e) {
        LOG.debug("The search REST API was unable to get a response from {}", searchRepositoryType);
        if (SEARCH_RESPONSES.indexOf(searchResponse) != (SEARCH_RESPONSES.size() - 1)) {
          LOG.debug("Trying to get a response from another search REST API type.");
        }
      } catch (JsonProcessingException e) {
        LOG.debug("{} repository using the URL: {}",
            MavenSearchRepositoryConstants.MAVEN_SEARCH_API_EXCEPTION_UNABLE_TO_PARSE_JSON, searchRepositoryType,
            searchRepositoryTargetLink, e);
        return downloadLinks;
      } catch (MalformedURLException e) {
        LOG.debug("{} {} repository using the URL: {}",
            MavenSearchRepositoryConstants.MAVEN_SEARCH_API_EXCEPTION_FAULTY_TARGET_URL, searchRepositoryType,
            searchRepositoryTargetLink, e);
        return downloadLinks;
      }
    }

    return downloadLinks;
  }

}
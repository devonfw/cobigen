package com.devonfw.cobigen.retriever.mavensearch.to.model.nexus3;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryType;
import com.devonfw.cobigen.retriever.mavensearch.exception.RestSearchResponseException;
import com.devonfw.cobigen.retriever.mavensearch.to.model.AbstractSearchResponse;
import com.devonfw.cobigen.retriever.mavensearch.to.model.ServerCredentials;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Json model for nexus3 Search REST API response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Nexus3SearchResponse extends AbstractSearchResponse {

  /** Logger instance. */
  @JsonIgnore
  private static final Logger LOG = LoggerFactory.getLogger(Nexus3SearchResponse.class);

  /** items */
  @JsonProperty("items")
  private List<Nexus3SearchResponseItem> items;

  @Override
  @JsonIgnore
  public List<URL> retrieveTemplateSetXmlDownloadURLs() throws MalformedURLException {

    List<URL> downloadLinks = new ArrayList<>();

    if (this.items == null) {
      LOG.debug("The {} response was empty.", getRepositoryType());
      return downloadLinks;
    }

    for (Nexus3SearchResponseItem item : this.items) {
      for (Nexus3SearchResponseAsset asset : item.assets) {
        if (asset.downloadUrl.endsWith(ConfigurationConstants.TEMPLATE_SET_CONFIG_FILENAME)) {
          downloadLinks.add(new URL(asset.downloadUrl));
        }
      }
    }

    return removeDuplicatedDownloadURLs(downloadLinks);
  }

  @Override
  @JsonIgnore
  public String retrieveJsonResponse(ServerCredentials serverCredentials, String groupId)
      throws RestSearchResponseException {

    String targetLink = retrieveRestSearchApiTargetLink(serverCredentials.getBaseUrl(), groupId);

    return retrieveJsonResponseWithAuthentication(targetLink, getRepositoryType(), serverCredentials);
  }

  @Override
  public MavenSearchRepositoryType getRepositoryType() {

    return MavenSearchRepositoryType.NEXUS3;
  }

  @Override
  public String retrieveRestSearchApiTargetLink(String repositoryUrl, String groupId) {

    String rootUrl = AbstractSearchResponse.createRootURL(repositoryUrl);
    if (rootUrl != null) {
      return rootUrl + "/" + MavenSearchRepositoryConstants.NEXUS3_REST_SEARCH_API_PATH + "?repository=maven-central"
          + "&group=" + groupId;
    } else {
      return null;
    }
  }
}

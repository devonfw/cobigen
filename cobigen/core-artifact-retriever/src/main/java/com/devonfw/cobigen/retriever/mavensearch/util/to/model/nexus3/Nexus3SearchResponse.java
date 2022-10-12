package com.devonfw.cobigen.retriever.mavensearch.util.to.model.nexus3;

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
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.AbstractSearchResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Json model for nexus3 Search REST API response
 *
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
  public String retrieveJsonResponse(String repositoryUrl, String username, String password, String groupId,
      String proxyAddress, int proxyPort) throws RestSearchResponseException {

    String targetLink = retrieveRestSearchApiTargetLink(repositoryUrl, groupId);

    return retrieveJsonResponseWithAuthentication(targetLink, username, password, getRepositoryType(), proxyAddress,
        proxyPort);
  }

  @Override
  public MavenSearchRepositoryType getRepositoryType() {

    return MavenSearchRepositoryType.NEXUS3;
  }

  @Override
  public String retrieveRestSearchApiTargetLink(String repositoryUrl, String groupId) {

    return repositoryUrl + "/" + MavenSearchRepositoryConstants.NEXUS3_REST_SEARCH_API_PATH
        + "?repository=maven-central" + "&group=" + groupId;
  }
}

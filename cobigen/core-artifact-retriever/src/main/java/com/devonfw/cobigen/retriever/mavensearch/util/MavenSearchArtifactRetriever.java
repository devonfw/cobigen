package com.devonfw.cobigen.retriever.mavensearch.util;

import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.retriever.mavensearch.util.to.model.SearchResponseFactory;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.ServerCredentials;

/**
 * Class for handling the Maven REST Search API and the communication with it.
 *
 */
public class MavenSearchArtifactRetriever {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(MavenSearchArtifactRetriever.class);

  /** List of artifact download URLs */
  private List<URL> mavenArtifactDownloadUrls;

  /**
   *
   * The constructor.
   *
   * @param baseUrl String of the repository server URL
   * @param username to use for authentication
   * @param password to use for authentication
   * @param proxyAddress the address of the proxy server
   * @param proxyPort the port of the proxy server
   * @param groupId the groupId to search for
   */
  public MavenSearchArtifactRetriever(String baseUrl, String username, String password, String proxyAddress,
      int proxyPort, String groupId) {

    ServerCredentials serverCredentials = new ServerCredentials(baseUrl, username, password, proxyAddress, proxyPort);

    this.mavenArtifactDownloadUrls = SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, groupId);
  }

  /**
   * @return mavenArtifactDownloadUrls
   */
  public List<URL> getMavenArtifactDownloadUrls() {

    return this.mavenArtifactDownloadUrls;
  }

}

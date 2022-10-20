package com.devonfw.cobigen.retriever.mavensearch.util;

import java.net.URL;
import java.util.List;

import com.devonfw.cobigen.retriever.mavensearch.util.to.model.SearchResponseFactory;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.ServerCredentials;

/**
 * Class for handling the Maven REST Search API and the communication with it.
 *
 */
public class MavenSearchArtifactRetriever {

  /**
   * Retrieves a list of maven artifact download URLs from available REST search APIs based on the provided groupId and
   * server credentials.
   *
   * Supports: Basic authentication (using username and password), Connection through a proxy server (using proxy URL
   * and a port)
   *
   * @param baseUrl String of the repository server URL
   * @param username to use for authentication
   * @param password to use for authentication
   * @param proxyAddress the address of the proxy server
   * @param proxyPort the port of the proxy server
   * @param groupId the groupId to search for
   *
   * @return List of artifact download URLs
   */
  public static List<URL> retrieveMavenArtifactDownloadUrls(String baseUrl, String username, String password,
      String proxyAddress, int proxyPort, String groupId) {

    ServerCredentials serverCredentials = new ServerCredentials(baseUrl, username, password, proxyAddress, proxyPort);

    return SearchResponseFactory.searchArtifactDownloadLinks(serverCredentials, groupId);
  }

}

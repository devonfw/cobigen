package com.devonfw.cobigen.retriever.mavensearch.util;

import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.retriever.mavensearch.util.to.model.SearchResponseFactory;

/**
 * Utils for maven artifacts
 *
 */
public class MavenArtifactsUtil {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(MavenArtifactsUtil.class);

  /**
   * Retrieves a list of download URLs by groupId from the specified repository search REST API using authentication
   * with bearer token
   *
   * @param baseUrl String of the repository server URL
   * @param username to use for authentication
   * @param password to use for authentication
   * @param groupId the groupId to search for
   * @param proxyAddress TODO
   * @param proxyPort TODO
   * @return List of artifact download URLS
   */
  public static List<URL> retrieveMavenArtifactsByGroupId(String baseUrl, String username, String password,
      String groupId, String proxyAddress, int proxyPort) {

    try {
      return SearchResponseFactory.searchArtifactDownloadLinks(baseUrl, username, password, groupId, proxyAddress,
          proxyPort);
    } catch (CobiGenRuntimeException e) {
      LOG.warn("An exception occured: ", e);
    }
    return null;

  }

}

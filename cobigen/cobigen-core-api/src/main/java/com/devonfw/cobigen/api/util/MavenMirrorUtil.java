package com.devonfw.cobigen.api.util;

import java.net.MalformedURLException;
import java.net.URL;

import com.devonfw.cobigen.api.to.model.MavenSettingsRepositoryModel;

/**
 * Utils to operate with mirrors specified in maven's settings.xml
 */
public class MavenMirrorUtil {

  private static final String WILDCARD = "*";

  private static final String EXTERNAL_WILDCARD = "external:*";

  private static final String EXTERNAL_HTTP_WILDCARD = "external:http:*";

  /**
   * This method checks if the pattern of the mirror (mirrorOf) matches the repository. Valid patterns:
   * <ul>
   * <li>{@code *} = everything,</li>
   * <li>{@code external:*} = everything not on the localhost and not file based,</li>
   * <li>{@code external:http:*} = any repository not on the localhost using HTTP,</li>
   * <li>{@code repo,repo1} = {@code repo} or {@code repo1},</li>
   * <li>{@code *,!repo1} = everything except {@code repo1}.</li>
   * </ul>
   *
   * @return true if the repository is a match to this pattern.
   */
  public static boolean matchPattern(MavenSettingsRepositoryModel repository, String pattern) {

    boolean result = false;
    String originalId = repository.getId();

    // simple checks first to short circuit processing below.
    if (WILDCARD.equals(pattern) || pattern.equals(originalId)) {
      result = true;
    } else {
      // process the list
      String[] repos = pattern.split(",");
      for (String repo : repos) {
        repo = repo.trim();

        // see if this is a negative match
        if (repo.length() > 1 && repo.startsWith("!")) {
          if (repo.substring(1).equals(originalId)) {
            // explicitly exclude. Set result and stop processing.
            result = false;
            break;
          }
        }
        // check for exact match
        else if (repo.equals(originalId)) {
          result = true;
          break;
        }
        // check for external:*
        else if (EXTERNAL_WILDCARD.equals(repo) && isExternalRepo(repository)) {
          result = true;
          // don't stop processing in case a future segment explicitly excludes this repo
        }
        // check for external:http:*
        else if (EXTERNAL_HTTP_WILDCARD.equals(repo) && isExternalHttpRepo(repository)) {
          result = true;
          // don't stop processing in case a future segment explicitly excludes this repo
        } else if (WILDCARD.equals(repo)) {
          result = true;
          // don't stop processing in case a future segment explicitly excludes this repo
        }
      }
    }
    return result;
  }

  /**
   * Checks the URL to see if this repository refers to an external repository
   *
   * @param repository
   * @return true if external.
   */
  private static boolean isExternalRepo(MavenSettingsRepositoryModel repository) {

    try {
      URL url = new URL(repository.getUrl().toString());
      return !(isLocal(url.getHost()) || url.getProtocol().equals("file"));
    } catch (MalformedURLException e) {
      // bad url just skip it here. It should have been validated already, but the wagon lookup will deal with it
      return false;
    }
  }

  private static boolean isLocal(String host) {

    return "localhost".equals(host) || "127.0.0.1".equals(host);
  }

  /**
   * Checks the URL to see if this repository refers to a non-localhost repository using HTTP.
   *
   * @param repository
   * @return true if external.
   */
  private static boolean isExternalHttpRepo(MavenSettingsRepositoryModel repository) {

    try {
      URL url = new URL(repository.getUrl().toString());
      return ("http".equalsIgnoreCase(url.getProtocol()) || "dav".equalsIgnoreCase(url.getProtocol())
          || "dav:http".equalsIgnoreCase(url.getProtocol()) || "dav+http".equalsIgnoreCase(url.getProtocol()))
          && !isLocal(url.getHost());
    } catch (MalformedURLException e) {
      // bad url just skip it here. It should have been validated already, but the wagon lookup will deal with it
      return false;
    }
  }
}

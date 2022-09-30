package com.devonfw.cobigen.api.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.devonfw.cobigen.api.to.model.MavenSettingsMirrorModel;
import com.devonfw.cobigen.api.to.model.MavenSettingsRepositoryModel;

/**
 * Utils to operate with mirrors specified in maven's settings.xml
 */
public class MavenMirrorUtil {

  private static final String WILDCARD = "*";

  private static final String EXTERNAL_WILDCARD = "external:*";

  private static final String EXTERNAL_HTTP_WILDCARD = "external:http:*";

  /**
   * Replaces urls of repositories with the urls of their mirrors if available
   *
   * @param repositories list of repositories of maven's settings.xml
   * @param mirrors list of mirrors of maven's settings.xml
   */
  /*
   * Important: The order of the mirrors is important! There can be at most one mirror for a given repository. Maven
   * will not aggregate the mirrors but simply picks the first match. Take a look here:
   * https://maven.apache.org/guides/mini/guide-mirror-settings.html
   */
  public static void injectMirrorUrl(List<MavenSettingsRepositoryModel> repositories,
      List<MavenSettingsMirrorModel> mirrors) {

    for (MavenSettingsRepositoryModel r : repositories) {
      for (MavenSettingsMirrorModel m : mirrors) {
        if (matchPattern(r, m.getMirrorOf())) {
          r.setUrl(m.getUrl());
          break;
        }
      }
    }
  }

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
   * @param repository repository which will be checked
   * @param pattern pattern which will be used to determine if the repository id matches this pattern
   *
   * @return true if the repository is a match to this pattern.
   */
  private static boolean matchPattern(MavenSettingsRepositoryModel repository, String pattern) {

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
      return false;
    }
  }
}

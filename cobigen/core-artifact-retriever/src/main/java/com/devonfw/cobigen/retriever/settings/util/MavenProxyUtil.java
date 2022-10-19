package com.devonfw.cobigen.retriever.settings.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsProxyModel;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsRepositoryModel;

/**
 * Utils to operate with proxys specified in maven's settings.xml
 */
public class MavenProxyUtil {

  /**
   *
   * @param repositories all active repositories
   * @param proxy the active proxy
   * @param withProxies boolean when set true it delivers repositories with proxies, when set false otherwise
   * @return a list of all repositories, which are using the given proxy
   */
  public static List<MavenSettingsRepositoryModel> obtainRepositories(List<MavenSettingsRepositoryModel> repositories,
      MavenSettingsProxyModel proxy, boolean withProxies) {

    List<MavenSettingsRepositoryModel> result = new ArrayList<>();

    for (MavenSettingsRepositoryModel r : repositories) {
      if (withProxies) {
        if (!validateNonProxyHosts(proxy, r.getUrl())) {
          result.add(r);
        }
      } else {
        if (validateNonProxyHosts(proxy, r.getUrl())) {
          result.add(r);
        }
      }
    }
    return result;
  }

  /**
   * Code was taken from here:
   * https://maven.apache.org/wagon/apidocs/src-html/org/apache/maven/wagon/proxy/ProxyUtils.html#line.27
   *
   * Check if the specified host is in the list of non proxy hosts.
   *
   * @param proxy the proxy info object contains set of properties.
   * @param targetHost the target hostname
   * @return true if the hostname is in the list of non proxy hosts, false otherwise.
   */
  private static boolean validateNonProxyHosts(MavenSettingsProxyModel proxy, String targetHost) {

    if (targetHost == null) {
      targetHost = new String();
    }
    if (proxy == null) {
      return false;
    }
    String nonProxyHosts = proxy.getNonProxyHosts();
    if (nonProxyHosts == null) {
      return false;
    }

    StringTokenizer tokenizer = new StringTokenizer(nonProxyHosts, "|");

    while (tokenizer.hasMoreTokens()) {
      String pattern = tokenizer.nextToken();
      pattern = pattern.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*");
      if (targetHost.matches(pattern)) {
        return true;
      }
    }
    return false;
  }

}

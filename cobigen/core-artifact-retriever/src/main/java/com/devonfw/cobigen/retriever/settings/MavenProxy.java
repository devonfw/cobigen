package com.devonfw.cobigen.retriever.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsProxyModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsRepositoryModel;

/**
 * Class to operate with {@link MavenSettingsProxyModel}s specified in maven's settings.xml
 */
public class MavenProxy {

  /**
   * Obtains a list of {@link MavenSettingsRepositoryModel} which are using the given {@link MavenSettingsProxyModel}
   *
   * @param repositories list of all active {@link MavenSettingsRepositoryModel}s
   * @param proxy the active {@link MavenSettingsProxyModel}
   * @param withProxies boolean when set true it delivers repositories with proxies, when set false otherwise
   * @return a list of all {@link MavenSettingsRepositoryModel}s, which are using the given proxy
   */
  public static List<MavenSettingsRepositoryModel> obtainRepositories(List<MavenSettingsRepositoryModel> repositories,
      MavenSettingsProxyModel proxy, boolean withProxies) {

    List<MavenSettingsRepositoryModel> result = new ArrayList<>();

    for (MavenSettingsRepositoryModel r : repositories) {
      if (withProxies == !validateNonProxyHosts(proxy, r.getUrl())) {
        result.add(r);
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

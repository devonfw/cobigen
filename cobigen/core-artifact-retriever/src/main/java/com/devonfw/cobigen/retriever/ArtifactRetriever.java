package com.devonfw.cobigen.retriever;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.util.MavenCoordinate;
import com.devonfw.cobigen.api.util.MavenUtil;
import com.devonfw.cobigen.retriever.mavensearch.MavenSearchArtifactRetriever;
import com.devonfw.cobigen.retriever.mavensearch.constants.MavenSearchRepositoryConstants;
import com.devonfw.cobigen.retriever.settings.MavenProxy;
import com.devonfw.cobigen.retriever.settings.MavenSettings;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsProxyModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsRepositoryModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsServerModel;

/**
 * Used to obtain maven artifact download URLs
 */
public class ArtifactRetriever {

  private static final Logger LOG = LoggerFactory.getLogger(ArtifactRetriever.class);

  /**
   * Retrieves template set jar download URLs by given maven group IDs and mavenCoordinates
   *
   * @param groupIds List of groupIds to search for download URLs
   * @param mavenCoordinates List of {@link MavenCoordinate}
   * @return List of download URLs as Strings
   */
  public static List<String> retrieveTemplateSetJarDownloadURLs(List<String> groupIds,
      List<MavenCoordinate> mavenCoordinates) {

    String mavenSettings = MavenUtil.determineMavenSettings();
    List<URL> downloadURLs = retrieveTemplateSetXmlDownloadLinks(groupIds, mavenSettings);

    List<String> templateSetJars = new ArrayList<>();

    for (MavenCoordinate mavenCoordinate : mavenCoordinates) {
      for (URL downloadURL : downloadURLs) {
        String jarURL = downloadURL.toString().replace("&e=xml", "&e=jar").replace("&c=template-set", "");
        if (jarURL.contains(mavenCoordinate.getArtifactId()) && jarURL.contains(mavenCoordinate.getVersion())) {
          templateSetJars.add(jarURL);
        }
        String sourcesURL = downloadURL.toString().replace("&e=xml", "&e=jar").replace("&c=template-set", "&c=sources");
        if (sourcesURL.contains(mavenCoordinate.getArtifactId()) && sourcesURL.contains(mavenCoordinate.getVersion())) {
          templateSetJars.add(sourcesURL);
        }
      }
    }

    return templateSetJars;

  }

  /**
   * Retrieves a list of maven artifact download URLs
   *
   * @param groupIdsList groupIds for template-sets
   * @param mavenSettings string of maven's settings.xml
   * @return list of maven artifact download URLs
   *
   */
  public static List<URL> retrieveTemplateSetXmlDownloadLinks(List<String> groupIdsList, String mavenSettings) {

    List<URL> downloadLinks = new ArrayList<>();

    MavenSettingsModel model = MavenSettings.generateMavenSettingsModel(mavenSettings);

    MavenSettingsProxyModel activeProxy = MavenSettings
        .determineActiveProxy(MavenSettings.generateMavenSettingsModel(mavenSettings));

    List<MavenSettingsRepositoryModel> allActiveRepositories = MavenSettings
        .retrieveRepositoriesFromMavenSettings(mavenSettings);

    List<MavenSettingsRepositoryModel> repositoriesWhichAreUsingTheProxy = MavenProxy
        .obtainRepositories(allActiveRepositories, activeProxy, true);

    List<MavenSettingsRepositoryModel> repositoriesWhichDoNotUseTheProxy = MavenProxy
        .obtainRepositories(allActiveRepositories, activeProxy, false);

    if (!repositoriesWhichAreUsingTheProxy.isEmpty()) {
      downloadLinks
          .addAll(retrieveArtifactsFromRepository(groupIdsList, model, activeProxy, repositoriesWhichAreUsingTheProxy));
    }

    if (!repositoriesWhichDoNotUseTheProxy.isEmpty()) {
      downloadLinks
          .addAll(retrieveArtifactsFromRepository(groupIdsList, model, activeProxy, repositoriesWhichDoNotUseTheProxy));
    }

    // Fallback if no repositories were found
    if (allActiveRepositories.isEmpty()) {
      LOG.debug("No repositories were found in settings.xml, using fallback.");
      downloadLinks.addAll(
          retrieveArtifactsFromRepository(groupIdsList, null, null, new ArrayList<MavenSettingsRepositoryModel>()));
    }
    return downloadLinks;
  }

  /**
   * Helper method to retrieve maven artifact download URLs
   *
   * @param groupIdsList grouIds for template-sets
   * @param model a class on which maven's settings.xml has been mapped to
   * @param activeProxy the currently active proxy
   * @param repositories local repositories from maven's settings.xml
   */
  private static List<URL> retrieveArtifactsFromRepository(List<String> groupIdsList, MavenSettingsModel model,
      MavenSettingsProxyModel activeProxy, List<MavenSettingsRepositoryModel> repositories) {

    List<URL> result = new ArrayList<>();

    for (MavenSettingsRepositoryModel repositoryModel : repositories) {
      if (model.getServers() == null) {
        return result;
      }
      MavenSettingsServerModel serverModel = getServerModel(model.getServers().getServerList(), repositoryModel);
      if (serverModel == null) {
        continue;
      }
      for (String groupID : groupIdsList) {
        if (activeProxy != null) {
          result.addAll(MavenSearchArtifactRetriever.retrieveMavenArtifactDownloadUrls(repositoryModel.getUrl(),
              serverModel.getUsername(), serverModel.getPassword(), activeProxy.getHost(),
              Integer.valueOf(activeProxy.getPort()), null, null, groupID));
        } else {
          result.addAll(MavenSearchArtifactRetriever.retrieveMavenArtifactDownloadUrls(repositoryModel.getUrl(),
              serverModel.getUsername(), serverModel.getPassword(), null, 0, null, null, groupID));
        }

      }
    }
    // Fallback if settings.xml was not usable (uses sonatype as default)
    if (result.isEmpty()) {
      LOG.warn(
          "Maven settings did not get any results, searching for artifacts using default sonatype fallback repository.");
      for (String groupID : groupIdsList) {
        result.addAll(MavenSearchArtifactRetriever.retrieveMavenArtifactDownloadUrls(
            MavenSearchRepositoryConstants.FALLBACK_REPOSITORY_URL, null, null, null, 0, null, null, groupID));
      }
      if (result.isEmpty())
        LOG.warn("No artifacts were found on the default sonatype fallback repository.");
    }
    return result;
  }

  /**
   * Helper method to determine the matching server for a repository
   *
   * @param servers from maven's settings.xml
   * @param repository for which a server is searched for
   * @return a server or null if none is found
   */
  private static MavenSettingsServerModel getServerModel(List<MavenSettingsServerModel> servers,
      MavenSettingsRepositoryModel repository) {

    for (MavenSettingsServerModel serverModel : servers) {
      if (serverModel.getId().equals(repository.getId())) {
        return serverModel;
      }
    }
    return null;
  }
}

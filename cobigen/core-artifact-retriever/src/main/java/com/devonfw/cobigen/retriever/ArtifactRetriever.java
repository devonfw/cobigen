package com.devonfw.cobigen.retriever;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.devonfw.cobigen.retriever.mavensearch.util.MavenSearchArtifactRetriever;
import com.devonfw.cobigen.retriever.settings.MavenProxy;
import com.devonfw.cobigen.retriever.settings.MavenSettings;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsProxyModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsRepositoryModel;
import com.devonfw.cobigen.retriever.settings.to.model.MavenSettingsServerModel;

/**
 * Utils to obtain maven artifact download URLs
 */
public class ArtifactRetriever {

  /**
   * Retrieves a list of maven artifact download URLs
   *
   * @param groupIdsList grouIds for template-sets
   * @param mavenSettings string of maven's settings.xml
   * @return list of maven artifact download URLs
   *
   */
  public static List<URL> retrieveTemplateSetXmlDownloadLinks(List<String> groupIdsList, String mavenSettings) {

    List<URL> downloadLinks = new ArrayList<>();

    MavenSettingsModel model = MavenSettings.generateMavenSettingsModel(mavenSettings);

    MavenSettingsProxyModel activeProxy = MavenSettings
        .getActiveProxy(MavenSettings.generateMavenSettingsModel(mavenSettings));

    List<MavenSettingsRepositoryModel> allActiveRepositories = MavenSettings
        .getRepositoriesFromMavenSettings(mavenSettings);

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
    return result;
  }

  /**
   * Helper method to determine the machting server for a repository
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

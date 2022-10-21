package com.devonfw.cobigen.retriever;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.devonfw.cobigen.retriever.mavensearch.util.MavenSearchArtifactRetriever;
import com.devonfw.cobigen.retriever.settings.util.MavenProxyUtil;
import com.devonfw.cobigen.retriever.settings.util.MavenSettingsUtil;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsModel;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsProxyModel;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsRepositoryModel;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsServerModel;

/**
 * TODO fberger This type ...
 *
 */
public class ArtifactRetriever {

  /**
   *
   */
  public List<URL> retrieveTemplateSetXmlDownloadLinks(List<String> groupIdsList, String mavenSettings) {

    List<URL> downloadLinks = new ArrayList<>();

    MavenSettingsModel model = MavenSettingsUtil.generateMavenSettingsModel(mavenSettings);

    MavenSettingsProxyModel activeProxy = MavenSettingsUtil
        .getActiveProxy(MavenSettingsUtil.generateMavenSettingsModel(mavenSettings));

    List<MavenSettingsRepositoryModel> allActiveRepositories = MavenSettingsUtil
        .getRepositoriesFromMavenSettings(mavenSettings);

    List<MavenSettingsRepositoryModel> repositoriesWhichAreUsingTheProxy = MavenProxyUtil
        .obtainRepositories(allActiveRepositories, activeProxy, true);

    List<MavenSettingsRepositoryModel> repositoriesWhichDoNotUseTheProxy = MavenProxyUtil
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
   * @param groupIdsList
   * @param downloadLinks
   * @param model
   * @param activeProxy
   * @param repositories
   */
  private List<URL> retrieveArtifactsFromRepository(List<String> groupIdsList, MavenSettingsModel model,
      MavenSettingsProxyModel activeProxy, List<MavenSettingsRepositoryModel> repositories) {

    List<URL> result = new ArrayList<>();

    for (MavenSettingsRepositoryModel repositoryModel : repositories) {
      MavenSettingsServerModel serverModel = getServerModel(model.getServers().getServerList(), repositoryModel);
      if (serverModel == null) {
        continue;
      }
      for (String groupID : groupIdsList) {
        if (activeProxy != null) {
          result.addAll(MavenSearchArtifactRetriever.retrieveMavenArtifactDownloadUrls(repositoryModel.getUrl(),
              serverModel.getUsername(), serverModel.getPassword(), activeProxy.getHost(),
              Integer.valueOf(activeProxy.getPort()), groupID));
        } else {
          result.addAll(MavenSearchArtifactRetriever.retrieveMavenArtifactDownloadUrls(repositoryModel.getUrl(),
              serverModel.getUsername(), serverModel.getPassword(), null, 0, groupID));
        }
      }
    }
    return result;
  }

  private static MavenSettingsServerModel getServerModel(List<MavenSettingsServerModel> servers,
      MavenSettingsRepositoryModel repository) {

    for (MavenSettingsServerModel serverModel : servers) {
      if (serverModel.getId() == repository.getId()) {
        return serverModel;
      }
    }
    return null;
  }
}

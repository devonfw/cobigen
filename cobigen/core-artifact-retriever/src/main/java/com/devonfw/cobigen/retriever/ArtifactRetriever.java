package com.devonfw.cobigen.retriever;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.devonfw.cobigen.api.util.MavenUtil;
import com.devonfw.cobigen.retriever.settings.util.MavenSettingsUtil;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsModel;
import com.devonfw.cobigen.retriever.settings.util.to.model.MavenSettingsRepositoryModel;

public class ArtifactRetriever {

  private List<String> groupIds;

  private String username;

  private String password;

  private String proxyAddress;

  private int proxyPort;

  private List<URL> templateSetXmlDownloadLinks;

  public ArtifactRetriever() {

    this.groupIds = new ArrayList<>();
    this.templateSetXmlDownloadLinks = new ArrayList<>();
  }

  public void retrieveTemplateSetXmlDownloadLinks() {

    List<MavenSettingsRepositoryModel> repositories = MavenSettingsUtil.getRepositoriesFromMavenSettings();

    MavenSettingsModel model = MavenSettingsUtil.generateMavenSettingsModel(MavenUtil.determineMavenSettings());

  }

  /**
   * @return templateSetXmlDownloadLinks
   */
  public List<URL> getTemplateSetXmlDownloadLinks() {

    return this.templateSetXmlDownloadLinks;
  }

}

package com.devonfw.cobigen.retriever.to.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Class, which represents the repositories element of the settings.xml
 *
 */

public class MavenSettingsRepositoriesModel {

  /**
   * Represents repository elements in maven's settings.xml
   */
  List<MavenSettingsRepositoryModel> repositoryList;

  /**
   * @return repository
   */
  public List<MavenSettingsRepositoryModel> getRepositoryList() {

    return this.repositoryList;
  }

  /**
   * @param repository new value of {@link #getrepository}.
   */
  @XmlElement(name = "repository")
  public void setRepositoryList(List<MavenSettingsRepositoryModel> repository) {

    this.repositoryList = repository;
  }

}
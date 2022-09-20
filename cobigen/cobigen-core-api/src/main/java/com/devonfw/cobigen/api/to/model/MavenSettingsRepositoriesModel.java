package com.devonfw.cobigen.api.to.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * Class, which represents the repositories element of the settings.xml
 *
 */

public class MavenSettingsRepositoriesModel {

  List<MavenSettingsRepositoryModel> repository;

  /**
   * @return repository
   */
  public List<MavenSettingsRepositoryModel> getRepository() {

    return this.repository;
  }

  /**
   * @param repository new value of {@link #getrepository}.
   */
  @XmlElement(name = "repository")
  public void setRepository(List<MavenSettingsRepositoryModel> repository) {

    this.repository = repository;
  }

}

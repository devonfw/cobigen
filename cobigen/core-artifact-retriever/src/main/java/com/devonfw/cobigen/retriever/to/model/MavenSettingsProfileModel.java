package com.devonfw.cobigen.retriever.to.model;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Class, which represents the profile element of the settings.xml
 *
 */
public class MavenSettingsProfileModel {

  /**
   * Represents the repositories element in maven's settings.xml
   */
  MavenSettingsRepositoriesModel repositories;

  /**
   * Id of the profile
   */
  String id;

  /**
   * @return repositories
   */
  public MavenSettingsRepositoriesModel getRepositories() {

    return this.repositories;
  }

  /**
   * @param repositories new value of {@link #getrepositories}.
   */
  @XmlElement(name = "repositories")
  public void setRepositories(MavenSettingsRepositoriesModel repositories) {

    this.repositories = repositories;
  }

  /**
   * @return id
   */
  public String getId() {

    return this.id;
  }

  /**
   * @param id new value of {@link #getid}.
   */
  @XmlElement(name = "id")
  public void setId(String id) {

    this.id = id;
  }

}
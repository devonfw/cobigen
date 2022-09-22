package com.devonfw.cobigen.api.to.model;

import javax.xml.bind.annotation.XmlElement;

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

}

package com.devonfw.cobigen.api.to.model;

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
   * Represents the activation element in maven's settings.xml
   */
  MavenSettingsActivation activation;

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
   * @return activation
   */
  public MavenSettingsActivation getActivation() {

    return this.activation;
  }

  /**
   * @param activation new value of {@link #getactivation}.
   */
  @XmlElement(name = "activation")
  public void setActivation(MavenSettingsActivation activation) {

    this.activation = activation;
  }

}

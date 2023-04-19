package com.devonfw.cobigen.retriever.settings.to.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Class, which represents the activeProfiles element of maven's settings.xml
 *
 */
public class MavenSettingsActiveProfilesModel {

  /**
   * Represents the activeProfile elements of activeProfiles
   */
  List<String> activeProfilesList;

  /**
   * @return activeProfilesList
   */
  public List<String> getActiveProfilesList() {

    return this.activeProfilesList;
  }

  /**
   * @param activeProfilesList new value of {@link #getactiveProfilesList}.
   */
  @XmlElement(name = "activeProfile")
  public void setActiveProfilesList(List<String> activeProfilesList) {

    this.activeProfilesList = activeProfilesList;
  }

}

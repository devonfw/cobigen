package com.devonfw.cobigen.api.to.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Class, which represents a profiles element of the settings.xml
 *
 */
public class MavenSettingsProfilesModel {

  /**
   * Represents profile elements in maven's settings.xml
   */
  List<MavenSettingsProfileModel> profileList;

  /**
   * @return profiles
   */
  public List<MavenSettingsProfileModel> getProfileList() {

    return this.profileList;
  }

  /**
   * @param profiles new value of {@link #getprofiles}.
   */
  @XmlElement(name = "profile")
  public void setProfileList(List<MavenSettingsProfileModel> profiles) {

    this.profileList = profiles;
  }

}

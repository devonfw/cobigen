package com.devonfw.cobigen.api.to.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * Class, which represents a profiles element of the settings.xml
 *
 */
public class MavenSettingsProfilesModel {

  List<MavenSettingsProfileModel> profiles;

  /**
   * @return profiles
   */
  public List<MavenSettingsProfileModel> getProfiles() {

    return this.profiles;
  }

  /**
   * @param profiles new value of {@link #getprofiles}.
   */
  @XmlElement(name = "profile")
  public void setProfiles(List<MavenSettingsProfileModel> profiles) {

    this.profiles = profiles;
  }

}

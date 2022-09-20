package com.devonfw.cobigen.api.to.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class, which represents the settings element of the settings.xml
 *
 */

@XmlRootElement(name = "settings")
public class MavenSettingsModel {

  MavenSettingsProfilesModel profiles;

  MavenSettingsServers servers;

  /**
   * @return profiles
   */
  public MavenSettingsProfilesModel getProfiles() {

    return this.profiles;
  }

  /**
   * @param profiles new value of {@link #getprofiles}.
   */
  @XmlElement(name = "profiles")
  public void setProfiles(MavenSettingsProfilesModel profiles) {

    this.profiles = profiles;
  }

  /**
   * @return servers
   */
  public MavenSettingsServers getServers() {

    return this.servers;
  }

  /**
   * @param servers new value of {@link #getservers}.
   */
  @XmlElement(name = "servers")
  public void setServers(MavenSettingsServers servers) {

    this.servers = servers;
  }

}

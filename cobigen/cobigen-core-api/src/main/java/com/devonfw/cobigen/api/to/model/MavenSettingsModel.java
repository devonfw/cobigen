package com.devonfw.cobigen.api.to.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Class, which represents the settings element of the settings.xml
 *
 */
@XmlRootElement(name = "settings")
public class MavenSettingsModel {

  /**
   * Represents the model element in maven's settings.xml
   */
  MavenSettingsProfilesModel profiles;

  /**
   * Represents the servers element in maven's settings.xml
   */
  MavenSettingsServers servers;

  /**
   * Represents the mirrors element in maven's settings.xml
   */
  MavenSettingsMirrorsModel mirrors;

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

  /**
   * @return mirrors
   */
  public MavenSettingsMirrorsModel getMirrors() {

    return this.mirrors;
  }

  /**
   * @param mirrors new value of {@link #getmirrors}.
   */
  @XmlElement(name = "mirrors")
  public void setMirrors(MavenSettingsMirrorsModel mirrors) {

    this.mirrors = mirrors;
  }

}

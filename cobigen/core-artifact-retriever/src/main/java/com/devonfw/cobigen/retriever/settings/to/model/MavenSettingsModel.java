package com.devonfw.cobigen.retriever.settings.to.model;

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
  MavenSettingsServersModel servers;

  /**
   * Represents the mirrors element in maven's settings.xml
   */
  MavenSettingsMirrorsModel mirrors;

  /**
   * Represents the mirrors element in maven's settings.xml
   */
  MavenSettingsProxiesModel proxies;

  /**
   * Represents the activeProfiles element in maven's settings.xml
   */
  MavenSettingsActiveProfilesModel activeProfiles;

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
  public MavenSettingsServersModel getServers() {

    return this.servers;
  }

  /**
   * @param servers new value of {@link #getservers}.
   */
  @XmlElement(name = "servers")
  public void setServers(MavenSettingsServersModel servers) {

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

  /**
   * @return proxies
   */
  public MavenSettingsProxiesModel getProxies() {

    return this.proxies;
  }

  /**
   * @param proxies new value of {@link #getproxies}.
   */
  @XmlElement(name = "proxies")
  public void setProxies(MavenSettingsProxiesModel proxies) {

    this.proxies = proxies;
  }

  /**
   * @return activeProfiles
   */
  public MavenSettingsActiveProfilesModel getActiveProfiles() {

    return this.activeProfiles;
  }

  /**
   * @param activeProfiles new value of {@link #getactiveProfiles}.
   */
  @XmlElement
  public void setActiveProfiles(MavenSettingsActiveProfilesModel activeProfiles) {

    this.activeProfiles = activeProfiles;
  }

}

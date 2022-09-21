package com.devonfw.cobigen.api.to.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * Class, which represents the servers element of the settings.xml
 *
 */
public class MavenSettingsServers {

  List<MavenSettingsServer> serverList;

  /**
   * @return server
   */
  public List<MavenSettingsServer> getServerList() {

    return this.serverList;
  }

  /**
   * @param server new value of {@link #getserver}.
   */
  @XmlElement(name = "server")
  public void setServerList(List<MavenSettingsServer> server) {

    this.serverList = server;
  }

}

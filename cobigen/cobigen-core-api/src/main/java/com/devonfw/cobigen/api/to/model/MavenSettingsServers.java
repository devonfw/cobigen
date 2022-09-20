package com.devonfw.cobigen.api.to.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * Class, which represents the servers element of the settings.xml
 *
 */
public class MavenSettingsServers {

  List<MavenSettingsServer> server;

  /**
   * @return server
   */
  public List<MavenSettingsServer> getServer() {

    return this.server;
  }

  /**
   * @param server new value of {@link #getserver}.
   */
  @XmlElement(name = "server")
  public void setServer(List<MavenSettingsServer> server) {

    this.server = server;
  }

}

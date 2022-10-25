package com.devonfw.cobigen.retriever.settings.to.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * Class, which represents the servers element of the settings.xml
 *
 */
public class MavenSettingsServersModel {

  /**
   * Represents server elements in maven's settings.xml
   */
  List<MavenSettingsServerModel> serverList;

  /**
   * @return server
   */
  public List<MavenSettingsServerModel> getServerList() {

    return this.serverList;
  }

  /**
   * @param server new value of {@link #getserver}.
   */
  @XmlElement(name = "server")
  public void setServerList(List<MavenSettingsServerModel> server) {

    this.serverList = server;
  }

}

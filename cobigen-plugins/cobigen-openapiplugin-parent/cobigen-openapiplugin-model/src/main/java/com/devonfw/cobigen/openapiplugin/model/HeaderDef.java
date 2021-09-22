package com.devonfw.cobigen.openapiplugin.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Definition that stores data about the overall application
 */
@SuppressWarnings("javadoc")
public class HeaderDef {
  List<ServerDef> servers;

  InfoDef info;

  public List<ServerDef> getServers() {

    return this.servers;
  }

  public void setServers(List<ServerDef> servers) {

    this.servers = servers;
  }

  public InfoDef getInfo() {

    return this.info;
  }

  public void setInfo(InfoDef info) {

    this.info = info;
  }

  public HeaderDef() {

    this.servers = new LinkedList<>();
  }

  @Override
  public String toString() {

    return this.info.getTitle() + "  " + this.info.getDescription();
  }
}

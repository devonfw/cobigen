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
        return servers;
    }

    public void setServers(List<ServerDef> servers) {
        this.servers = servers;
    }

    public InfoDef getInfo() {
        return info;
    }

    public void setInfo(InfoDef info) {
        this.info = info;
    }

    public HeaderDef() {
        servers = new LinkedList<>();
    }

    @Override
    public String toString() {
        return info.getTitle() + "  " + info.getDescription();
    }
}

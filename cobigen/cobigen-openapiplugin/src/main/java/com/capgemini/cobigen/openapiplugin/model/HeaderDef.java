package com.capgemini.cobigen.openapiplugin.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Definition that stores data about the overall application
 */
@SuppressWarnings("javadoc")
public class HeaderDef {
    List<EntityDef> entities;

    List<ServerDef> servers;

    InfoDef info;

    public List<EntityDef> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityDef> entities) {
        this.entities = entities;
    }

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
        entities = new LinkedList<>();
        servers = new LinkedList<>();
    }
}

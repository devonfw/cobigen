package com.capgemini.cobigen.openapiplugin.model;

import java.util.LinkedList;
import java.util.List;

import io.swagger.models.ModelImpl;

/**
 *
 */
public class ComponentDef {

    private String name;

    private String version;

    private List<PathDef> paths;

    private List<ModelImpl> entities;

    public List<PathDef> getPaths() {
        return paths;
    }

    public void setPaths(List<PathDef> paths) {
        this.paths = paths;
    }

    public List<ModelImpl> getEntities() {
        return entities;
    }

    public void setEntities(List<ModelImpl> entities) {
        this.entities = entities;
    }

    public ComponentDef() {
        name = "";
        version = "v1";
        paths = new LinkedList<>();
        entities = new LinkedList<>();
    }

    public String getComponent() {
        return name;
    }

    public void setComponent(String component) {
        this.name = component;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

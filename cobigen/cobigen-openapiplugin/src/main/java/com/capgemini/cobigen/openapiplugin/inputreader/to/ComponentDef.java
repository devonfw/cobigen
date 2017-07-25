package com.capgemini.cobigen.openapiplugin.inputreader.to;

import java.util.LinkedList;
import java.util.List;

import io.swagger.models.ModelImpl;

/**
 *
 */
public class ComponentDef {

    private String component;

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
        component = "";
        version = "v1";
        paths = new LinkedList<>();
        entities = new LinkedList<>();
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

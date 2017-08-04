package com.capgemini.cobigen.openapiplugin.model;

import java.util.LinkedList;
import java.util.List;

import io.swagger.models.ModelImpl;

/**
 *
 */
public class EntityDef extends ModelImpl {

    private String component;

    private List<PathDef> paths;

    public EntityDef() {
        paths = new LinkedList<>();
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public List<PathDef> getPaths() {
        return paths;
    }

    public void setPaths(List<PathDef> paths) {
        this.paths = paths;
    }

}

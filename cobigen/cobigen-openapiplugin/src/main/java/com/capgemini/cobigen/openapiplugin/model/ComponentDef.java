package com.capgemini.cobigen.openapiplugin.model;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class ComponentDef {

    private String version;

    private List<PathDef> paths;

    public List<PathDef> getPaths() {
        return paths;
    }

    public void setPaths(List<PathDef> paths) {
        this.paths = paths;
    }

    public ComponentDef() {
        version = "v1";
        paths = new LinkedList<>();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

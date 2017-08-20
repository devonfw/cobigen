package com.capgemini.cobigen.openapiplugin.model;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class ComponentDef {

    private List<PathDef> paths;

    public List<PathDef> getPaths() {
        return paths;
    }

    public void setPaths(List<PathDef> paths) {
        this.paths = paths;
    }

    public ComponentDef() {
        paths = new LinkedList<>();
    }
}

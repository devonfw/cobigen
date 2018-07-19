package com.devonfw.cobigen.openapiplugin.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Definition that store the configuration of a oasp4j component from OpenApi
 */
@SuppressWarnings("javadoc")
public class ComponentDef {

    private List<PathDef> paths;

    public ComponentDef() {
        paths = new LinkedList<>();
    }

    public List<PathDef> getPaths() {
        return paths;
    }

    public void setPaths(List<PathDef> paths) {
        this.paths = paths;
    }

}

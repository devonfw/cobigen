package com.capgemini.cobigen.openapiplugin.model;

import io.swagger.models.Path;

/**
 *
 */
public class PathDef {

    private String pathURI;

    private Path path;

    public PathDef() {
        path = new Path();
    }

    public String getPathURI() {
        return pathURI;
    }

    public void setPathURI(String pathURI) {
        this.pathURI = pathURI;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

}

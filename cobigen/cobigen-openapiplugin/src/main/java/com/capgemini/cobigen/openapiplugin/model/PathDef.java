package com.capgemini.cobigen.openapiplugin.model;

import com.reprezen.kaizen.oasparser.model3.Path;

/**
 *
 */
public class PathDef {

    private String pathURI;

    private String version;

    private Path path;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}

package com.capgemini.cobigen.openapiplugin.inputreader.to;

import java.util.LinkedList;
import java.util.List;

import io.swagger.models.Operation;
import io.swagger.models.Path;

/**
 *
 */
public class PathDef {

    private String pathURI;

    private Path path;

    private List<Operation> operations;

    public PathDef() {
        operations = new LinkedList<>();
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

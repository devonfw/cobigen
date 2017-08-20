package com.capgemini.cobigen.openapiplugin.model;

import java.util.List;

/**
 *
 */
public class PathDef {

    private String pathURI;

    private String version;

    private List<OperationDef> operations;

    public PathDef(String pathURI, String version) {
        this.version = version;
        this.pathURI = pathURI;
    }

    public PathDef() {
        version = "v1";
    }

    public String getPathURI() {
        return pathURI;
    }

    public void setPathURI(String pathURI) {
        this.pathURI = pathURI;
    }

    public List<OperationDef> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationDef> operations) {
        this.operations = operations;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}

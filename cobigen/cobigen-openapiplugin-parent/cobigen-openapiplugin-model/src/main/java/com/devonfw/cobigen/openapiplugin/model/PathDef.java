package com.devonfw.cobigen.openapiplugin.model;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
@SuppressWarnings("javadoc")
public class PathDef {

    private String rootComponent;

    private String pathURI;

    private String version;

    private List<OperationDef> operations;

    public PathDef(String pathURI, String version) {
        this.version = version;
        this.pathURI = pathURI;
        operations = new LinkedList<>();
    }

    public PathDef(String rootComponent, String pathURI, String version) {
        this(pathURI, version);
        this.rootComponent = rootComponent;
    }

    public PathDef() {
        version = "v1";
    }

    public String getRootComponent() {
        return rootComponent;
    }

    public void setRootComponent(String rootComponent) {
        this.rootComponent = rootComponent;
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

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
    this.operations = new LinkedList<>();
  }

  public PathDef(String rootComponent, String pathURI, String version) {

    this(pathURI, version);
    this.rootComponent = rootComponent;
  }

  public PathDef() {

    this.version = "v1";
  }

  public String getRootComponent() {

    return this.rootComponent;
  }

  public void setRootComponent(String rootComponent) {

    this.rootComponent = rootComponent;
  }

  public String getPathURI() {

    return this.pathURI;
  }

  public void setPathURI(String pathURI) {

    this.pathURI = pathURI;
  }

  public List<OperationDef> getOperations() {

    return this.operations;
  }

  public void setOperations(List<OperationDef> operations) {

    this.operations = operations;
  }

  public String getVersion() {

    return this.version;
  }

  public void setVersion(String version) {

    this.version = version;
  }

}

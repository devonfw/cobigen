package com.devonfw.cobigen.openapiplugin.model;

/**
 * Definition that stores data about a server declaration
 */
@SuppressWarnings("javadoc")
public class ServerDef {
  private String URI;

  private String description;

  public String getURI() {

    return this.URI;
  }

  public void setURI(String uRI) {

    this.URI = uRI;
  }

  public String getDescription() {

    return this.description;
  }

  public void setDescription(String description) {

    this.description = description;
  }
}

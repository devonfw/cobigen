package com.devonfw.cobigen.openapiplugin.model;

/**
 * Definition that stores data about a server declaration
 */
@SuppressWarnings("javadoc")
public class ServerDef {
    private String URI;

    private String description;

    public String getURI() {
        return URI;
    }

    public void setURI(String uRI) {
        URI = uRI;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

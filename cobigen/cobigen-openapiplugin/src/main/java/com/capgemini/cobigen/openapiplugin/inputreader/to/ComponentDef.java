package com.capgemini.cobigen.openapiplugin.inputreader.to;

/**
 *
 */
public class ComponentDef {

    private String component;

    private String version;

    public ComponentDef() {
        component = "";
        version = "v1";
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

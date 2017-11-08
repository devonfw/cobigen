package com.capgemini.cobigen.openapiplugin.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Definition that stores the configuration of an OpenApi definition schema
 */
@SuppressWarnings("javadoc")
public class EntityDef {

    private ComponentDef component;

    private String componentName;

    private String name;

    private String description;

    private List<PropertyDef> properties;

    private List<RelationShip> relationShips;

    public EntityDef() {
        properties = new LinkedList<>();
        relationShips = new LinkedList<>();
    }

    public ComponentDef getComponent() {
        return component;
    }

    public void setComponent(ComponentDef component) {
        this.component = component;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public List<PropertyDef> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyDef> properties) {
        this.properties = properties;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RelationShip> getRelationShips() {
        return relationShips;
    }

    public void setRelationShips(List<RelationShip> relationShips) {
        this.relationShips = relationShips;
    }

}

package com.capgemini.cobigen.openapiplugin.model;

/**
 *
 */
public class RelationShip {

    private String type;

    private String entity;

    private boolean sameComponent;

    private boolean unidirectional;

    public RelationShip(String type, String entity) {
        setType(type);
        setEntity(entity);
        sameComponent = false;
    }

    public RelationShip(String type, String entity, boolean sameComponent, boolean unidirectional) {
        setType(type);
        setEntity(entity);
        setSameComponent(sameComponent);
        this.unidirectional = unidirectional;
    }

    public RelationShip() {
        setType("");
        setEntity("");
        sameComponent = false;
        unidirectional = true;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public boolean getSameComponent() {
        return sameComponent;
    }

    public void setSameComponent(boolean sameComponent) {
        this.sameComponent = sameComponent;
    }

    public boolean getUnidirectional() {
        return unidirectional;
    }

    public void setUnidirectional(boolean unidirectional) {
        this.unidirectional = unidirectional;
    }

}

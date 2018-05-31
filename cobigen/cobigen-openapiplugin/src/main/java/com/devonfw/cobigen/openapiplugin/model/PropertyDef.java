package com.capgemini.cobigen.openapiplugin.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@SuppressWarnings("javadoc")
public class PropertyDef {

    private String name;

    private String type;

    private String format;

    private boolean isCollection;

    private boolean isEntity;

    private boolean sameComponent;

    private String description;

    private boolean required;

    private Map<String, Object> constraints;

    public PropertyDef() {
        constraints = new HashMap<>();
        setIsCollection(false);
        setIsEntity(false);
        constraints = new HashMap<>();
        setSameComponent(false);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Map<String, Object> getConstraints() {
        return constraints;
    }

    public void setConstraints(Map<String, Object> constraints) {
        this.constraints = constraints;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(boolean isCollection) {
        this.isCollection = isCollection;
    }

    public boolean getIsEntity() {
        return isEntity;
    }

    public void setIsEntity(boolean isEntity) {
        this.isEntity = isEntity;
    }

    public boolean getSameComponent() {
        return sameComponent;
    }

    public void setSameComponent(boolean sameComponent) {
        this.sameComponent = sameComponent;
    }
}

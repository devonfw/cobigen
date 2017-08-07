package com.capgemini.cobigen.openapiplugin.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class PropertyDef {

    private String name;

    private String type;

    private String description;

    private boolean required;

    private Map<String, Object> constraints;

    public PropertyDef() {
        constraints = new HashMap<>();
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

    public boolean isRequired() {
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

}

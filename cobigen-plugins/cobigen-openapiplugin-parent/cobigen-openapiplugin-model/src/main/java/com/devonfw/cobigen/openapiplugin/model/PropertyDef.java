package com.devonfw.cobigen.openapiplugin.model;

import java.util.HashMap;
import java.util.List;
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

  private List<String> enumElements;

  public PropertyDef() {

    this.constraints = new HashMap<>();
    setIsCollection(false);
    setIsEntity(false);
    this.constraints = new HashMap<>();
    setSameComponent(false);
  }

  public void setEnumElements(List<String> enumElements) {

    this.enumElements = enumElements;
  }

  public List<String> getEnumElements() {

    return this.enumElements;
  }

  public String getName() {

    return this.name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public String getType() {

    return this.type;
  }

  public void setType(String type) {

    this.type = type;
  }

  public boolean getRequired() {

    return this.required;
  }

  public void setRequired(boolean required) {

    this.required = required;
  }

  public Map<String, Object> getConstraints() {

    return this.constraints;
  }

  public void setConstraints(Map<String, Object> constraints) {

    this.constraints = constraints;
  }

  public String getDescription() {

    return this.description;
  }

  public void setDescription(String description) {

    this.description = description;
  }

  public String getFormat() {

    return this.format;
  }

  public void setFormat(String format) {

    this.format = format;
  }

  public boolean getIsCollection() {

    return this.isCollection;
  }

  public void setIsCollection(boolean isCollection) {

    this.isCollection = isCollection;
  }

  public boolean getIsEntity() {

    return this.isEntity;
  }

  public void setIsEntity(boolean isEntity) {

    this.isEntity = isEntity;
  }

  public boolean getSameComponent() {

    return this.sameComponent;
  }

  public void setSameComponent(boolean sameComponent) {

    this.sameComponent = sameComponent;
  }
}

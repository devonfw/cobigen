package com.devonfw.cobigen.openapiplugin.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Definition that stores the configuration of an OpenApi definition schema
 */
@SuppressWarnings("javadoc")
public class EntityDef {

  private ComponentDef component;

  private String componentName;

  private HeaderDef header;

  /**
   * This Map stores all the "extension" properties defined by the user on the OpenAPI file for this entity
   */
  private Map<String, Object> extensionProperties = new HashMap<>();

  private String name;

  private String description;

  private List<PropertyDef> properties;

  private List<EntityDef> allEntityDefs;

  public HeaderDef getHeader() {

    return this.header;
  }

  public void setHeader(HeaderDef header) {

    this.header = header;
  }

  public Map<String, Object> getExtensionProperties() {

    return this.extensionProperties;
  }

  public void setExtensionProperties(Map<String, Object> extensionProperties) {

    this.extensionProperties = extensionProperties;
  }

  public EntityDef() {

    this.properties = new LinkedList<>();
  }

  public ComponentDef getComponent() {

    return this.component;
  }

  public void setComponent(ComponentDef component) {

    this.component = component;
  }

  /**
   * @return the Map of all the extension tags
   */
  public Map<String, Object> getUserPropertiesMap() {

    return this.extensionProperties;
  }

  /**
   * Sets the Map of all the extensions tags
   *
   * @param extensions
   */
  public void setUserPropertiesMap(Map<String, Object> extensions) {

    this.extensionProperties.putAll(extensions);

  }

  public Object getUserProperty(String key) {

    return this.extensionProperties.get(key);
  }

  public void setUserProperty(String key, String value) {

    this.extensionProperties.put(key, value);
  }

  public String getName() {

    return this.name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public String getComponentName() {

    return this.componentName;
  }

  public void setComponentName(String componentName) {

    this.componentName = componentName;
  }

  public List<PropertyDef> getProperties() {

    return this.properties;
  }

  public void setProperties(List<PropertyDef> properties) {

    this.properties = properties;
  }

  public String getDescription() {

    return this.description;
  }

  public void setDescription(String description) {

    this.description = description;
  }

  public List<EntityDef> getAllEntityDefs() {

    return this.allEntityDefs;
  }

  public void setAllEntityDefs(List<EntityDef> allEntityDefs) {

    this.allEntityDefs = allEntityDefs;
  }

}

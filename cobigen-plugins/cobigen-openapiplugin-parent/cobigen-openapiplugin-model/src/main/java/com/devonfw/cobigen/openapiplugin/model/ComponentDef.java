package com.devonfw.cobigen.openapiplugin.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Definition that store the configuration of a oasp4j component from OpenApi
 */
@SuppressWarnings("javadoc")
public class ComponentDef {

  private String name;

  private List<PathDef> paths;

  /**
   * This Map stores all the "extension" properties defined by the user on the OpenAPI file for this entity
   */
  private Map<String, Object> extensionProperties = new HashMap<>();

  public ComponentDef() {

    this.paths = new LinkedList<>();
  }

  public String getName() {

    return this.name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public List<PathDef> getPaths() {

    return this.paths;
  }

  public void setPaths(List<PathDef> paths) {

    this.paths = paths;
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

}

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

    public HeaderDef getHeader() {
        return header;
    }

    public void setHeader(HeaderDef header) {
        this.header = header;
    }

    public Map<String, Object> getExtensionProperties() {
        return extensionProperties;
    }

    public void setExtensionProperties(Map<String, Object> extensionProperties) {
        this.extensionProperties = extensionProperties;
    }

    /**
     * This Map stores all the "extension" properties defined by the user on the OpenAPI file for this entity
     */
    private Map<String, Object> extensionProperties = new HashMap<>();

    private String name;

    private String description;

    private List<PropertyDef> properties;

    public EntityDef() {
        properties = new LinkedList<>();
    }

    public ComponentDef getComponent() {
        return component;
    }

    public void setComponent(ComponentDef component) {
        this.component = component;
    }

    /**
     * @return the Map of all the extension tags
     */
    public Map<String, Object> getUserPropertiesMap() {
        return extensionProperties;
    }

    /**
     * Sets the Map of all the extensions tags
     * @param extensions
     */
    public void setUserPropertiesMap(Map<String, Object> extensions) {
        extensionProperties.putAll(extensions);

    }

    public Object getUserProperty(String key) {
        return extensionProperties.get(key);
    }

    public void setUserProperty(String key, String value) {
        extensionProperties.put(key, value);
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

}

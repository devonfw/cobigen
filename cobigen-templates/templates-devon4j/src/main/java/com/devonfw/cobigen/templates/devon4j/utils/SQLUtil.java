package com.devonfw.cobigen.templates.devon4j.utils;

import java.util.*;

/**
 * Provides operations to identify and process SQL specific information
 *
 */
public class SQLUtil extends CommonUtil {

  /**
   * The constructor.
   */
  public SQLUtil() {

    // Empty for CobiGen to automatically instantiate it
  }

  /**
   * Debug function to set breakpoint to analyze some data passed to the freemarkertemplate
   */
  public void debug(Map<String, Object> pojo) {

    System.out.println("DEBUG");
  }

  public String tableName(String entityName) {

    return entityName.replace("Entity", "").toUpperCase();
  }

  public String primaryKeyStatement(Map<String, ?> field) {

    String fieldName;
    Map<String, ?> annotations = getValue(field, "annotations");
    fieldName = getValue(field, "name");
    String incrementType = "AUTO_INCREMENT";
    return String.format("%s BIGINT %s PRIMARY KEY", fieldName, incrementType);
  }

  public String foreignKeyStatement(Map<String, ?> field) {

    return "";
  }

  /**
   * Extracts the name of the field from the Map whilst checking for name-overriding annotations.
   */
  static private String getFieldName(Map<String, ?> field) {
    String fieldName = chainAccess(field, new String[] { "annotations", "javax_persistence_Column", "name" });
    if (fieldName != null) {
      return fieldName;
    } else {
      return getValue(field, "name");
    }
  }

  /**
   * Dynamic Helper function to navigate nested maps. Returns null on any type of error
   */
  static private <T> T chainAccess(Map<String, ?> map, String[] nestedFields) {
    try {
      for (int i = 0; i < nestedFields.length - 1; i++) {
        String key = nestedFields[i];
        map = getValue(map, key);
      }
      return getValue(map, nestedFields[nestedFields.length - 1]);
    } catch (Exception ignored) {
      return null;
    }
  }

  /**
   * Parametrized helper function to dynamically extract data from a map. Returns null on casting errors
   */
  static private <T> T getValue(Map<String, ?> map, String key) {
    try {
      return (T) map.get(key);
    } catch (Exception ignored) {
      return null;
    }
  }
}

package utils;

import java.util.Map;

import constants.pojo.Field;

/**
 * A class for shared functions in the
 *
 * @author sholzer
 *
 */
public class OaspUtil {

  /**
   * Check whether the given 'canonicalType' is an OASP Entity, which is declared in the given 'component'
   *
   * @param canonicalType the type name
   * @param component the component name
   * @return true iff the canonicalType is an OASP Entity
   */
  public boolean isEntityInComponent(String canonicalType, String component) {

    return canonicalType.matches(String.format(".+%1$s\\.dataaccess\\.api\\.[A-Za-z0-9]+Entity(<.*)?", component));
  }

  /**
   * Determines the ID getter for a given 'field' dependent on whether the getter should access the ID via an object
   * reference or a direct ID getter
   *
   * @param field the field
   * @param byObjectReference boolean
   * @param component the OASP4j component name
   * @return get+ {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, true, String)})+()
   */
  public String resolveIdGetter(Map<String, Object> field, boolean byObjectReference, String component) {

    return "get" + resolveIdVariableNameOrSetterGetterSuffix(field, byObjectReference, true, component) + "()";
  }

  /**
   * same as {@link #resolveIdGetter(Map, boolean, String)} but with byObjectReference=false and component=""
   */
  public String resolveIdGetter(Map<String, Object> field) {

    return this.resolveIdGetter(field, false, "");
  }

  /**
   * Determines the ID setter for a given 'field' dependent on whether the setter should access the ID via an object
   * reference or a direct ID setter. In contrast to resolveIdGetter, this function does not generate the function
   * parenthesis to enable parameter declaration.
   *
   * @param field the field
   * @param byObjectReference boolean
   * @param component the OASP4j component name
   * @return set + {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, boolean, true, String)})
   */
  public String resolveIdSetter(Map<String, Object> field, boolean byObjectReference, String component) {

    return "set" + resolveIdVariableNameOrSetterGetterSuffix(field, byObjectReference, true, component);
  }

  /**
   * same as {@link #resolveIdSetter(Map, boolean, String)} but with byObjectReference=false and component=""
   */
  public String resolveIdSetter(Map<String, Object> field) {

    return this.resolveIdSetter(field, false, "");
  }

  /**
   * Determines the variable name for the id value of the 'field'
   *
   * @param field the field
   * @return {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, false, false, "")})
   */
  public String resolveIdVariableName(Map<String, Object> field) {

    // the component is passed down as an empty string since byObjectReference is false and therefore the component is
    // never touched
    return resolveIdVariableNameOrSetterGetterSuffix(field, false, false, "");
  }

  /**
   * Determines the ID setter/getter suffix for a given 'field' dependent on whether the setter/getter should access the
   * ID via an object reference or a direct ID setter/getter
   *
   * @param field the field
   * @param byObjectReference boolean
   * @param capitalize if the field name should be capitalized
   * @param component the oasp component. Only needed if $byObjectReference is true
   * @return idVariable name or getter/setter suffix
   */
  public String resolveIdVariableNameOrSetterGetterSuffix(Map<String, Object> field, boolean byObjectReference,
      boolean capitalize, String component) {

    String fieldName = (String) field.get(Field.NAME);
    if (capitalize) {
      fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
    String suffix = "";

    String fieldType = (String) field.get(Field.TYPE);
    String fieldCType = (String) field.get(Field.CANONICAL_TYPE);
    if (fieldType.contains("Entity")) {
      if (fieldCType.startsWith("java.util.List") || fieldCType.startsWith("java.util.Set")) {
        suffix = "Ids";
        if (fieldName.endsWith("s")) {
          // Assume trailing 's' as indicator for a plural
          fieldName = fieldName.substring(0, fieldName.length() - 1);
        }
      } else {
        suffix = "Id";
      }
      if (byObjectReference && isEntityInComponent(fieldCType, component)) {
        // direct references for Entities in same component, so get id of the object reference
        suffix = "().getId";
      }
    }

    return fieldName + suffix;

  }

  /**
   * Converts all occurrences of OASP Entities types in the given 'field' simple type (possibly generic) to Longs
   *
   * @param field the field
   * @return the field type as String. If field type contains 'Entity' the result is the field type under the regex
   *         /[^<>]+Entity/Long/
   */
  public String getSimpleEntityTypeAsLongReference(Map<String, Object> field) {

    String fieldType = (String) field.get(Field.TYPE);
    if (fieldType.contains("Entity")) {
      fieldType = fieldType.replaceAll("[^<>]+Entity", "Long");
    }
    return fieldType;
  }

}
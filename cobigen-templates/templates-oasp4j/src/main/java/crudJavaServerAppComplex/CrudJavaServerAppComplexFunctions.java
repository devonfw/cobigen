package crudJavaServerAppComplex;

import java.util.Map;

import utils.OaspUtil;

/**
 * @author sholzer
 *
 */
public class CrudJavaServerAppComplexFunctions {

  /**
   * The constructor.
   */
  public CrudJavaServerAppComplexFunctions() {
    // empty for automatic instantiation
  }

  /**
   * Check whether the given 'canonicalType' is an OASP Entity, which is declared in the given 'component'
   *
   * @param canonicalType the type name
   * @param component the component name
   * @return true iff the canonicalType is an OASP Entity
   */
  public boolean isEntityInComponent(String canonicalType, String component) {

    return new OaspUtil().isEntityInComponent(canonicalType, component);
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

    return new OaspUtil().resolveIdGetter(field, byObjectReference, component);
  }

  /**
   * same as {@link #resolveIdGetter(Map, boolean, String)} but with byObjectReference=false and component=""
   */
  public String resolveIdGetter(Map<String, Object> field) {

    return new OaspUtil().resolveIdGetter(field);
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

    return new OaspUtil().resolveIdSetter(field, byObjectReference, component);
  }

  /**
   * same as {@link #resolveIdSetter(Map, boolean, String)} but with byObjectReference=false and component=""
   */
  public String resolveIdSetter(Map<String, Object> field) {

    return new OaspUtil().resolveIdSetter(field);
  }

  /**
   * Determines the variable name for the id value of the 'field'
   *
   * @param field the field
   * @return {@link #resolveIdVariableNameOrSetterGetterSuffix(Map, false, false, "")})
   */
  public String resolveIdVariableName(Map<String, Object> field) {

    return new OaspUtil().resolveIdVariableName(field);
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

    return new OaspUtil().resolveIdVariableNameOrSetterGetterSuffix(field, byObjectReference, capitalize, component);

  }

  /**
   * Converts all occurrences of OASP Entities types in the given 'field' simple type (possibly generic) to Longs
   *
   * @param field the field
   * @return the field type as String. If field type contains 'Entity' the result is the field type under the regex
   *         /[^<>]+Entity/Long/
   */
  public String getSimpleEntityTypeAsLongReference(Map<String, Object> field) {

    return new OaspUtil().getSimpleEntityTypeAsLongReference(field);
  }

}

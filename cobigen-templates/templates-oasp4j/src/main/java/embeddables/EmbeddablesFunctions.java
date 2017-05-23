package embeddables;

import java.util.Map;

import constants.pojo.Field;

/**
 * @author sholzer
 *
 */
public class EmbeddablesFunctions {

  /**
   * The constructor.
   */
  public EmbeddablesFunctions() {
    // canonical constructor
  }

  /**
   * Check whether the given 'canonicalType' is an OASP Entity, which is declared in the given 'component'
   *
   * @param canonicalType the entity type to be checked
   * @param component the component in which the entity may or may not be included. this is an component in the sense of
   *        the oasp4j structure
   * @return true iff canonicalType matches the regex .*${component}\.dataaccess\.api\.[A-Za-z0-9_]+Entity(<.*)?
   */
  private boolean isEntityInComponent(String canonicalType, String component) {

    return canonicalType.matches(".*" + component + "\\.dataaccess\\.api\\.[A-Za-z0-9_]+Entity(<.*)?");
  }

  /**
   * Determines the ID getter for a given 'field' dependent on whether the getter should access the ID via an object
   * reference or a direct ID getter (default=false)
   */
  @SuppressWarnings("unused")
  private String resolveIdGetter(Map<String, Object> field, boolean byObjectReference, String component) {

    String suffix = resolveIdVariableNameOrsetterGetterSuffix(field, byObjectReference, true, component);
    return "get" + suffix + "()";
  }

  /**
   * Determines the ID setter for a given 'field' dependent on whether the setter should access the ID via an object
   * reference or a direct ID setter (default=false) In contrast to resolveIdGetter, this function does not generate the
   * function parenthesis to enable parameter declaration.
   */
  @SuppressWarnings("unused")
  private String resolveIdSetter(Map<String, Object> field, boolean byObjectReference, String component) {

    String suffix = resolveIdVariableNameOrsetterGetterSuffix(field, byObjectReference, true, component);
    return "set" + suffix;
  }

  /**
   * Determines the ID setter/getter suffix for a given 'field' dependent on whether the setter/getter should access the
   * ID via an object reference or a direct ID setter/getter (default=false)
   *
   * @param field the name of the field
   * @param byObjectReference boolean
   * @param capitalize boolean
   * @param component the oasp4j component name
   * @return String
   */
  private String resolveIdVariableNameOrsetterGetterSuffix(Map<String, Object> field, boolean byObjectReference,
      boolean capitalize, String component) {

    String fieldName = (String) field.get(Field.NAME);
    fieldName = capitalize ? fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1) : fieldName;

    String suffix = "";
    if (((String) field.get(Field.TYPE)).contains("Entity")) {
      String canonicalType = (String) field.get(Field.CANONICAL_TYPE);
      if (canonicalType.startsWith("java.util.List") || canonicalType.startsWith("java.util.Set")) {
        suffix = "Ids";
        if (fieldName.endsWith("s")) {
          // assume the tailing 's' indicates a plural
          fieldName = fieldName.substring(0, fieldName.length() - 1);
        }
      } else {
        suffix = "Id";
      }
      if (byObjectReference && isEntityInComponent(canonicalType, component)) {
        suffix = "().getId";
      }
    }
    return fieldName + suffix;

  }

  /**
   * Converts all occurrences of OASP Entities types in the given 'field' simple type (possibly generic) to Longs
   */
  @SuppressWarnings("unused")
  private String getSimpleEntityTypeAsLongReference(Map<String, Object> field) {

    String result = (String) field.get(Field.TYPE);
    if (result.contains("Entity")) {
      result.replaceAll("[^<>]+Entity", "Long");
    }
    return result;
  }

}

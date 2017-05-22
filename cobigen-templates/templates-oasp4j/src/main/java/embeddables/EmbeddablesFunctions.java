package embeddables;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import constants.pojo.Field;
import utils.JavaUtil;

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
   * Returns the Object version of a Java primitive or the input if the input isn't a java primitive
   *
   * @param variableType String
   * @return the corresponding object type name of the input if the input is the name of a primitive java type. The
   *         input itself if not.
   */
  public String boxJavaPrimitives(String variableType) {

    switch (variableType) {
    case "boolean":
      return "Boolean";
    case "byte":
      return "Byte";
    case "char":
      return "Char";
    case "double":
      return "Double";
    case "float":
      return "Float";
    case "int":
      return "Integer";
    case "long":
      return "Long";
    case "short":
      return "Short";
    default:
      return variableType;
    }

  }

  /**
   * Generates all field declaration whereas Entity references will be converted to appropriate id references
   *
   * @param pojoFields the fields from the pojo
   * @return String
   */
  public String generateFieldDeclarationsWithRespectToEntityObjectToIdReferenceConversion(
      List<Map<String, Object>> pojoFields) {

    StringBuilder result = new StringBuilder();
    for (Map<String, Object> field : pojoFields) {
      if (field.get(Field.TYPE) instanceof String && field.get(Field.NAME) instanceof String) {
        List<String> components = new LinkedList<>();
        String type = (String) field.get(Field.TYPE);
        components.add("private");
        if (type.contains("Entity")) {
          components.add(type.replaceAll("[^<>,]+Entity", "Long"));
          components.add(resolveIdVariableNameOrsetterGetterSuffix(field, false, false));
        } else {
          components.add(boxJavaPrimitives(type));
          components.add((String) field.get(Field.NAME));
        }
        result.append(JavaUtil.getStatement(components, 4));
      }
    }
    return result.toString();

  }

  /**
   * Determines the ID setter/getter suffix for a given 'field' dependent on whether the setter/getter should access the
   * ID via an object reference or a direct ID setter/getter (default=false)
   *
   * @param field the name of the field
   * @param byObjectReference boolean
   * @param capitalize boolean
   * @return String
   */
  private String resolveIdVariableNameOrsetterGetterSuffix(Map<String, Object> field, boolean byObjectReference,
      boolean capitalize) {

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
      if (byObjectReference && isEntityInComponent(canonicalType, null)) {
        suffix = "().getId";
      }
    }
    return fieldName + suffix;

  }

  private boolean isEntityInComponent(String canonicalType, String component) {

    return canonicalType.matches(".*" + component + "\\.dataaccess\\.api\\.[A-Za-z0-9_]+Entity(<.*)?");
  }

}

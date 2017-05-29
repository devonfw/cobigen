package utils;

/**
 * @author sholzer
 *
 */
public class JavaUtil {

  /**
   * The constructor.
   */
  public JavaUtil() {
    // Empty for CobiGen to automatically instantiate it
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
      return "Character";
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
   * Checks if the given type is a Java primitive
   *
   * @param simpleType the type to be checked
   * @return true iff simpleType is a Java primitive
   */
  public boolean equalsJavaPrimitive(String simpleType) {

    return "boolean".equals(simpleType) || "byte".equals(simpleType) || "char".equals(simpleType)
        || "double".equals(simpleType) || "float".equals(simpleType) || "int".equals(simpleType)
        || "long".equals(simpleType) || "short".equals(simpleType);
  }

  /**
   * Checks if the given type is a Java primitive or a Java primitive array
   *
   * @param simpleType the Type name to be checked
   * @return true iff {@link #equalsJavaPrimitive(String)} is true or if {@link #boxJavaPrimitives(String)} is true for
   *         simpleType without trailing '[]'
   */
  public boolean equalsJavaPrimitiveIncludingArrays(String simpleType) {

    String output = simpleType;
    if (output.endsWith("[]")) {
      output = simpleType.substring(0, simpleType.length() - 2);
    }
    return equalsJavaPrimitive(output);
  }

  /**
   * Returns a cast statement for a given (java primitive, variable name) pair or nothing of the type isn't a java
   * primitive
   *
   * @param simpleType Java Type
   * @param varName Variable name
   * @return String either of the form '((Java Primitive Object Type)varName)' if simpleType is a primitive or the emtpy
   *         String otherwise
   */
  public String boxJavaPrimitives(String simpleType, String varName) {

    if (equalsJavaPrimitive(simpleType)) {
      return String.format("((%1$s)%2$s)", this.boxJavaPrimitives(simpleType), varName);
    } else {
      return "";
    }

  }
}

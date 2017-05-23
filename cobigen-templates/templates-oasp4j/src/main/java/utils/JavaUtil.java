package utils;

import java.util.List;

/**
 * @author sholzer
 *
 */
public class JavaUtil {

  private JavaUtil() {
  }
  /**
   * Returns the Object version of a Java primitive or the input if the input isn't a java primitive
   *
   * @param variableType String
   * @return the corresponding object type name of the input if the input is the name of a primitive java type. The
   *         input itself if not.
   */
  public static String boxJavaPrimitives(String variableType) {

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
}

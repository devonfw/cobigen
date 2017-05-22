package utils;

import java.util.List;

/**
 * @author sholzer
 *
 */
public class JavaUtil {

  /**
   * creates a statement from a List of Strings
   *
   * @param components List of Strings. Not null nor any of it's elements.
   * @param indentation the number of white spaces to print before the actual statement
   * @return String containing the indentation followed by the white space separated components, a semicolon and a new
   *         line
   */
  public static String getStatement(List<String> components, int indentation) {

    StringBuilder builder = new StringBuilder();
    for (String component : components) {
      builder.append(component);
      builder.append(" ");
    }
    return getIndendation(indentation) + builder.toString().trim() + ";" + "\n";

  }

  private static String getIndendation(int spaces) {

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < spaces; i++) {
      builder.append(" ");
    }
    return builder.toString();
  }
}

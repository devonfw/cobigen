package com.devonfw.cobigen.openapiplugin.util.constants;

/**
 * Useful constants
 */
@SuppressWarnings("javadoc")
public class Constants {

  public static final String ARRAY = "array";

  public static final String PAGINATED = "paginated";

  public static final String OBJECT = "object";

  public static final String SEARCH_CRITERIA = "searchCriteria";

  public static final String COMPONENT_EXT = "x-component";

  public static final String SCHEMA = "schema";

  private static final String message = "The property %s was %s in a variable assignment although the input does not provide this property%s";

  /**
   * Generates a warning or error message for when the mandatory attribute was asked for but not set.
   *
   * @param mandatory true if required, false if not
   * @param property the property that was not set
   * @return the error or warning message
   */
  public static String getMandatoryMessage(boolean mandatory, String property) {

    if (mandatory) {
      return String.format(message, property.toLowerCase(), "required",
          " Please add the required attribute in your input file or set the \"mandatory\" attribute to \"false\".");
    }
    return String.format(message, property.toLowerCase(), "requested", ".");
  }
}

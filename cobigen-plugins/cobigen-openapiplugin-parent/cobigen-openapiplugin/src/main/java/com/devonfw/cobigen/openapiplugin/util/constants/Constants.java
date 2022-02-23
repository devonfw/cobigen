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

  public static String getMandatoryErrorMessage(String property) {

    return "The property " + property.toLowerCase()
        + " was required in a variable assignment although the input does not provide this property. "
        + "Please add the required attribute in your input file or set the \"mandatory\" attribute to \"false\". ";
  }

  public static String getMandatoryWarning(String property) {

    return "The property " + property.toLowerCase()
        + " was requested in a variable assignment although the input does not provide this property. Setting it to empty";
  }
}

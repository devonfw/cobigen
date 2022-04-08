package com.devonfw.cobigen.templates.devon4j.utils.documentation;

import java.util.Map;

/**
 *
 */
public class OpenApiDocumentationUtil {

  /**
   * Adds indicators to a parameter name based on them being a ?query or a {path} parameter
   *
   * @param param the parameter to add indicators too
   * @return The name of the given parameter with the prefix ? or surrounded by {}
   */
  public String getParam(Map<String, Object> param) {

    String result = "";
    String type = (String) param.get("type");
    if (type == null || type.equals("void")) {
      result = "void";
    } else {
      if ((boolean) param.get("inPath")) {
        result += "{" + type + "}";
      } else if ((boolean) param.get("inQuery")) {
        result += "?" + type;
      }
    }
    return result;
  }

  /**
   * Lists the constraints of an operations parameter as asciidoc code
   *
   * @param param The parameter which constraints should be listed
   * @return The list of constraints as asciidoc code
   */
  public String getConstraintList(Map<String, Object> param) {

    String result = "";
    Map<String, Object> constraints = (Map<String, Object>) param.get("constraints");
    boolean required = false;
    int nrParam = 0;
    if (constraints != null) {
      if (constraints.containsKey("notNull")) {
        required = (boolean) constraints.get("notNull");
      }
      if (required) {
        result += "[red]#__Required__# +" + System.lineSeparator();
        nrParam++;
      }
      for (String key : constraints.keySet()) {
        if (!key.equals("notNull")) {
          Object val = constraints.get(key);
          if (val != null) {
            if (val instanceof Boolean) {
              if ((boolean) val) {
                result += key + " +" + System.lineSeparator();
                nrParam++;
              }
            } else {
              result += key + " = " + val + " +" + System.lineSeparator();
              nrParam++;
            }
          }
        }
      }
    }
    if (nrParam == 0) {
      result = "-";
    }
    return result;
  }
}

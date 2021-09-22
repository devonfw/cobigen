package com.devonfw.cobigen.templates.devon4j.utils.documentation;

/**
 *
 */
public class DocumentationUtil {

  /**
   * Creates asciidoc code for colour coded HTTP request types
   *
   * @param type the HTTP request type to be coloured
   * @return the asciidoc code for differently coloured request types, aqua for get, lime for post, red for delete,
   *         yellow for put and fuchsia for patch (HTML colour names)
   */
  public String getTypeWithAsciidocColour(String type) {

    switch (type.toLowerCase()) {
      case "get":
        return "[aqua]#" + type.toUpperCase() + "#";
      case "post":
        return "[lime]#" + type.toUpperCase() + "#";
      case "delete":
        return "[red]#" + type.toUpperCase() + "#";
      case "put":
        return "[yellow]#" + type.toUpperCase() + "#";
      case "patch":
        return "[fuchsia]#" + type.toUpperCase() + "#";
      default:
        return "";
    }
  }
}

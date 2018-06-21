package com.devonfw.cobigen.templates.oasp4j.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

/**
 * Class that contains every connector found for one class and generates the resultant text for the template.
 */
public class Connectors {

  private List<Connector> connectors;

  public Connectors() {

    this.connectors = new ArrayList<Connector>();
  }

  public void addConnector(Connector connector) {

    this.connectors.add(connector);
  }

  /**
   * @return
   */
  public String generateText(boolean isImpl, boolean isOverride, String className) {

    String content = "";
    if (isImpl) {
      for (Connector connector : this.connectors) {
        String connectedClassName = connector.getCounterpartName();
        String multiplicity = connector.getCounterpartMultiplicity();
        if (multiplicity == null || multiplicity.equals("1")) {
          content += "\n\n\tprivate " + connectedClassName + "Entity " + connectedClassName.toLowerCase() + ";";
        } else if (multiplicity != null && multiplicity.equals("*")) {
          content += "\n\n\tprivate List<" + connectedClassName + "Entity> "
              + removePlural(connectedClassName.toLowerCase()) + "s;";
        }
      }
    }

    for (Connector connector : this.connectors) {
      String connectedClassName = connector.getCounterpartName();
      String multiplicity = connector.getCounterpartMultiplicity();
      if (multiplicity != null && multiplicity.equals("1")) {

        content += "\n\n\t";
        if (isOverride) {
          content += "@Override\n\t";
        }
        if (isImpl) {
          content += getRelationshipAnnotations(connector) + "\n\t";
          content += "public " + connectedClassName + "Entity get" + connectedClassName + "()";
        } else {
          content += "public Long get" + connectedClassName + "Id()";
        }
        if (isImpl) {
          content += "{" + "\n\t\treturn this." + connectedClassName.toLowerCase() + ";" + "\n\t}";
        } else {
          content += ";";
        }

        content += "\n\n\t";
        if (isOverride) {
          content += "@Override\n\t";
        }
        if (isImpl) {
          content += "public void set" + connectedClassName + "(" + connectedClassName + "Entity "
              + connectedClassName.toLowerCase() + ")";
        } else {
          content += "public void set" + connectedClassName + "Id(Long " + connectedClassName.toLowerCase() + "Id)";
        }
        if (isImpl) {
          content += "{" + "\n\t\tthis." + connectedClassName.toLowerCase() + " = " + connectedClassName.toLowerCase()
              + ";" + "\n\t}";
        } else {
          content += ";";
        }
        // Now w generate the get and set IDs methods for the implementation
        if (isImpl) {
          // getter
          content += "\n\n\t";
          content += "@Override\n\t";
          content += "public Long get" + connectedClassName + "Id()";
          content += "{" + "\n\t\tif(this." + connectedClassName.toLowerCase() + " == null){";
          content += "\n\t\treturn null;\n\t}";
          content += "\n\t\treturn this." + connectedClassName.toLowerCase() + ".getId();" + "\n\t}";
          // setter
          content += "\n\n\t";
          content += "@Override\n\t";
          content += "public void set" + connectedClassName + "Id(Long " + connectedClassName.toLowerCase() + "Id)";
          content += "{" + "\n\t\tif(" + connectedClassName.toLowerCase() + "Id == null){";
          content += "\n\t\tthis." + connectedClassName.toLowerCase() + " = null;\n\t}";
          content += "else {\n\t";
          content += connectedClassName + "Entity " + connectedClassName.toLowerCase() + "Entity = new "
              + connectedClassName + "Entity();\n\n\t";
          content += connectedClassName.toLowerCase() + ".setId(" + connectedClassName.toLowerCase() + "Id);\n\n\t";
          content += "this." + connectedClassName.toLowerCase() + " " + "= " + connectedClassName.toLowerCase()
              + "Entity;\n\n\t}";
          content += "\n\n\t}";
        }

      } else if (multiplicity != null && multiplicity.equals("*") && isImpl) {

        content += "\n\n\t";
        if (isOverride) {
          content += "@Override\n\t";
        }
        content += getRelationshipAnnotations(connector) + "\n\t";
        content += "public List<" + connectedClassName + "Entity> get" + removePlural(connectedClassName) + "s()";
        content += "{" + "\n\t\treturn this." + removePlural(connectedClassName.toLowerCase()) + "s;" + "\n\t}";
        content += "\n\n\t";
        if (isOverride) {
          content += "@Override\n\t";
        }
        content += "public void set" + removePlural(connectedClassName) + "s(List<" + connectedClassName + "Entity> "
            + removePlural(connectedClassName.toLowerCase()) + "s)";
        content += "{" + "\n\t\tthis." + removePlural(connectedClassName.toLowerCase()) + "s = "
            + removePlural(connectedClassName.toLowerCase()) + "s;" + "\n\t}";
      }
    }
    return content;
  }

  private String getRelationshipAnnotations(Connector source) {

    String relationship = "";
    if (source.ISSOURCE) {
      if (source.getMultiplicity().equals("*")) {
        if (source.getCounterpartMultiplicity().equals("*")) {
          relationship += "@ManyToMany()";
          relationship += "\n\t@JoinTable(name = \"" + WordUtils.capitalize(source.getCounterpartName())
              + WordUtils.capitalize(source.getClassName()) + "\", joinColumns = @JoinColumn(name = \""
              + source.getClassName() + "Id\"), inverseJoinColumns = @JoinColumn(name = \""
              + source.getCounterpartName() + "Id\"))";
        } else if (source.getCounterpartMultiplicity().equals("1")) {
          relationship += "@ManyToOne(fetch = FetchType.LAZY)\n\t@JoinColumn(name = \"" + source.getCounterpartName()
              + "Id\")";
        }
      } else if (source.getMultiplicity().equals("1")) {
        if (source.getCounterpartMultiplicity().equals("*")) {
          relationship = "@OneToMany(fetch = FetchType.LAZY)\n\t@JoinColumn(name = \""
              + WordUtils.capitalize(source.getCounterpartName()) + "id\")";
        } else if (source.getCounterpartMultiplicity().equals("1")) {
          relationship = "@OneToOne()" + "\n\t@JoinColumn(name = \"" + source.getCounterpartName() + "Id\")";
        }
      }
    } else if (source.ISTARGET) {
      if (source.getCounterpartMultiplicity().equals("*")) {
        if (source.getMultiplicity().equals("*")) {
          relationship += "@ManyToMany(mappedBy = \"" + removePlural(source.getClassName()).toLowerCase() + "s\")";
        } else if (source.getMultiplicity().equals("1")) {
          relationship = "@OneToMany(fetch = FetchType.LAZY, mappedBy = \"" + source.getCounterpartName().toLowerCase()
              + "\")";
        }
      } else if (source.getCounterpartMultiplicity().equals("1")) {
        if (source.getMultiplicity().equals("*")) {
          relationship += "@ManyToOne(fetch = FetchType.LAZY)\n\t@JoinColumn(name = \"" + source.getCounterpartName()
              + "Id\")";
        } else if (source.getMultiplicity().equals("1")) {
          relationship = "@OneToOne()" + "\n\t@JoinColumn(name = \"" + source.getCounterpartName() + "Id\")";
        }
      }
    }
    return relationship;
  }

  public ArrayList<String> getConnectedClasses() {

    ArrayList<String> connectedClasses = new ArrayList<String>();

    for (Connector connector : this.connectors) {
      connectedClasses.add(connector.getClassName());
    }

    return connectedClasses;
  }

  /**
   * If the string last character is an 's', then it gets removed
   *
   * @param targetClassName
   * @return
   */
  private String removePlural(String targetClassName) {

    // Remove last 's' for Many multiplicity
    if (targetClassName.charAt(targetClassName.length() - 1) == 's') {
      targetClassName = targetClassName.substring(0, targetClassName.length() - 1);
    }
    return targetClassName;
  }
}

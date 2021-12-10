package com.devonfw.cobigen.templates.devon4j.utils.uml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.devonfw.cobigen.templates.devon4j.utils.DevonfwUtil;

/**
 *
 */
public class UmlUtil {

  /**
   * List of connectors
   */
  private List<Connector> connectors = new ArrayList<>();

  /**
   * For generating the variables and methods (Getters and Setters) of all the connected classes to this class
   *
   * @param isImpl Boolean: Is implementation tag needed
   * @param isOverride Boolean: Is override tag needed
   * @return String: Contains all the generated text
   */
  public String generateConnectorsVariablesMethodsText(boolean isImpl, boolean isOverride) {

    String textContent = generateText(isImpl, isOverride);

    this.connectors = new ArrayList();

    return textContent;
  }

  /**
   * Stores connector's source and target in HashMaps for further generation
   *
   * @param source source object
   * @param target target object
   * @param className name of the class
   */
  public void resolveConnectorsContent(Object source, Object target, String className) {

    Node sourceNode = (Node) source;
    Node targetNode = (Node) target;

    HashMap<String, Node> sourceHash = new HashMap<>();
    NodeList childs = sourceNode.getChildNodes();
    for (int i = 0; i < childs.getLength(); i++) {
      sourceHash.put(childs.item(i).getNodeName(), childs.item(i));
    }

    HashMap<String, Node> targetHash = new HashMap<>();
    childs = targetNode.getChildNodes();
    for (int i = 0; i < childs.getLength(); i++) {
      targetHash.put(childs.item(i).getNodeName(), childs.item(i));
    }

    setConnectorsContent(sourceHash, targetHash, className);
  }

  /**
   * Sets to the Connectors class the information retrieved from source and target tags. Only sets the classes that are
   * connected to our class
   *
   * @param sourceHash Source hash
   * @param targetHash Target hash
   * @param className name of the class
   */
  public void setConnectorsContent(HashMap<?, ?> sourceHash, HashMap<?, ?> targetHash, String className) {

    Connector sourceConnector = null;
    Connector targetConnector = null;
    boolean isTarget = false;
    boolean isSource = false;

    String sourceName = getClassName(sourceHash);
    String sourceMultiplicity = getMultiplicity(sourceHash);
    if (sourceName.equals(className)) {
      isSource = true;
    }

    String targetName = getClassName(targetHash);
    String targetMultiplicity = getMultiplicity(targetHash);
    if (sourceName.equals(className)) {
      isTarget = true;
    }

    if (isSource) {
      sourceConnector = getConnector(sourceHash, true, targetMultiplicity, targetName);
      this.connectors.add(sourceConnector);
    } else if (isTarget) {
      targetConnector = getConnector(targetHash, false, sourceMultiplicity, sourceName);
      this.connectors.add(targetConnector);
    }
  }

  /**
   * Creates a Connector. The connector class is contains the information retrieved to the classes that are connected to
   * our class
   *
   * @param nodeHash contains the node
   * @param isSource true if I am source
   * @param counterpartMultiplicity multiplicity of the counter part
   * @param counterpartName Name of the counter part
   * @return A newly created Connector
   */
  private Connector getConnector(HashMap<?, ?> nodeHash, boolean isSource, String counterpartMultiplicity,
      String counterpartName) {

    Connector connector = new Connector(getClassName(nodeHash), getMultiplicity(nodeHash), isSource);
    connector.setCounterpartMultiplicity(counterpartMultiplicity);
    connector.setCounterpartName(counterpartName);

    return connector;
  }

  /**
   * Extracts the multiplicity of a Connector from a Node
   *
   * @param nodeHash The node to get the multiplicity of
   * @return The multiplicity of the connector
   */
  private String getMultiplicity(HashMap<?, ?> nodeHash) {

    if (nodeHash.containsKey("type")) {
      Node node = (Node) nodeHash.get("type");
      NamedNodeMap attrs = node.getAttributes();
      for (int j = 0; j < attrs.getLength(); j++) {
        Attr attribute = (Attr) attrs.item(j);
        if (attribute.getName().equals("multiplicity")) {
          return attribute.getValue();
        }
      }
    }
    return "1";
  }

  /**
   * Extracts the name of a Connector from a Node
   *
   * @param nodeHash The node to get the name of
   * @return The name of the connector
   */
  private String getClassName(HashMap<?, ?> nodeHash) {

    if (nodeHash.containsKey("model")) {
      Node node = (Node) nodeHash.get("model");
      NamedNodeMap attrs = node.getAttributes();
      for (int j = 0; j < attrs.getLength(); j++) {
        Attr attribute = (Attr) attrs.item(j);
        if (attribute.getName().equals("name")) {
          return attribute.getValue();
        }
      }
    }
    return "ErrorClassName";
  }

  /**
   * @param isImpl true if this is called from an Implementation template
   * @param isOverride true if this is called from an Entity template
   * @return Generated text
   */
  public String generateText(boolean isImpl, boolean isOverride) {

    String content = "";
    if (isImpl) {
      for (Connector connector : this.connectors) {
        String connectedClassName = connector.getCounterpartName();
        String multiplicity = connector.getCounterpartMultiplicity();
        if (multiplicity == null || multiplicity.equals("1")) {
          content += "\n\n\tprivate " + connectedClassName + "Entity " + connectedClassName.toLowerCase() + ";";
        } else if (multiplicity != null && multiplicity.equals("*")) {
          content += "\n\n\tprivate List<" + connectedClassName + "Entity> "
              + new DevonfwUtil().removePlural(connectedClassName.toLowerCase()) + "s;";
        }
      }
    }

    for (Connector connector : this.connectors) {
      String connectedClassName = connector.getCounterpartName();
      String multiplicity = connector.getCounterpartMultiplicity();
      if (multiplicity == null || multiplicity.equals("1")) {

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
        content += "public List<" + connectedClassName + "Entity> get"
            + new DevonfwUtil().removePlural(connectedClassName) + "s()";
        content += "{" + "\n\t\treturn this." + new DevonfwUtil().removePlural(connectedClassName.toLowerCase()) + "s;"
            + "\n\t}";
        content += "\n\n\t";
        if (isOverride) {
          content += "@Override\n\t";
        }
        content += "public void set" + new DevonfwUtil().removePlural(connectedClassName) + "s(List<" + connectedClassName
            + "Entity> " + new DevonfwUtil().removePlural(connectedClassName.toLowerCase()) + "s)";
        content += "{" + "\n\t\tthis." + new DevonfwUtil().removePlural(connectedClassName.toLowerCase()) + "s = "
            + new DevonfwUtil().removePlural(connectedClassName.toLowerCase()) + "s;" + "\n\t}";
      }
    }
    return content;
  }

  /**
   * Generates the annotations of all the connected classes
   *
   * @param source The source connector that is used to generate relationship annotations
   * @return relationship string with all the annotations for the connected classes
   */
  private String getRelationshipAnnotations(Connector source) {

    String relationship = "";
    if (source.ISSOURCE) {
      if (source.getMultiplicity() == null || source.getMultiplicity().equals("1")) {
        if (source.getCounterpartMultiplicity() == null || source.getCounterpartMultiplicity().equals("1")) {
          relationship = "@OneToOne()" + "\n\t@JoinColumn(name = \"" + source.getCounterpartName() + "Id\")";
        } else if (source.getCounterpartMultiplicity().equals("*")) {
          relationship = "@OneToMany(fetch = FetchType.LAZY)\n\t@JoinColumn(name = \""
              + WordUtils.capitalize(source.getCounterpartName()) + "id\")";
        }
      } else if (source.getMultiplicity().equals("*")) {
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
      }
    } else if (source.ISTARGET) {
      if (source.getCounterpartMultiplicity() == null || source.getCounterpartMultiplicity().equals("1")) {
        if (source.getMultiplicity() == null || source.getMultiplicity().equals("1")) {
          relationship = "@OneToOne()" + "\n\t"//
              + "@JoinColumn(name = \"" + source.getCounterpartName() + "Id\")";
        } else if (source.getMultiplicity().equals("*")) {
          relationship += "@ManyToOne(fetch = FetchType.LAZY)\n\t"//
              + "@JoinColumn(name = \"" + source.getCounterpartName() + "Id\")";
        }
      } else if (source.getCounterpartMultiplicity().equals("*")) {
        if (source.getMultiplicity().equals("*")) {
          relationship += "@ManyToMany(mappedBy = \""
              + new DevonfwUtil().removePlural(source.getClassName()).toLowerCase() + "s\")";
        } else if (source.getMultiplicity().equals("1")) {
          relationship = "@OneToMany(fetch = FetchType.LAZY, mappedBy = \"" + source.getCounterpartName().toLowerCase()
              + "\")";
        }
      }
    }
    return relationship;
  }

  /**
   * Returns connectors
   *
   * @return connectors
   */
  public List<Connector> getConnectors() {

    return this.connectors;
  }

  /**
   * Sets a new connector list
   *
   * @param connectors The new list of connectors
   */
  public void setConnectors(List<Connector> connectors) {

    this.connectors = connectors;
  }

}

package com.devonfw.cobigen.templates.oasp4j.utils.uml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.dom.DeferredElementNSImpl;

/**
 *
 */
@SuppressWarnings("restriction")
public class UmlUtil {

    /**
     *
     */
    private Connectors connectors = new Connectors();

    /**
     * For generating the variables and methods (Getters and Setters) of all the connected classes to this
     * class
     *
     * @param isImpl
     *            Boolean: Is implementation tag needed
     * @param isOverride
     *            Boolean: Is override tag needed
     * @param className
     *            name of the class
     * @return String: Contains all the generated text
     */
    public String generateConnectorsVariablesMethodsText(boolean isImpl, boolean isOverride, String className) {

        String textContent = connectors.generateText(isImpl, isOverride, className);

        connectors = new Connectors();

        return textContent;
    }

    /**
     * Gets all the class names that are connected to this class
     * @return ArrayList: Contains every class name connected to this class
     */
    public ArrayList<String> getConnectedClasses() {

        ArrayList<String> connectedClasses = new ArrayList<>();

        connectedClasses = connectors.getConnectedClasses();
        return connectedClasses;
    }

    /**
     * Stores connector's source and target in HashMaps for further generation
     *
     * @param source
     *            source object
     * @param target
     *            target object
     * @param className
     *            name of the class
     */
    public void resolveConnectorsContent(Object source, Object target, String className) {

        source.getClass().getClassLoader();
        DeferredElementNSImpl sourceNode = (DeferredElementNSImpl) source;
        DeferredElementNSImpl targetNode = (DeferredElementNSImpl) target;

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
     * Sets to the Connectors class the information retrieved from source and target tags. Only sets the
     * classes that are connected to our class
     *
     * @param sourceHash
     *            Source hash
     * @param targetHash
     *            Target hash
     * @param className
     *            name of the class
     * @return string containing the connections to generate
     */
    public String setConnectorsContent(HashMap<?, ?> sourceHash, HashMap<?, ?> targetHash, String className) {

        Connector sourceConnector = null;
        Connector targetConnector = null;
        boolean isTarget = false;
        boolean isSource = false;
        String textContent = "";
        // Get source's model attributes
        // if (sourceHash.containsKey("model")) {
        // Node node = (Node) sourceHash.get("model");
        // NamedNodeMap attrs = node.getAttributes();
        // for (int j = 0; j < attrs.getLength(); j++) {
        // Attr attribute = (Attr) attrs.item(j);
        // // This is for every type of connector
        // // Get name attribute and check if it is className
        // if (attribute.getName().equals("name")) {
        // sourceName = attribute.getValue();
        // if (attribute.getValue().equals(className)) {
        // isSource = true;
        // }
        // }
        // }
        // }
        String sourceName = getClassName(sourceHash);
        String sourceMultiplicity = getMultiplicity(sourceHash);
        if (sourceName.equals(className)) {
            isSource = true;
        } else {
            isSource = false;
        }
        // if (sourceHash.containsKey("type")) {
        // Node node = (Node) sourceHash.get("type");
        // NamedNodeMap attrs = node.getAttributes();
        // for (int j = 0; j < attrs.getLength(); j++) {
        // Attr attribute = (Attr) attrs.item(j);
        // if (attribute.getName().equals("multiplicity")) {
        // sourceMultiplicity = attribute.getValue();
        // }
        // }
        // }
        // Get target's model attributes
        String targetName = getClassName(sourceHash);
        String targetMultiplicity = getMultiplicity(sourceHash);
        if (sourceName.equals(className)) {
            isSource = true;
        } else {
            isSource = false;
        }
        // if (targetHash.containsKey("model")) {
        // Node node = (Node) targetHash.get("model");
        // NamedNodeMap attrs = node.getAttributes();
        // for (int j = 0; j < attrs.getLength(); j++) {
        // Attr attribute = (Attr) attrs.item(j);
        // // This is for every type of connector
        // // Get name attribute and check if it is className
        // if (attribute.getName().equals("name")) {
        // targetName = attribute.getValue();
        // if (attribute.getValue().equals(className)) {
        // isTarget = true;
        // }
        // }
        // }
        // }
        // if (targetHash.containsKey("type")) {
        // Node node = (Node) targetHash.get("type");
        // NamedNodeMap attrs = node.getAttributes();
        // for (int j = 0; j < attrs.getLength(); j++) {
        // Attr attribute = (Attr) attrs.item(j);
        // if (attribute.getName().equals("multiplicity")) {
        // targetMultiplicity = attribute.getValue();
        // }
        // }
        // }

        if (isSource) {
            sourceConnector = getConnector(sourceHash, true, targetMultiplicity, targetName);
            connectors.addConnector(sourceConnector);
        } else if (isTarget) {
            targetConnector = getConnector(targetHash, false, sourceMultiplicity, sourceName);
            connectors.addConnector(targetConnector);
        }
        return textContent;
    }

    /**
     * Creates a Connector. The connector class is contains the information retrieved to the classes that are
     * connected to our class
     *
     * @param nodeHash
     *            contains the node
     * @param isSource
     *            true if I am source
     * @param counterpartMultiplicity
     *            multiplicity of the counter part
     * @param counterpartName
     *            Name of the counter part
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
     * Extracts the name of a Connector from a Node
     * @param nodeHash
     *            The node to get the name of
     * @return The name of the connector
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
     * Extracts the multiplicity of a Connector from a Node
     * @param nodeHash
     *            The node to get the multiplicity of
     * @return The multiplicity of the connector
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
     * @param rs
     *            Map containing the entity information
     * @param entityName
     *            name of the entity
     * @return string containing annotation
     */
    public String getRelationShipAnnotation(Map<String, Object> rs, String entityName) {

        char c[] = ((String) rs.get("entity")).toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        String ent = new String(c);

        c = entityName.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        String entName = new String(c);
        switch ((String) rs.get("type")) {
        case "manytoone":
            return "@ManyToOne" + '\n' + "@JoinColumn(name = \"" + ent + "\")";
        case "onetomany":
            if ((boolean) rs.get("unidirectional")) {
                return "@OneToMany" + "\n" + "@JoinColumn(name = \"" + entName + "Id\")";
            }
            return "@OneToMany(mappedBy = \"" + entName + "\")";

        case "onetoone":
            return "@OneToOne" + '\n' + "@JoinColumn(name = \"" + ent + "\")";
        case "manytomany":
            return "@ManyToMany" + '\n' + "@JoinTable(name = \"" + entityName + (String) rs.get("entity")
                + "\", joinColumns = {" + '\n' + "@javax.persistence.JoinColumn(name = \"" + entName
                + "Id\") }, inverseJoinColumns = @javax.persistence.JoinColumn(name = \"" + ent + "Id\"))";
        default:
            return "";
        }
    }
}

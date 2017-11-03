package com.capgemini.cobigen.xmlplugin.appTesting;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

/**
 * Hello world!
 *
 */
public class App {

    /**
     *
     */
    private static Document document;

    /**
     *
     */
    private static Document newXmlDocument;

    /**
     * @param args
     *            unused
     * @throws XPathExpressionException
     *             indicates an error of the XPath
     */
    @SuppressWarnings("null")
    public static void main(String[] args) throws XPathExpressionException {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();

        }

        try {
            String currentDirectory = System.getProperty("user.dir");
            document = builder.parse(new FileInputStream(currentDirectory
                + "\\src\\main\\java\\com\\capgemini\\cobigen\\xmlplugin\\appTesting\\classDiagramExample.xml"));
            // "c:\\Users\\jdiazgon\\Documents\\repositorios\\interns-uml-plugin\\master\\RestaurantAsDiagram\\restaurantUseCaseSequence.xml"));
            // "C:\\EclipseOomph\\workspaces\\cobigen-development\\dev_xmlplugin_ruben\\cobigen\\cobigen-xmlplugin\\src\\main\\java\\com\\capgemini\\cobigen\\xmlplugin\\appTesting\\restaurantUseCaseSequence.xml"));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        XPath xPath = XPathFactory.newInstance().newXPath();
        String pack = "XMI/Model/packagedElement[@type='uml:Package']";

        // NodeList nodeList = (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);
        NodeList packList = (NodeList) xPath.evaluate(pack, document, XPathConstants.NODESET);

        List<Object> docsList = new LinkedList<>();

        docsList = recursiveExtractor(docsList, packList, "");
        // output for testing purposes
        System.out.println("--generated " + docsList.size() + " new documents--");
        for (Object d : docsList) {
            System.out.println(" ");
            printXmlDocument((Document) d);
        }
    }

    /**
     * This recursive function extracts classes and paths out of a xml file and generates for every class a
     * new xmi file.
     *
     * The first call should use an empty path, an empty docList and the whole document as the NodeList If
     * necessary the packages can be manipulated by providing a pre-package through the path.
     *
     * @param docList
     *            contains every new generated xmi file consisting of only one class and package annotation
     * @param nl
     *            the list of nodes to work with in this recursion.
     * @param path
     *            provides the package for every new recursive call
     * @return a list of objects (new xmi files)
     */
    private static List<Object> recursiveExtractor(List<Object> docList, NodeList nl, String path) {

        for (int i = 0; i < nl.getLength(); i++) {
            // not sure if this statement will cause some errors in the future; which items have attributes?
            if (nl.item(i).hasAttributes()) {
                // TODO: catch cases where getNamedItem("abc") = null -> already catched from .equals("abc")?
                if (nl.item(i).getAttributes().getNamedItem("xmi:type").getTextContent().equals("uml:Package")) {
                    recursiveExtractor(docList, nl.item(i).getChildNodes(),
                        // path + "." + this is for getting all the parent packages (right now is not needed).
                        nl.item(i).getAttributes().getNamedItem("name").getTextContent());
                } else if (nl.item(i).getAttributes().getNamedItem("xmi:type").getTextContent().equals("uml:Class")) {
                    docList.add(generateNewClass(nl.item(i), path));
                }
            }
        }
        // System.out.println("-------recursive-anchor--------");
        return docList;
    }

    /**
     * Generates a new xmi file for any given class node and package.
     *
     * @param n
     *            This node needs to represent an class. It will be the source for the new xml file
     * @param pack
     *            The package of the class.
     * @return A document which represents one class.
     */
    private static Object generateNewClass(Node n, String pack) {
        try {
            newXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        List<Element> attributes = getClassAttributes(n);

        Element pa = newXmlDocument.createElement("Package");
        pa.setAttribute("name", pack);

        Element root = newXmlDocument.createElement("xmi:XMI");
        newXmlDocument.appendChild(root);

        Node copyNode = newXmlDocument.importNode(n, false);

        Element newClass = newXmlDocument.createElement("Class");
        newClass.setAttribute("name", copyNode.getAttributes().getNamedItem("name").getTextContent());
        newClass.setAttribute("visibility", copyNode.getAttributes().getNamedItem("visibility").getTextContent());

        root.appendChild(pa);
        if (attributes != null) {
            for (int i = 0; i < attributes.size(); i++) {
                newClass.appendChild(attributes.get(i));
            }
        }
        pa.appendChild(newClass);

        return newXmlDocument;
    }

    /**
     * This function generates a new node for every attribute for a given class.
     * @param n
     * @return null if there are no attributes, otherwise it returns a list of nodes where every node
     *         represent an attribute.
     */
    private static List<Element> getClassAttributes(Node no) {
        if (no == null) {
            // System.out.println("Class was NULL");
            return null;
        }

        if (!no.hasChildNodes()) {
            // System.out.println(no.getAttributes().getNamedItem("name").getTextContent() + " has no
            // Attribute");
            return null;
        } else {
            List<Element> returnList = new ArrayList<>();
            NodeList loc = no.getChildNodes();
            for (int i = 0; i < loc.getLength(); i++) {
                // System.out.println("Att: " + loc.item(i).getNodeName());
                if (loc.item(i).getNodeName().equals("ownedAttribute")) {
                    // System.out.println(
                    // no.getAttributes().getNamedItem("name").getTextContent() + " has Attribute:
                    // ownedAttribute ");
                    Node n = loc.item(i);

                    if (n.hasAttributes()) {
                        TreeMap<String, String> map = new TreeMap<>();
                        Element newAttribute = newXmlDocument.createElement("Attribute");
                        for (int l = 0; l < n.getAttributes().getLength(); l++) {
                            // System.out.println(n.getAttributes().item(l));
                            // System.out.println(n.getAttributes().item(l).getNodeName());
                            // System.out.println(n.getAttributes().item(l).getTextContent());

                            // TODO .equals -> isMemberOf(ListOfAttributes)
                            if (n.getAttributes().item(l).getNodeName().equals("visibility")) {
                                map.put(n.getAttributes().item(l).getNodeName(),
                                    n.getAttributes().item(l).getTextContent());
                            }
                            if (n.getAttributes().item(l).getNodeName().equals("name")) {
                                map.put(n.getAttributes().item(l).getNodeName(),
                                    n.getAttributes().item(l).getTextContent());
                            }
                            if (n.getAttributes().item(l).getNodeName().equals("isStatic")) {
                                map.put(n.getAttributes().item(l).getNodeName(),
                                    n.getAttributes().item(l).getTextContent());
                            }

                            if (!map.isEmpty()) {
                                while (!map.isEmpty()) {
                                    newAttribute.setAttribute(map.firstEntry().getKey(), map.firstEntry().getValue());
                                    map.remove(map.firstEntry().getKey());
                                }
                            }

                        }
                        // For getting the type of the attribute (int, string, long...)
                        NodeList children = n.getChildNodes();
                        for (int j = 0; j < children.getLength(); j++) {
                            if (children.item(j).getNodeName().equals("type")) {
                                String type = children.item(j).getAttributes().item(0).getTextContent();
                                type = type.replace("EAJava_", "");

                                newAttribute.setAttribute("type", type);
                            }
                        }
                        returnList.add(newAttribute);
                    }
                }

                if (loc.item(i).getNodeName().equals("ownedOperation")) {
                    Node n = loc.item(i);

                    if (n.hasAttributes()) {
                        TreeMap<String, String> map = new TreeMap<>();
                        Element newOperation = newXmlDocument.createElement("Operation");
                        for (int l = 0; l < n.getAttributes().getLength(); l++) {
                            // System.out.println(n.getAttributes().item(l));
                            // System.out.println(n.getAttributes().item(l).getNodeName());
                            // System.out.println(n.getAttributes().item(l).getTextContent());

                            // TODO .equals -> isMemberOf(ListOfAttributes)
                            if (n.getAttributes().item(l).getNodeName().equals("visibility")) {
                                map.put(n.getAttributes().item(l).getNodeName(),
                                    n.getAttributes().item(l).getTextContent());
                            }
                            if (n.getAttributes().item(l).getNodeName().equals("name")) {
                                map.put(n.getAttributes().item(l).getNodeName(),
                                    n.getAttributes().item(l).getTextContent());
                            }

                            if (!map.isEmpty()) {
                                while (!map.isEmpty()) {
                                    newOperation.setAttribute(map.firstEntry().getKey(), map.firstEntry().getValue());
                                    map.remove(map.firstEntry().getKey());
                                }
                            }

                        }

                        // For getting the type of the method (int, string, long...)
                        NodeList children = n.getChildNodes();
                        for (int j = 0; j < children.getLength(); j++) {
                            Node child = children.item(j);
                            if (child.getNodeName().equals("ownedParameter")) {
                                String type = child.getAttributes().item(0).getTextContent();
                                type = type.replace("EAJava_", "");

                                newOperation.setAttribute("type", type);
                            }
                        }
                        returnList.add(newOperation);
                    }
                }
            }

            // Element newAttribute = newXmlDocument.createElement("Attribute");
            // newAttribute.setAttribute("name", "value");

            // returnList.add(newAttribute);

            return returnList;
        }
    }

    public static void printXmlDocument(Document document) {
        DOMImplementationLS domImplementationLS = (DOMImplementationLS) document.getImplementation();
        LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
        String string = lsSerializer.writeToString(document);
        System.out.println(string);
    }
}

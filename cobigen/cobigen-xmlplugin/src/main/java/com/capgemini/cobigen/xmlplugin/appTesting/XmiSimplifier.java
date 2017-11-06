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
public class XmiSimplifier {

    private static Document document;

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
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        XPath xPath = XPathFactory.newInstance().newXPath();
        String pack = "XMI/Model/packagedElement[@type='uml:Package']";

        NodeList packList = (NodeList) xPath.evaluate(pack, document, XPathConstants.NODESET);
        List<Object> docsList = new LinkedList<>();

        docsList = recursiveExtractor(docsList, packList, "");

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
        return docList;
    }

    /**
     * Generates a new xmi file for any given class node and package.
     *
     * @param classNode
     *            This node represents a class. It is the source for the new xml file.
     * @param pack
     *            The package of the class.
     * @return A document which represents one class.
     */
    private static Object generateNewClass(Node classNode, String pack) {
        try {
            newXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        List<Element> attributes = getClassAttributes(classNode);

        Element packageElement = newXmlDocument.createElement("package");
        packageElement.setAttribute("name", pack);

        Element root = newXmlDocument.createElement("xmi:XMI");
        newXmlDocument.appendChild(root);

        Node newClassDoc = newXmlDocument.importNode(classNode, false);
        Element newClassElement = newXmlDocument.createElement("class");
        newClassElement.setAttribute("name", newClassDoc.getAttributes().getNamedItem("name").getTextContent());
        newClassElement.setAttribute("visibility",
            newClassDoc.getAttributes().getNamedItem("visibility").getTextContent());

        root.appendChild(packageElement);
        if (attributes != null) {
            for (int i = 0; i < attributes.size(); i++) {
                newClassElement.appendChild(attributes.get(i));
            }
        }
        packageElement.appendChild(newClassElement);

        return newXmlDocument;
    }

    /**
     * This function generates a new node for every attribute for a given class.
     * @param n
     * @return null if there are no attributes, otherwise it returns a list of nodes where every node
     *         represent an attribute.
     */
    private static List<Element> getClassAttributes(Node classNode) {
        if (classNode == null || !classNode.hasChildNodes()) {
            return null;
        }

        List<Element> returnList = new ArrayList<>();
        NodeList nodes = classNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeName().equals("ownedAttribute")) {
                Node childNode = nodes.item(i);

                if (childNode.hasAttributes()) {

                    TreeMap<String, String> map = new TreeMap<>();
                    Element newAttribute = newXmlDocument.createElement("attribute");
                    for (int l = 0; l < childNode.getAttributes().getLength(); l++) {
                        if (exportAttribute(childNode.getAttributes().item(l).getNodeName())) {
                            map.put(childNode.getAttributes().item(l).getNodeName(),
                                childNode.getAttributes().item(l).getTextContent());
                        }
                        if (!map.isEmpty()) {
                            while (!map.isEmpty()) {
                                newAttribute.setAttribute(map.firstEntry().getKey(), map.firstEntry().getValue());
                                map.remove(map.firstEntry().getKey());
                            }
                        }
                    }
                    // For getting the type of the attribute (int, string, long...)
                    NodeList children = childNode.getChildNodes();
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

            if (nodes.item(i).getNodeName().equals("ownedOperation")) {
                Node n = nodes.item(i);
                if (n.hasAttributes()) {
                    TreeMap<String, String> map = new TreeMap<>();
                    Element newOperation = newXmlDocument.createElement("operation");
                    for (int l = 0; l < n.getAttributes().getLength(); l++) {
                        if (extractOperation(n.getAttributes().item(l).getNodeName())) {
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
                            Boolean returnFlag = isOperationReturn(child);
                            for (int k = 0; k < child.getAttributes().getLength(); k++) {
                                if (child.getAttributes().item(k).getNodeName().equals("type")) {
                                    String type = child.getAttributes().item(k).getTextContent();
                                    type = type.replace("EAJava_", "");

                                    if (returnFlag) {
                                        newOperation.setAttribute("returnType", type);
                                    } else {
                                        newOperation.setAttribute("inType", type);
                                    }
                                }
                            }
                        }
                    }
                    returnList.add(newOperation);
                }
            }
        }
        return returnList;

    }

    private static boolean extractOperation(String op) {
        // TODO could be solved smarter: some kind of attribute definition at the top
        // Really bad performance
        ArrayList<String> t = new ArrayList<>();
        t.add("visibility");
        t.add("name");

        if (t.contains(op)) {
            return true;
        }
        return false;
    }

    private static boolean exportAttribute(String att) {
        // TODO could be solved smarter: some kind of attribute definition at the top
        // Really bad performance
        ArrayList<String> t = new ArrayList<>();
        t.add("visibility");
        t.add("name");
        t.add("isStatic");

        if (t.contains(att)) {
            return true;
        }
        return false;
    }

    private static Boolean isOperationReturn(Node child) {
        for (int i = 0; i < child.getAttributes().getLength(); i++) {
            if (child.getAttributes().item(i).getNodeName().equals("direction")) {
                if (child.getAttributes().item(i).getNodeValue().equals("return")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void printXmlDocument(Document document) {
        DOMImplementationLS domImplementationLS = (DOMImplementationLS) document.getImplementation();
        LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
        String string = lsSerializer.writeToString(document);
        System.out.println(string);
    }
}
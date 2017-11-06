package com.capgemini.cobigen.xmlplugin.appTesting;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
    public static List<Object> recursiveExtractor(List<Object> docList, NodeList nl, String path) {

        // List of nodes for storing the parent packages of the class
        List<Node> packagesList = new LinkedList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            // not sure if this statement will cause some errors in the future; which items have attributes?
            if (nl.item(i).hasAttributes()) {
                if (nl.item(i).getAttributes().getNamedItem("xmi:type").getTextContent().equals("uml:Package")) {
                    recursiveExtractor(docList, nl.item(i).getChildNodes(),
                        // path + "." + this is for getting all the parent packages (right now is not needed).
                        nl.item(i).getAttributes().getNamedItem("name").getTextContent());
                    packagesList.add(nl.item(i));
                } else if (nl.item(i).getAttributes().getNamedItem("xmi:type").getTextContent().equals("uml:Class")) {
                    docList.add(generateNewClass(nl.item(i), path, packagesList));
                    packagesList = new LinkedList<>();
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
     * @param packagesList
     * @return A document which represents one class.
     */
    private static Object generateNewClass(Node n, String pack, List<Node> packagesList) {
        try {
            newXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Node rootNode = document.getFirstChild();
        NodeList children = rootNode.getChildNodes();

        Node parent = newXmlDocument.importNode(n.getParentNode(), false);

        Element rootTag = newXmlDocument.createElement("xmi:XMI");
        // Copies attributes of rootNode to rootTag
        for (int i = 0; i < rootNode.getAttributes().getLength(); i++) {
            rootTag.setAttribute(rootNode.getAttributes().item(i).getNodeName(),
                rootNode.getAttributes().item(i).getTextContent());
        }
        // Copies the necessary children of the rootNode to rootTag
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeName().equals("xmi:Documentation")) {
                Element documentation = newXmlDocument.createElement("xmi:Documentation");
            } else if (children.item(i).getNodeName().equals("uml:Model")) {
                // rootTag.appendChild(children.item(i));
            }
        }

        newXmlDocument.appendChild(rootTag);

        Node copyNode = newXmlDocument.importNode(n, true);
        parent.appendChild(copyNode);
        rootTag.appendChild(parent);

        return newXmlDocument;
    }

    public static void printXmlDocument(Document document) {
        DOMImplementationLS domImplementationLS = (DOMImplementationLS) document.getImplementation();
        LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
        String string = lsSerializer.writeToString(document);
        System.out.println(string);
    }
}

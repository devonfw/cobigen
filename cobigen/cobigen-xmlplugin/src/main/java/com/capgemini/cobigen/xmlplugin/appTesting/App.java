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
            System.out.println(" s");
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

        Element pa = newXmlDocument.createElement("package");
        pa.setAttribute("name", pack);
        Element root = newXmlDocument.createElement("xmi:XMI");
        newXmlDocument.appendChild(root);
        Node copyNode = newXmlDocument.importNode(n, false);
        root.appendChild(pa);
        pa.appendChild(copyNode);
        return newXmlDocument;
    }

    /**
     *
     * @param n
     * @return
     */
    private static List<Node> getClassAttributes(NodeList node) {
        List<Element> list = null;
        for (int n = 0; n < node.getLength(); n++) {
            if (node.item(n).hasAttributes()
                && node.item(n).getAttributes().getNamedItem("xmi:type").getTextContent().equals("uml:Property")) {
                list.add((Element) node.item(n));
            }
        }
        return null;
    }

    public static void printXmlDocument(Document document) {
        DOMImplementationLS domImplementationLS = (DOMImplementationLS) document.getImplementation();
        LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
        String string = lsSerializer.writeToString(document);
        System.out.println(string);
    }
}

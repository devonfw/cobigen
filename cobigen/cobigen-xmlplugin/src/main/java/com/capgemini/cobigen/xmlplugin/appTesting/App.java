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

    public static void main(String[] args) throws XPathExpressionException {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();

        }

        try {
            document = builder.parse(new FileInputStream(
                // "c:\\Users\\jdiazgon\\Documents\\repositorios\\interns-uml-plugin\\master\\RestaurantAsDiagram\\restaurantUseCaseSequence.xml"));
                "C:\\EclipseOomph\\workspaces\\cobigen-development\\dev_xmlplugin_ruben\\cobigen\\cobigen-xmlplugin\\src\\main\\java\\com\\capgemini\\cobigen\\xmlplugin\\appTesting\\restaurantUseCaseSequence.xml"));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "XMI/Model/packagedElement/packagedElement[@type='uml:Class']";
        String pack = "XMI/Model/packagedElement[@type='uml:Package']";

        NodeList nodeList = (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);
        NodeList packList = (NodeList) xPath.evaluate(pack, document, XPathConstants.NODESET);

        List<Object> docsList = new LinkedList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            try {
                newXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            } catch (ParserConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Element root = newXmlDocument.createElement("xmi:XMI");
            newXmlDocument.appendChild(root);

            // Node nodePack = packList.item(0).getAttributes().getNamedItem("name");

            // System.out.println(nodePack.getAttributes().getNamedItem("name"));
            // out: "api"
            // newXmlDocument.add(new Element(getNodeName()), ?)

            // Node nodePack = packList.item(i);
            // Node copyNode = newXmlDocument.importNode(nodePack, false);
            // root.appendChild(copyNode);

            Node node = packList.item(0);
            Node copyNode = newXmlDocument.importNode(node, false);
            root.appendChild(copyNode);
            docsList.add(newXmlDocument);

            node = nodeList.item(i);
            copyNode = newXmlDocument.importNode(node, true);
            root.appendChild(copyNode);
            docsList.add(newXmlDocument);

        }
        printXmlDocument((Document) docsList.get(0));

        nodeList = (NodeList) xPath.evaluate("XMI/packagedElement/@name", docsList.get(1), XPathConstants.NODESET);
        System.out.println("Result: " + nodeList.item(0).getNodeValue());

        /*
         * System.out.println("Expresion Xpath:\t" + expression); NodeList nodeList = (NodeList)
         * xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
         * System.out.println(nodeList.getLength()); for (int i = 0; i < nodeList.getLength(); i++) {
         * System.out.println(nodeList.item(i)); expression = "packagedElement[@type='uml:Class']/@name";
         * NodeList nodeList2 = (NodeList) xPath.compile(expression).evaluate(nodeList.item(i),
         * XPathConstants.NODESET); for (int j = 0; j < nodeList2.getLength(); j++) {
         * System.out.println(nodeList2.item(j)); } }
         */

    }

    public static void printXmlDocument(Document document) {
        DOMImplementationLS domImplementationLS = (DOMImplementationLS) document.getImplementation();
        LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
        String string = lsSerializer.writeToString(document);
        System.out.println(string);
    }
}

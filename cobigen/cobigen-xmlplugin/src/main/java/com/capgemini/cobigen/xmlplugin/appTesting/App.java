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
                "c:\\Users\\jdiazgon\\Documents\\repositorios\\interns-uml-plugin\\master\\RestaurantAsDiagram\\restaurantUseCaseSequence.xml"));

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "XMI/Model/packagedElement/packagedElement[@type='uml:Class']";

        NodeList list = (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);

        List<Object> docsList = new LinkedList<>();
        for (int i = 0; i < list.getLength(); i++) {
            try {
                newXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            } catch (ParserConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Element root = newXmlDocument.createElement("xmi:XMI");

            Node node = list.item(i);
            newXmlDocument.appendChild(root);
            Node copyNode = newXmlDocument.importNode(node, true);
            root.appendChild(copyNode);
            docsList.add(newXmlDocument);
        }
        printXmlDocument((Document) docsList.get(3));

        list = (NodeList) xPath.evaluate("XMI/packagedElement/@name", docsList.get(1), XPathConstants.NODESET);
        System.out.println("Result: " + list.item(0).getNodeValue());

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

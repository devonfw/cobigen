package com.capgemini.cobigen.xmlplugin.inputreader;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

import com.capgemini.cobigen.api.to.MatcherTo;

/**
 * Extracts class names out of xmi files to generate multiple classes out of one source file.
 */
public class XmiClassReader {
    // TODO get the right input file
    // something like:
    // Path cobigenConfigFolder = new
    // File("src/test/resources/testdata/integrationtest/uml-basic-test").toPath();
    // Path input = cobigenConfigFolder.resolve("uml.xml");

    // parsing the xmi:
    // 1: cobigen uses JAXB: ask Ruben if we can use this parser to get all nodenames (class names)
    // 2: use XPath to get every occurrence of packagedElement like
    // "XMI/Model/packagedElement/packagedElement/@name"
    // some kind of iteration is needed.

    // return all extracted Names as List<Object>

    /**
     *
     * @return a list of objects which represent the name of every class in the given xmi.
     */
    List<Object> getClassNames(Document doc) {
        List<Object> classesNames = new ArrayList<Object>();
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "XMI/Model/packagedElement/packagedElement[@type='uml:Class']/@name";

        System.out.println("I AM IN XmiClassReader");
        NodeList nodeList = null;
        try {
            nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                classesNames.add(nodeList.item(i).getFirstChild().getNodeValue());
            }
            System.out.println("Classes Names List: " + Arrays.toString(classesNames.toArray()));
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // combine xpath with input
        return classesNames;
    }

    public List<Object> getInputObjects(Object input, Charset inputCharset) {

        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "XMI/Model/packagedElement/packagedElement[@type='uml:Class']";
        Document newXmlDocument = null;

        NodeList list = null;
        try {
            list = (NodeList) xPath.evaluate(expression, input, XPathConstants.NODESET);
        } catch (XPathExpressionException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

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
        return docsList;
    }

    Document getInput(MatcherTo matcher) {
        Object targetXpath = matcher.getTarget();
        if (targetXpath instanceof Document) {
            return ((Document) targetXpath);
        }
        return null;
    }

}

package com.capgemini.cobigen.xmlplugin.inputreader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
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

    Document getInput(MatcherTo matcher) {
        Object targetXpath = matcher.getTarget();
        if (targetXpath instanceof Document) {
            return ((Document) targetXpath);
        }
        return null;
    }

}

package com.capgemini.cobigen.xmlplugin.matcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.capgemini.cobigen.api.to.MatcherTo;
import com.capgemini.cobigen.api.to.VariableAssignmentTo;

/**
 *
 */
public class XPathLogic {

    public boolean matchesXPath(MatcherTo matcher, Logger LOG) {
        // #112
        Object targetXpath = matcher.getTarget();

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOG.info("Document builder initialization failed!");
            e.printStackTrace();
        }

        if (targetXpath instanceof Document) {
            Document document = ((Document) targetXpath);

            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = matcher.getValue();

            System.out.println("Expresion Xpath:\t" + expression);
            System.out.println("I AM IN matches");

            NodeList nodeList = null;
            try {
                nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
                if (nodeList == null) {
                    return false;
                } else {
                    return true;
                }
                // for (int i = 0; i < nodeList.getLength(); i++) {
                // System.out.println(nodeList.item(i).getFirstChild().getNodeValue());
                // }
                // return nodeList.item(0).getFirstChild().getNodeValue() != null;
            } catch (XPathExpressionException e) {
                // TODO Auto-generated catch block
                LOG.info("Matcher Xpath expression is not correct!");
                e.printStackTrace();
            }
        }
        return false;
    }

    public Map<String, String> resolveVariablesXPath(MatcherTo matcher,
        List<VariableAssignmentTo> variableAssignments) {
        // TODO #112
        Object targetXpath = matcher.getTarget();

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();

        }
        if (targetXpath instanceof Document) {
            Map<String, String> resolvedVariables = new HashMap<>();
            for (VariableAssignmentTo va : variableAssignments) {
                Document document = ((Document) targetXpath);
                String resultXpath = "Error_See_XMLMatcher";

                XPath xPath = XPathFactory.newInstance().newXPath();
                String expression = va.getValue();

                System.out.println("Expresion Xpath:\t" + expression);
                System.out.println("I AM IN resolveVariables");
                System.out.println(document.hasAttributes() + " " + document.getNodeValue());

                String entity = new String();
                try {
                    entity = (String) xPath.compile("[" + expression + "]").evaluate(document, XPathConstants.STRING);
                    System.out.println(entity);
                    resolvedVariables.put(va.getVarName(), entity);
                } catch (XPathExpressionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return resolvedVariables;
        }
        return null;
    }
}

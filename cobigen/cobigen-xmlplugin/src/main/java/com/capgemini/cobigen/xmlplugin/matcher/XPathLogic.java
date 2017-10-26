package com.capgemini.cobigen.xmlplugin.matcher;

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
                for (int i = 0; i < nodeList.getLength(); i++) {
                    System.out.println(nodeList.item(i).getFirstChild().getNodeValue());
                }
                if (nodeList == null) {
                    return false;
                } else {
                    return true;
                }
            } catch (XPathExpressionException e) {
                // TODO Auto-generated catch block
                LOG.info("Matcher Xpath expression is not correct!");
                e.printStackTrace();
            }
        }
        return false;
    }

    public String resolveVariablesXPath(MatcherTo matcher, VariableAssignmentTo va) {
        // TODO #112
        Object targetXpath = matcher.getTarget();
        String entity = new String("Error_XPATH_SeeXpathLogic");

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();

        }
        System.out.println(targetXpath.getClass());
        if (targetXpath instanceof Document) {
            Document document = ((Document) targetXpath);

            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = va.getValue();

            System.out.println("Expresion Xpath:\t" + expression);
            System.out.println("I AM IN resolveVariables");

            try {
                NodeList list = (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);
                entity = list.item(0).getNodeValue();
                System.out.println("Entity resolvedVaribales: " + entity);
            } catch (XPathExpressionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return entity;
        }
        return null;
    }
}

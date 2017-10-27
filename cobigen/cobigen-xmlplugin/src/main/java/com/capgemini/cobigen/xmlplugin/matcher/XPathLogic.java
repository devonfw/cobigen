package com.capgemini.cobigen.xmlplugin.matcher;

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

        if (targetXpath instanceof Document) {
            Document document = ((Document) targetXpath);

            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = matcher.getValue();

            NodeList nodeList = null;
            try {
                nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
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

        Object targetXpath = matcher.getTarget();
        String entity = new String("Error_XPATH_SeeXpathLogic");

        if (targetXpath instanceof Document) {
            Document document = ((Document) targetXpath);

            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = va.getValue();

            try {
                NodeList list = (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);
                entity = list.item(0).getNodeValue();
            } catch (XPathExpressionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return entity;
        }
        return null;
    }
}

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
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.to.MatcherTo;
import com.capgemini.cobigen.api.to.VariableAssignmentTo;
import com.google.common.collect.Maps;

/**
 * {@link MatcherInterpreter} for XML matcher configurations.
 */
public class XmlMatcher implements MatcherInterpreter {

    /**
     * Assigning logger to XmlClassMatcher
     */
    private static final Logger LOG = LoggerFactory.getLogger(XmlMatcher.class);

    /**
     * Currently supported matcher types
     *
     * @author fkreis (18.11.2014)
     */
    private enum MatcherType {
        /** Document's root name */
        NODENAME,
        /** Xpath expression group assignment */
        XPATH
    }

    /**
     * Available variable types for the matcher
     *
     * @author fkreis (18.11.2014)
     */
    private enum VariableType {
        /** Constant variable assignment */
        CONSTANT,
        /** Regular expression group assignment */
        REGEX,
        /** Xpath expression group assignment */
        XPATH
    }

    @Override
    public boolean matches(MatcherTo matcher) {
        try {
            MatcherType matcherType = Enum.valueOf(MatcherType.class, matcher.getType().toUpperCase());
            switch (matcherType) {
            case NODENAME:
                Object target = matcher.getTarget();
                if (target instanceof Document) {
                    String documentRootName = ((Document) target).getDocumentElement().getNodeName();
                    // return documentRootName.equals(matcher.getValue());
                    return documentRootName != null && !documentRootName.equals("")
                        && matcher.getValue().matches(documentRootName);
                }
                break;
            case XPATH:
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

                    NodeList nodeList;
                    try {
                        nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            System.out.println(nodeList.item(i).getFirstChild().getNodeValue());
                        }
                        return nodeList.item(0).getFirstChild().getNodeValue() != null;
                    } catch (XPathExpressionException e) {
                        // TODO Auto-generated catch block
                        LOG.info("Matcher Xpath expression is not correct!");
                        e.printStackTrace();
                    }
                }
                return false;
            }
        } catch (IllegalArgumentException e) {
            LOG.info("Matcher type '{}' not registered --> no match!", matcher.getType());
        }
        return false;
    }

    @Override
    public Map<String, String> resolveVariables(MatcherTo matcher, List<VariableAssignmentTo> variableAssignments)
        throws InvalidConfigurationException {

        try {
            MatcherType matcherType = Enum.valueOf(MatcherType.class, matcher.getType().toUpperCase());
            Map<String, String> resolvedVariables;
            switch (matcherType) {
            case NODENAME:
                resolvedVariables = new HashMap<>();
                for (VariableAssignmentTo va : variableAssignments) {
                    VariableType variableType = Enum.valueOf(VariableType.class, va.getType().toUpperCase());
                    switch (variableType) {
                    case CONSTANT:
                        resolvedVariables.put(va.getVarName(), va.getValue());
                        break;
                    case REGEX:
                        // TODO #64
                    }
                }
                return resolvedVariables;
            case XPATH:
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
                    resolvedVariables = new HashMap<>();
                    for (VariableAssignmentTo va : variableAssignments) {
                        Document document = ((Document) targetXpath);
                        String resultXpath = "Error_See_XMLMatcher";

                        XPath xPath = XPathFactory.newInstance().newXPath();
                        String expression = va.getValue();

                        System.out.println("Expresion Xpath:\t" + expression);
                        System.out.println("I AM IN resolveVariables");

                        NodeList nodeList;
                        try {
                            nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
                            for (int i = 0; i < nodeList.getLength(); i++) {
                                resultXpath = nodeList.item(i).getFirstChild().getNodeValue();

                                resolvedVariables.put(va.getVarName(), resultXpath);
                                System.out.println(resultXpath);
                            }
                        } catch (XPathExpressionException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    return resolvedVariables;
                }
            default:
                break;
            }
        } catch (IllegalArgumentException e) {
            LOG.warn("Matcher type '{}' not registered --> no match!", matcher.getType());
        }
        return Maps.newHashMap();
    }

}

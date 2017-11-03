package com.capgemini.cobigen.xmlplugin.matcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

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

    private XPathLogic logic = new XPathLogic();

    /**
     * Currently supported matcher types
     *
     * @author fkreis (18.11.2014)
     */
    private enum MatcherType {
        /** Document's root name */
        NODENAME,
        /** Should match if input is a Document */
        DOCUMENT,
        /** Container matcher for UML diagrams */
        UML
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
            Object target = matcher.getTarget();
            switch (matcherType) {
            case NODENAME:
                if (target instanceof Document) {
                    String documentRootName = ((Document) target).getDocumentElement().getNodeName();
                    // return documentRootName.equals(matcher.getValue());
                    return documentRootName != null && !documentRootName.equals("")
                        && matcher.getValue().matches(documentRootName);
                }
                break;
            case DOCUMENT:
                return matcher.getTarget().getClass().getSimpleName().toLowerCase()
                    .equals(matcher.getValue().toLowerCase());
            case UML:
                if (target instanceof Document) {
                    return ((Document) target).getDocumentElement().getNodeName().equals(matcher.getValue());
                }
                break;
            }

        } catch (IllegalArgumentException e) {
            LOG.info("Matcher type '{}' not registered --> no match!", matcher.getType());
        }
        return false;
    }

    @Override
    public Map<String, String> resolveVariables(MatcherTo matcher, List<VariableAssignmentTo> variableAssignments)
        throws InvalidConfigurationException {

        Map<String, String> resolvedVariables = new HashMap<>();
        try {
            MatcherType matcherType = Enum.valueOf(MatcherType.class, matcher.getType().toUpperCase());
            switch (matcherType) {
            case NODENAME:
                for (VariableAssignmentTo va : variableAssignments) {
                    VariableType variableType = Enum.valueOf(VariableType.class, va.getType().toUpperCase());
                    switch (variableType) {
                    case CONSTANT:
                        resolvedVariables.put(va.getVarName(), va.getValue());
                    case REGEX:
                        // TODO #64
                    }
                }
                return resolvedVariables;
            default:
                break;
            }
            // In case of being an XMI document
            for (VariableAssignmentTo va : variableAssignments) {
                VariableType variableType = Enum.valueOf(VariableType.class, va.getType().toUpperCase());
                switch (variableType) {
                case XPATH:
                    resolvedVariables.put(va.getVarName(), logic.resolveVariablesXPath(matcher, va));
                    break;
                case CONSTANT:
                    resolvedVariables.put(va.getVarName(), va.getValue());
                    break;
                }

            }
            return resolvedVariables;
        } catch (IllegalArgumentException e) {
            LOG.warn("Matcher type '{}' not registered --> no match!", matcher.getType());
        }
        return Maps.newHashMap();
    }

}

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

    /**
     * Currently supported matcher types
     *
     * @author fkreis (18.11.2014)
     */
    private enum MatcherType {
        /** Document's root name */
        NODENAME,
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
        REGEX
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
            switch (matcherType) {
            case NODENAME:
                Map<String, String> resolvedVariables = new HashMap<>();
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
            default:
                break;
            }
        } catch (IllegalArgumentException e) {
            LOG.warn("Matcher type '{}' not registered --> no match!", matcher.getType());
        }
        return Maps.newHashMap();
    }

}

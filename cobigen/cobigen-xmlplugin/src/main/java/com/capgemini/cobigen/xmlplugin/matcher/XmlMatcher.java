package com.capgemini.cobigen.xmlplugin.matcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.extension.IMatcher;
import com.capgemini.cobigen.extension.to.MatcherTo;
import com.capgemini.cobigen.extension.to.VariableAssignmentTo;

/**
 *
 * @author fkreis (18.11.2014)
 */
public class XmlMatcher implements IMatcher {

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
        /** Full Qualified Name Matching */
        FQN,
        /** Integration Test Matching */
        TEST
    }

    /**
     * Available variable types for the matcher
     *
     * @author fkreis (18.11.2014)
     */
    private enum VariableType {
    }

    /**
     * {@inheritDoc}
     * @author fkreis (18.11.2014)
     */
    @Override
    public boolean matches(MatcherTo matcher) {
        try {
            MatcherType matcherType = Enum.valueOf(MatcherType.class, matcher.getType().toUpperCase());
            switch (matcherType) {
            case FQN:
                // TODO
                // String fqn = ""; // ???;
                // return fqn != null && fqn.matches(matcher.getValue());
                return false;
            case TEST:
                return true;
            }
        } catch (IllegalArgumentException e) {
            LOG.info("Matcher type '{}' not registered --> no match!", matcher.getType());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * @author fkreis (18.11.2014)
     */
    @Override
    public Map<String, String> resolveVariables(MatcherTo matcher,
        List<VariableAssignmentTo> variableAssignments) throws InvalidConfigurationException {
        // TODO currently no variables supported
        HashMap<String, String> result = new HashMap<>();
        return result;
    }

}

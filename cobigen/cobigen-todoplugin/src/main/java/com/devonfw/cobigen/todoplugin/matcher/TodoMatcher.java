package com.devonfw.cobigen.todoplugin.matcher;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.extension.MatcherInterpreter;
import com.devonfw.cobigen.api.to.MatcherTo;
import com.devonfw.cobigen.api.to.VariableAssignmentTo;
import com.devonfw.cobigen.todoplugin.inputreader.TodoInputReader;
import com.google.common.collect.Maps;

/**
 * Matcher implementation for the Todo plug-in
 */
public class TodoMatcher implements MatcherInterpreter {
    /** Logger instance */
    private static final Logger LOG = LoggerFactory.getLogger(TodoMatcher.class);

    /** Currently supported matcher types */
    private enum MatcherType {
        /** Full Qualified Name Matching */
        FQN
    }

    /** Available variable types for the matcher */
    private enum VariableType {
        /** Constant variable assignment */
        CONSTANT,
        /** Regular expression group assignment */
        REGEX
    }

    @Override
    public boolean matches(MatcherTo matcher) {

        LOG.debug("Type {} matches FQN ? ", matcher.getType());
        boolean matchesFqn = matcher.getType().toUpperCase().equals(MatcherType.FQN.toString());
        if (matchesFqn) {
            String fqn = getFqn(matcher);
            LOG.debug("Matching input FQN {} against regex '{}'", fqn, matcher.getValue());
            return fqn != null && fqn.matches(matcher.getValue());
        }
        return matchesFqn;
    }

    /**
     * Returns the full qualified name of the matchers input
     *
     * @param matcher
     *            {@link MatcherTo} to retrieve the input from
     * @return the full qualified name of the matchers input
     */
    private String getFqn(MatcherTo matcher) {

        Object target = matcher.getTarget();
        String fqn = null;

        try {
            // Input corresponds to the parsed file
            Map<String, Object> mapModel = new TodoInputReader().createModel(target);
            mapModel = (Map<String, Object>) mapModel.get("model");
            fqn = Paths.get(mapModel.get("path").toString()).getFileName().toString();
            // We remove the file extension
            extensionToLowerCase(fqn);
            fqn = FilenameUtils.removeExtension(fqn);
        } catch (NullPointerException e) {
            return null;
        }
        return fqn;
    }

    /**
     * If any file extension is found, converts it to lower case
     * @param fqn
     *            full qualified name of the input file
     * @return The file name with the extension in lower case (if it does not have extension, nothing changes)
     */
    private String extensionToLowerCase(String fqn) {
        int index = FilenameUtils.indexOfExtension(fqn);
        if (index == -1) {
            return fqn;
        } else {
            String extension = fqn.substring(index).toLowerCase();
            return fqn.substring(0, index) + extension;
        }

    }

    @Override
    public Map<String, String> resolveVariables(MatcherTo matcher, List<VariableAssignmentTo> variableAssignments)
        throws InvalidConfigurationException {

        try {
            MatcherType matcherType = Enum.valueOf(MatcherType.class, matcher.getType().toUpperCase());
            switch (matcherType) {
            case FQN:
                String fqn = getFqn(matcher);
                return getResolvedVariables(matcherType, matcher.getValue(), fqn, variableAssignments);
            default:
                break;
            }
        } catch (IllegalArgumentException e) {
            LOG.warn("Matcher type '{}' not registered --> no match!", matcher.getType());
        }
        return Maps.newHashMap();
    }

    /**
     * Resolves all variables for this trigger
     *
     * @param matcherType
     *            matcher type
     * @param matcherValue
     *            matcher value
     * @param stringToMatch
     *            String to match
     * @param variableAssignments
     *            variable assigments to be resolved
     * @return a {@link Map} from variable name to the resolved value
     * @throws InvalidConfigurationException
     *             if some of the matcher type and variable type combinations are not supported
     * @author mbrunnli (15.04.2013)
     */
    public Map<String, String> getResolvedVariables(MatcherType matcherType, String matcherValue, String stringToMatch,
        List<VariableAssignmentTo> variableAssignments) throws InvalidConfigurationException {

        Map<String, String> resolvedVariables = new HashMap<>();
        for (VariableAssignmentTo va : variableAssignments) {
            VariableType variableType = Enum.valueOf(VariableType.class, va.getType().toUpperCase());
            switch (variableType) {
            case CONSTANT:
                resolvedVariables.put(va.getVarName(), va.getValue());
                break;
            case REGEX:
                String resolvedRegexValue = resolveRegexValue(matcherType, matcherValue, stringToMatch, va);
                resolvedVariables.put(va.getVarName(), resolvedRegexValue != null ? resolvedRegexValue : "");
                break;
            }
        }
        return resolvedVariables;
    }

    /**
     * Resolves the variable assignments of type {@link VariableType#REGEX}
     *
     * @param matcherType
     *            type of the matcher
     * @param matcherValue
     *            value of the matcher
     * @param stringToMatch
     *            string to match
     * @param va
     *            {@link VariableAssignmentTo} to be resolved
     * @return the resolved variable
     * @throws InvalidConfigurationException
     *             thrown if the matcher type and matcher value does not work in combination
     * @author mbrunnli (08.04.2014)
     */
    private String resolveRegexValue(MatcherType matcherType, String matcherValue, String stringToMatch,
        VariableAssignmentTo va) throws InvalidConfigurationException {

        Pattern p = Pattern.compile(matcherValue);
        Matcher m = p.matcher(stringToMatch);

        if (m != null) {
            if (m.matches()) {
                try {
                    String value = m.group(Integer.parseInt(va.getValue()));
                    // removed a not null check (github issue #159) causing a InvalidConfigurationException
                    // thrown when value == null
                    return value;
                } catch (NumberFormatException e) {
                    LOG.error(
                        "The VariableAssignment '{}' of Matcher of type '{}' should have an integer as value"
                            + " representing a regular expression group.\nCurrent value: '{}'",
                        va.getType().toUpperCase(), matcherType.toString(), va.getValue(), e);
                    throw new InvalidConfigurationException("The VariableAssignment '" + va.getType().toUpperCase()
                        + "' of Matcher of type '" + matcherType.toString()
                        + "' should have an integer as value representing a regular expression group.\nCurrent value: '"
                        + va.getValue() + "'");
                } catch (IndexOutOfBoundsException e) {
                    LOG.error(
                        "The VariableAssignment '{}' of Matcher of type '{}' declares a regular expression"
                            + " group not in range.\nCurrent value: '{}'",
                        va.getType().toUpperCase(), matcherType.toString(), va.getValue(), e);
                    throw new InvalidConfigurationException("The VariableAssignment '" + va.getType().toUpperCase()
                        + "' of Matcher of type '" + matcherType.toString()
                        + "' declares a regular expression group not in range.\nCurrent value: '" + va.getValue()
                        + "'");
                }
            } // else should not occur as #matches(...) will be called beforehand
        } else {
            throw new InvalidConfigurationException(
                "The VariableAssignment type 'REGEX' can only be combined with matcher type 'FQN' or 'PACKAGE'");
        }
        return null; // should not occur
    }

}

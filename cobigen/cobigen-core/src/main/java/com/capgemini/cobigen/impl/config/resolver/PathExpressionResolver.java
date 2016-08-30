package com.capgemini.cobigen.impl.config.resolver;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.capgemini.cobigen.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;
import com.capgemini.cobigen.impl.config.ContextConfiguration;
import com.capgemini.cobigen.impl.util.StringUtil;
import com.google.common.collect.Maps;

/**
 * The {@link PathExpressionResolver} provides an interface for replacing any variable expression in a
 * {@link String} from the context xml
 *
 * @author mbrunnli (18.02.2013)
 */
public class PathExpressionResolver {

    /**
     * Pointer to the {@link ContextConfiguration} which provides all values for the variables to be resolved
     */
    private Map<String, String> variables = Maps.newHashMap();

    /**
     * Creates a new {@link PathExpressionResolver} for the given config
     *
     * @param variables
     *            Map of current settings
     * @author mbrunnli (18.02.2013)
     */
    public PathExpressionResolver(Map<String, String> variables) {

        // Adapt trigger values to make them equally accessible as in the whole FreeMarker model
        for (String key : variables.keySet()) {
            this.variables.put("variables." + key, variables.get(key));
        }
        adaptVariables();
    }

    /**
     * Adapts the current variable values such that each dot will be replaced by a slash such that the
     * variables can be used to construct paths
     *
     * @author mbrunnli (15.04.2013)
     */
    private void adaptVariables() {

        HashMap<String, String> newVariables = new HashMap<>();
        for (String var : variables.keySet()) {
            String value = variables.get(var);
            if (value != null) {
                newVariables.put(var, value.replaceAll("\\.", "/"));
            } else {
                newVariables.put(var, value);
            }
        }
        variables = newVariables;
    }

    /**
     * Checks whether all expressions in the given string are valid and can be resolved
     *
     * @param in
     *            string to be parsed
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @throws UnknownContextVariableException
     *             if there is a unknown context variable used in the string
     * @author mbrunnli (11.03.2013)
     */
    public void checkExpressions(String in)
        throws UnknownExpressionException, UnknownContextVariableException {

        evaluateExpressions(in);
    }

    /**
     * Evaluates variable expressions within a string stated in the configuration xml
     *
     * @param in
     *            {@link String} containing variable expressions
     * @return the given {@link String} where all variable expressions are replaced by its values
     * @throws UnknownContextVariableException
     *             if a context variable could not be resolved
     * @author mbrunnli (18.02.2013)
     */
    public String evaluateExpressions(String in) throws UnknownContextVariableException {

        if (in == null) {
            return null;
        }
        Pattern p = Pattern.compile("\\$\\{([^?#}]+)(((\\?|#)[^}?#]+)*)\\}");
        Matcher m = p.matcher(in);
        StringBuffer out = new StringBuffer();
        while (m.find()) {
            if (!variables.containsKey(m.group(1))) {
                throw new UnknownContextVariableException(m.group(1));
            }

            if (variables.get(m.group(1)) != null) {
                if (m.group(2) != null) {
                    boolean first = true;
                    String modifiedValue = variables.get(m.group(1));
                    for (String modifier : m.group(2).split("(\\?|#)")) {
                        if (first) {
                            first = false;
                            continue; // ignore first as always empty due to beginning '?'
                        }
                        modifiedValue = applyStringModifier(modifier, modifiedValue);
                    }
                    m.appendReplacement(out, modifiedValue);
                } else {
                    m.appendReplacement(out, variables.get(m.group(1)));
                }
            } else {
                m.appendReplacement(out, "");
            }
        }
        m.appendTail(out);

        // Cleanup empty path segements
        String rawPath = out.toString();
        return rawPath.replaceAll("/+", "/");
    }

    /**
     * Applies the given {@link String} modifier defined by ?modifier behind the variable reference
     *
     * @param modifierName
     *            name of the {@link String} modifier to be applied
     * @param string
     *            {@link String} the modifier should be applied on
     * @return the modified {@link String}
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @author mbrunnli (18.02.2013)
     */
    private String applyStringModifier(String modifierName, String string) throws UnknownExpressionException {

        // simple operators
        if (modifierName.equals("cap_first")) {
            return StringUtil.capFirst(string);
        } else if (modifierName.equals("uncap_first")) {
            return StringUtil.uncapFirst(string);
        } else if (modifierName.equals("lower_case")) {
            return string.toLowerCase();
        } else if (modifierName.equals("upper_case")) {
            return string.toUpperCase();
        }

        String parameterRegex = "\\s*'([^']*)'\\s*";

        // ?replace(String regex, String replacement)
        Pattern p = Pattern.compile("replace\\(" + parameterRegex + "," + parameterRegex + "\\)");
        Matcher m = p.matcher(modifierName);

        if (m.matches()) {
            return string.replaceAll(m.group(1), m.group(2));
        }

        // ?removeSuffix(String suffix)
        p = Pattern.compile("removeSuffix\\(" + parameterRegex + "\\)");
        m = p.matcher(modifierName);

        if (m.matches() && string.endsWith(m.group(1))) {
            return string.substring(0, string.length() - m.group(1).length());
        }

        // ?removePrefix(String prefix)
        p = Pattern.compile("removePrefix\\(" + parameterRegex + "\\)");
        m = p.matcher(modifierName);

        if (m.matches() && string.startsWith(m.group(1))) {
            return string.substring(m.group(1).length(), string.length());
        }

        throw new UnknownExpressionException("?" + modifierName);
    }

}

package com.capgemini.cobigen.javaplugin.matcher.resolver;

import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.exceptions.UnknownExpressionException;

/**
 * The {@link TriggerExpressionResolver} resolves expressions for {@link Trigger} objects.
 *
 * @author mbrunnli (05.04.2013)
 */
public class TriggerExpressionResolver {

    /**
     * The current triggered POJO {@link Class}
     */
    private Class<?> pojo;

    /**
     * Assigning logger to TriggerExpressionResolver
     */
    private static final Logger LOG = LoggerFactory.getLogger(TriggerExpressionResolver.class);

    /**
     * Pattern for 'instanceof' expression; syntax: "instanceof p.a.c.k.a.g.e.ClassName"
     */
    private static Pattern instanceOfPattern = Pattern.compile("\\s*instanceof\\s+([^\\s]+)");

    /**
     * Pattern for 'isAbstract' expression
     */
    private static Pattern isAbstractPattern = Pattern.compile("\\s*isAbstract\\s*");

    /**
     * Creates a new {@link TriggerExpressionResolver} for the given pojo with its {@link ClassLoader}
     *
     * @param pojo
     *            current triggered POJO {@link Class}
     * @author mbrunnli (15.04.2013)
     */
    public TriggerExpressionResolver(Class<?> pojo) {
        this.pojo = pojo;
    }

    /**
     * Evaluates the given expression
     *
     * @param expression
     *            to be evaluated
     * @return <code>true</code> if the expression is valid,<br>
     *         <code>false</code>, otherwise or if any exception occurred during the evaluation
     * @author mbrunnli (05.04.2013)
     */
    public boolean evaluateExpression(String expression) {
        Matcher m = instanceOfPattern.matcher(expression);
        if (m.matches()) {
            try {
                return pojo.getClassLoader().loadClass(m.group(1)).isAssignableFrom(pojo);
            } catch (ClassNotFoundException e) {
                LOG.error(
                    "{}",
                    "this exception should not block the user, as the context currently might be not of interest",
                    e);
            }
        } else {
            m = isAbstractPattern.matcher(expression);
            if (m.matches()) {
                return Modifier.isAbstract(pojo.getModifiers());
            } else {
                throw new UnknownExpressionException("Unknown trigger expression: '" + expression + "'");
            }
        }
        return false;
    }
}

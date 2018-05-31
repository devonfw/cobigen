package com.capgemini.cobigen.javaplugin.matcher.resolver;

import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.exception.UnknownExpressionException;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * The {@link TriggerExpressionResolver} to resolve the matcher's values.
 */
public class TriggerExpressionResolver {

    /**
     * The current triggered POJO.
     */
    private Object pojo;

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
     * @param reflectionClass
     *            current triggered POJO {@link Class}
     * @author mbrunnli (15.04.2013)
     */
    public TriggerExpressionResolver(Class<?> reflectionClass) {
        pojo = reflectionClass;
    }

    /**
     * Creates a new {@link TriggerExpressionResolver} for the given parsed {@link JavaClass}
     * @param parsedClass
     *            parsed {@link JavaClass}
     * @author mbrunnli (24.02.2015)
     */
    public TriggerExpressionResolver(JavaClass parsedClass) {
        pojo = parsedClass;
    }

    /**
     * Evaluates the given expression
     *
     * @param expression
     *            to be evaluated
     * @return <code>true</code> if the expression is valid,<br>
     *         <code>false</code>, otherwise or if any exception occurred during the evaluation
     */
    public boolean evaluateExpression(String expression) {
        Matcher m = instanceOfPattern.matcher(expression);
        if (m.matches()) {
            if (pojo instanceof Class<?>) {
                try {
                    return ((Class<?>) pojo).getClassLoader().loadClass(m.group(1)).isAssignableFrom((Class<?>) pojo);
                } catch (ClassNotFoundException e) {
                    LOG.info("Could not load class '{}' to resolve expression '{}'.", m.group(1), expression);
                }
            } else if (pojo instanceof JavaClass) {
                return ((JavaClass) pojo).isA(m.group(1));
            }
        } else {
            m = isAbstractPattern.matcher(expression);
            if (m.matches()) {
                if (pojo instanceof Class<?>) {
                    return Modifier.isAbstract(((Class<?>) pojo).getModifiers());
                } else if (pojo instanceof JavaClass) {
                    return ((JavaClass) pojo).isAbstract();
                }
            } else {
                throw new UnknownExpressionException("Unknown trigger expression: '" + expression + "'");
            }
        }
        return false;
    }
}

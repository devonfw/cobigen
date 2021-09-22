package com.devonfw.cobigen.javaplugin.matcher.resolver;

import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.UnknownExpressionException;
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
   * @param reflectionClass current triggered POJO {@link Class}
   * @author mbrunnli (15.04.2013)
   */
  public TriggerExpressionResolver(Class<?> reflectionClass) {

    this.pojo = reflectionClass;
  }

  /**
   * Creates a new {@link TriggerExpressionResolver} for the given parsed {@link JavaClass}
   *
   * @param parsedClass parsed {@link JavaClass}
   * @author mbrunnli (24.02.2015)
   */
  public TriggerExpressionResolver(JavaClass parsedClass) {

    this.pojo = parsedClass;
  }

  /**
   * Evaluates the given expression
   *
   * @param expression to be evaluated
   * @return <code>true</code> if the expression is valid,<br>
   *         <code>false</code>, otherwise or if any exception occurred during the evaluation
   */
  public boolean evaluateExpression(String expression) {

    Matcher m = instanceOfPattern.matcher(expression);
    if (m.matches()) {
      if (this.pojo instanceof Class<?>) {
        try {
          return ((Class<?>) this.pojo).getClassLoader().loadClass(m.group(1)).isAssignableFrom((Class<?>) this.pojo);
        } catch (ClassNotFoundException e) {
          LOG.info("Could not load class '{}' to resolve expression '{}'.", m.group(1), expression);
        }
      } else if (this.pojo instanceof JavaClass) {
        return ((JavaClass) this.pojo).isA(m.group(1));
      }
    } else {
      m = isAbstractPattern.matcher(expression);
      if (m.matches()) {
        if (this.pojo instanceof Class<?>) {
          return Modifier.isAbstract(((Class<?>) this.pojo).getModifiers());
        } else if (this.pojo instanceof JavaClass) {
          return ((JavaClass) this.pojo).isAbstract();
        }
      } else {
        throw new UnknownExpressionException("Unknown trigger expression: '" + expression + "'");
      }
    }
    return false;
  }
}

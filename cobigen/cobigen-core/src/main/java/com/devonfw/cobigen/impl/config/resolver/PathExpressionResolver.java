package com.devonfw.cobigen.impl.config.resolver;

import com.devonfw.cobigen.impl.config.entity.Variables;
import com.devonfw.cobigen.impl.exceptions.UnknownContextVariableException;

/**
 * The {@link PathExpressionResolver} provides an interface for replacing any variable expression in a {@link String}
 * from the context xml
 */
public class PathExpressionResolver {

  /**
   * Pointer to the {@link ContextConfiguration} which provides all values for the variables to be resolved
   */
  private Variables variables;

  /**
   * Creates a new {@link PathExpressionResolver} for the given config
   *
   * @param variables Map of current settings
   */
  public PathExpressionResolver(Variables variables) {

    super();
    this.variables = variables;
  }

  /**
   * Evaluates variable expressions within a string stated in the configuration xml
   *
   * @param relativeUnresolvedPath virtual target folder path to be resolved
   * @return the given {@link String} where all variable expressions are replaced by its values
   * @throws UnknownContextVariableException if a context variable could not be resolved
   */
  public String evaluateExpressions(String relativeUnresolvedPath) throws UnknownContextVariableException {

    if (relativeUnresolvedPath == null) {
      return null;
    }
    String resolvedPath = this.variables.resolve(relativeUnresolvedPath, '/');
    // Cleanup empty path segments
    return resolvedPath.replaceAll("/+", "/");
  }

}

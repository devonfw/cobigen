package com.capgemini.cobigen.impl.config.resolver;

import com.capgemini.cobigen.impl.config.ContextConfiguration;
import com.capgemini.cobigen.impl.config.entity.Variables;
import com.capgemini.cobigen.impl.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.impl.exceptions.UnknownExpressionException;

/**
 * The {@link PathExpressionResolver} provides an interface for replacing any variable expression in a
 * {@link String} from the context xml
 */
public class PathExpressionResolver {

    /**
     * Pointer to the {@link ContextConfiguration} which provides all values for the variables to be resolved
     */
    private Variables variables;

    /**
     * Creates a new {@link PathExpressionResolver} for the given config
     *
     * @param variables
     *            Map of current settings
     */
    public PathExpressionResolver(Variables variables) {

        super();
        this.variables = variables;
    }

    /**
     * Checks whether all expressions in the given string are valid and can be resolved
     *
     * @param relativeUnresolvedPath
     *            virtual target folder path to be checked for resolved
     * @throws UnknownExpressionException
     *             if there is an unknown variable modifier
     * @throws UnknownContextVariableException
     *             if there is a unknown context variable used in the string
     */
    public void checkExpressions(String relativeUnresolvedPath)
        throws UnknownExpressionException, UnknownContextVariableException {

        evaluateExpressions(relativeUnresolvedPath);
    }

    /**
     * Evaluates variable expressions within a string stated in the configuration xml
     *
     * @param relativeUnresolvedPath
     *            virtual target folder path to be resolved
     * @return the given {@link String} where all variable expressions are replaced by its values
     * @throws UnknownContextVariableException
     *             if a context variable could not be resolved
     */
    public String evaluateExpressions(String relativeUnresolvedPath) throws UnknownContextVariableException {

        if (relativeUnresolvedPath == null) {
            return null;
        }
        String resolvedPath = variables.resolve(relativeUnresolvedPath, '/');
        // Cleanup empty path segments
        return resolvedPath.replaceAll("/+", "/");
    }

}

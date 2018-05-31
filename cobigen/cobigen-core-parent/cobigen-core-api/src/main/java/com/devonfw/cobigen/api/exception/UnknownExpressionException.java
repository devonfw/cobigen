package com.devonfw.cobigen.api.exception;


/**
 * Occurs if a variable expression in the configuration xml is unknown
 *
 * @author mbrunnli (18.02.2013)
 */
public class UnknownExpressionException extends InvalidConfigurationException {

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 1;

    /**
     * Creates a new {@link UnknownExpressionException}
     *
     * @param unknownExpression
     *            unknown expression which could not be resolved
     * @author mbrunnli (18.02.2013)
     */
    public UnknownExpressionException(String unknownExpression) {

        super("Unknown variable expression in the configuration.xml: " + unknownExpression);
    }
}

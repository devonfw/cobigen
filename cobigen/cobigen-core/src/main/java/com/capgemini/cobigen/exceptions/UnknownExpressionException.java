/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.exceptions;

/**
 * Occurs if a variable expression in the configuration xml is unknown
 * 
 * @author mbrunnli (18.02.2013)
 */
public class UnknownExpressionException extends InvalidConfigurationException {

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = -8573224799473882852L;

    /**
     * Creates a new {@link UnknownExpressionException}
     * 
     * @param unknownExpression
     *        unknown expression which could not be resolved
     * @author mbrunnli (18.02.2013)
     */
    public UnknownExpressionException(String unknownExpression) {

        super("Unknown variable expression in the configuration.xml: " + unknownExpression);
    }
}

package com.capgemini.cobigen.impl.exceptions;

/**
 * The {@link UnknownContextVariableException} occurs a context variable is used, which is unknown by xsd
 * definition
 *
 * @author mbrunnli (19.02.2013)
 */
public class UnknownContextVariableException extends InvalidConfigurationException {

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 1;

    /**
     * Creates a new {@link UnknownContextVariableException} with the name of the unknown variable
     *
     * @param variableName
     *            name of the unknown variable
     * @author mbrunnli (19.02.2013)
     */
    public UnknownContextVariableException(String variableName) {

        super("Unknown context variable: " + variableName);
    }
}

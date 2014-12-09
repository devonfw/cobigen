package com.capgemini.cobigen.exceptions;

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
    private static final long serialVersionUID = -821298945327700134L;

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

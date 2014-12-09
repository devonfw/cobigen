package com.capgemini.cobigen.eclipse.common.exceptions;

/**
 * States that an exception occured during generator instance creation
 * @author mbrunnli (06.12.2014)
 */
public class GeneratorCreationException extends Exception {

    /**
     * Default serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link GeneratorCreationException}
     * @param message
     *            of the exception
     * @param cause
     *            of the exception
     * @author mbrunnli (06.12.2014)
     */
    public GeneratorCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}

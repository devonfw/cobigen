package com.capgemini.cobigen.exceptions;

/**
 * A technical runtime exception indicating an unexpected error cause.
 * @author mbrunnli (Jun 22, 2015)
 */
public class TechnicalRuntimeException extends RuntimeException {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link TechnicalRuntimeException} with the given message.
     * @param message
     *            informative message describing the exception or where it has been occurred
     * @author mbrunnli (Jun 22, 2015)
     */
    public TechnicalRuntimeException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link TechnicalRuntimeException} with the given message and cause.
     * @param message
     *            informative message describing the exception or where it has been occurred
     * @param cause
     *            originating this exception
     * @author mbrunnli (Jun 22, 2015)
     */
    public TechnicalRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}

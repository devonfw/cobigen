package com.devonfw.cobigen.api.exception;


/** Exception to indicate that an InputReader encountered a problem while parsing input content. */
public class InputReaderException extends CobiGenRuntimeException {

    /** Generate serial version UID */
    private static final long serialVersionUID = -3460790488241223241L;

    /**
     * Creates a new InputReaderException with the specified message
     * @param message
     *            describing the exception
     */
    public InputReaderException(String message) {
        super(message);
    }

    /**
     * Creates a new InputReaderException with the specified message and the causing {@link Throwable}
     * @param message
     *            describing the exception
     * @param cause
     *            the causing Throwable
     */
    public InputReaderException(String message, Throwable cause) {
        super(message, cause);
    }

}

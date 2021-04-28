package com.devonfw.cobigen.impl.exceptions;

/**
 * Thrown when an HTTP request returns an unexpected code
 */
public class HttpConnectionException extends Exception {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link HttpConnectionException} with the given message.
     * @param message
     *            informative message describing the exception or where it has been occurred
     */
    public HttpConnectionException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link HttpConnectionException} with the given message and cause.
     * @param message
     *            informative message describing the exception or where it has been occurred
     * @param cause
     *            originating this exception
     */
    public HttpConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}

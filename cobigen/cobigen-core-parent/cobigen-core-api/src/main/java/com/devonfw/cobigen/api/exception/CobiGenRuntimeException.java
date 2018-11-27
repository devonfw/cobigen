package com.devonfw.cobigen.api.exception;

/**
 * A technical runtime exception indicating an unexpected error cause.
 */
public class CobiGenRuntimeException extends RuntimeException {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * Creates a new {@link CobiGenRuntimeException} with the given message.
     * @param message
     *            informative message describing the exception or where it has been occurred
     */
    public CobiGenRuntimeException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link CobiGenRuntimeException} with the given message and cause.
     * @param message
     *            informative message describing the exception or where it has been occurred
     * @param cause
     *            originating this exception
     */
    public CobiGenRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}

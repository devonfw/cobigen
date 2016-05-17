package com.capgemini.cobigen.eclipse.common.exceptions;

/**
 *
 * @author mbrunnli (Jan 10, 2016)
 */
public class CobiGenEclipseRuntimeException extends RuntimeException {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link CobiGenEclipseRuntimeException} for the given message and cause.
     * @param message
     *            error message
     * @param cause
     *            of the exception
     * @author mbrunnli (Jan 10, 2016)
     */
    public CobiGenEclipseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}

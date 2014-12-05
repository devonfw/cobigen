package com.capgemini.cobigen.eclipse.common.exceptions;

/**
 * States that any logic has been triggered, which is currently not supported
 * @author mbrunnli (17.10.2014)
 */
public class NotYetSupportedException extends RuntimeException {

    /**
     * Default serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link NotYetSupportedException} with the given Message
     * @param msg
     *            error message
     * @author mbrunnli (17.10.2014)
     */
    public NotYetSupportedException(String msg) {
        super(msg + "\nPlease state a feature request on GitHub if you need further support.");
    }

}

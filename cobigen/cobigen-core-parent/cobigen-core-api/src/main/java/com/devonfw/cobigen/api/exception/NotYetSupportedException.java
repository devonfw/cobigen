package com.devonfw.cobigen.api.exception;

/**
 * States that any logic has been triggered, which is currently not supported.
 * @author mbrunnli (Jun 22, 2015)
 */
public class NotYetSupportedException extends CobiGenRuntimeException {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link NotYetSupportedException} with the given Message.
     * @param msg
     *            error message
     * @author mbrunnli (Jun 22, 2015)
     */
    public NotYetSupportedException(String msg) {
        super(msg + "\nPlease state a feature request on GitHub if you need further support.");
    }

}

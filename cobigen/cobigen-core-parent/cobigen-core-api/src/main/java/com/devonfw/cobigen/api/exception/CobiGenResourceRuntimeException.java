package com.devonfw.cobigen.api.exception;

/**
 * A runtime exception indicating that a resource couldn't be opened
 */
public class CobiGenResourceRuntimeException extends CobiGenRuntimeException {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1;

    /**
     * @param message
     *            informative message describing the exception or where it has been occurred
     */
    public CobiGenResourceRuntimeException(String message) {
        super(message);
    }

}

package com.capgemini.cobigen.eclipse.common.exceptions;

/**
 * Thrown if the input could not be read as expected.
 * @author mbrunnli (Jun 17, 2015)
 */
public class InvalidInputException extends Exception {

    /**
     * Generated Serial Version UID.
     */
    private static final long serialVersionUID = 6923294459129047262L;

    /** States, whether the exception has been initialized with a source exception */
    private boolean hasRootCause;

    /**
     * @see Exception#Exception(String)
     * @author mbrunnli (Jun 17, 2015)
     */
    public InvalidInputException(String message) {
        super(message);
        hasRootCause = false;
    }

    /**
     * @see Exception#Exception(String, Throwable)
     * @author mbrunnli (Jun 17, 2015)
     */
    public InvalidInputException(String message, Throwable t) {
        super(message, t);
        hasRootCause = true;
    }

    /**
     * Returns the field 'hasRootCause'
     * @return value of hasRootCause
     * @author mbrunnli (Jun 17, 2015)
     */
    public boolean hasRootCause() {
        return hasRootCause;
    }

}

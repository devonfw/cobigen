package com.capgemini.cobigen.senchaplugin.exceptions;

/**
 *
 * @author rudiazma (22 de jul. de 2016)
 */
public class JSParseError extends RuntimeException {

    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = 7583064321403970609L;

    /**
     * Creates an exception with the given message
     * @param msg
     *            error message
     * @author mbrunnli (18.06.2013)
     */
    public JSParseError(String msg) {
        super(msg);
    }

    /**
     * Creates an exception with the given message and root cause.
     * @param msg
     *            error message
     * @param cause
     *            root cause
     * @author mbrunnli (Jun 25, 2015)
     */
    public JSParseError(String msg, Throwable cause) {
        super(msg, cause);
    }

}

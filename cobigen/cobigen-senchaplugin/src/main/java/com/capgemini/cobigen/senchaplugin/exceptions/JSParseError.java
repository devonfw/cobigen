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
     * @author rudiazma (12 de sept. de 2016)
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
     * @author rudiazma (12 de sept. de 2016)
     */
    public JSParseError(String msg, Throwable cause) {
        super(msg, cause);
    }

}

package com.capgemini.cobigen.exceptions;

/**
 * The {@link UnknownTemplateException} occurs if a template is requested which is not registered in the
 * template configuration
 * @author mbrunnli (19.02.2013)
 */
public class UnknownTemplateException extends RuntimeException {

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = -8572955214162287243L;

    /**
     * Creates a new {@link UnknownTemplateException} with the given message
     * @param msg
     *            error message
     * @author mbrunnli (09.04.2014)
     */
    public UnknownTemplateException(String msg) {
        super(msg);
    }
}

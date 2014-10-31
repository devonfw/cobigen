/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.exceptions;

/**
 * This Exception indicates a problem while merging
 * @author mbrunnli (18.06.2013)
 */
public class MergeException extends Exception {

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
    public MergeException(String msg) {
        super(msg);
    }

}

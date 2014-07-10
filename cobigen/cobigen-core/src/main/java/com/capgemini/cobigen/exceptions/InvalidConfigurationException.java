/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.exceptions;

import java.io.File;

/**
 * Occurs if the configuration xml could not be parsed successfully
 * @author mbrunnli (19.02.2013)
 */
public class InvalidConfigurationException extends Exception {

    /**
     * Generated serial version UID
     */
    private static final long serialVersionUID = 8744712919444512918L;

    /**
     * Creates a new {@link InvalidConfigurationException} with the given message
     * @param file
     *            {@link File} causing the InvalidConfigurationException
     * @param msg
     *            error message of the exception
     * @param t
     *            cause exception
     * @author mbrunnli (25.04.2014)
     */
    public InvalidConfigurationException(File file, String msg, Throwable t) {
        super(file.getAbsolutePath().toString() + ":\n" + msg, t);
    }

    /**
     * Creates a new {@link InvalidConfigurationException} with the given message
     * @param file
     *            {@link File} causing the InvalidConfigurationException
     * @param msg
     *            error message of the exception
     * @author mbrunnli (19.02.2013)
     */
    public InvalidConfigurationException(File file, String msg) {
        super(file.getAbsolutePath().toString() + ":\n" + msg);
    }

    /**
     * Creates a new {@link InvalidConfigurationException} with the given message
     * @param msg
     *            error message
     * @author mbrunnli (08.04.2014)
     */
    public InvalidConfigurationException(String msg) {
        super(msg);
    }
}

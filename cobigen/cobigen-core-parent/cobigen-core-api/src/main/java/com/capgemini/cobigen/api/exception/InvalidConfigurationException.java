package com.capgemini.cobigen.api.exception;

/**
 * Occurs if the configuration xml could not be parsed successfully
 */
public class InvalidConfigurationException extends CobiGenRuntimeException {

    /**
     * Default serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link InvalidConfigurationException} with the given message
     *
     * @param filePath
     *            file path causing the InvalidConfigurationException or null if not available
     * @param msg
     *            error message of the exception
     * @param t
     *            cause exception
     * @author mbrunnli (25.04.2014)
     */
    public InvalidConfigurationException(String filePath, String msg, Throwable t) {

        super((filePath != null ? filePath + ":\n" : "") + msg, t);
    }

    /**
     * Creates a new {@link InvalidConfigurationException} with the given message
     *
     * @param filePath
     *            file path causing the InvalidConfigurationException or null if not available
     * @param msg
     *            error message of the exception
     * @author mbrunnli (19.02.2013)
     */
    public InvalidConfigurationException(String filePath, String msg) {

        super((filePath != null ? filePath + ":\n" : "") + msg);
    }

    /**
     * Creates a new {@link InvalidConfigurationException} with the given message
     *
     * @param msg
     *            error message
     * @author mbrunnli (08.04.2014)
     */
    public InvalidConfigurationException(String msg) {

        super(msg);
    }
}

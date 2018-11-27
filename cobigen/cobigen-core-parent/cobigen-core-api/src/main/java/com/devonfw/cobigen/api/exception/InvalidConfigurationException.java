package com.devonfw.cobigen.api.exception;

import java.nio.file.Path;

/** Occurs if the configuration could not be parsed successfully */
public class InvalidConfigurationException extends CobiGenRuntimeException {

    /** Default serial version UID */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link InvalidConfigurationException} with the given message
     * @param filePath
     *            file path causing the InvalidConfigurationException or null if not available
     * @param msg
     *            error message of the exception
     * @param t
     *            cause exception
     */
    public InvalidConfigurationException(String filePath, String msg, Throwable t) {
        super((filePath != null ? filePath + ":\n" : "") + msg, t);
    }

    /**
     * Creates a new {@link InvalidConfigurationException} with the given message
     * @param filePath
     *            {@link Path} file path causing the InvalidConfigurationException or null if not available
     * @param msg
     *            error message of the exception
     * @param t
     *            cause exception
     */
    public InvalidConfigurationException(Path filePath, String msg, Throwable t) {
        super((filePath != null ? filePath.toAbsolutePath().toString() + ":\n" : "") + msg, t);
    }

    /**
     * Creates a new {@link InvalidConfigurationException} with the given message
     * @param filePath
     *            file path causing the InvalidConfigurationException or null if not available
     * @param msg
     *            error message of the exception
     */
    public InvalidConfigurationException(String filePath, String msg) {
        super((filePath != null ? filePath + ":\n" : "") + msg);
    }

    /**
     * Creates a new {@link InvalidConfigurationException} with the given message
     * @param filePath
     *            {@link Path} file path causing the InvalidConfigurationException or null if not available
     * @param msg
     *            error message of the exception
     */
    public InvalidConfigurationException(Path filePath, String msg) {
        super((filePath != null ? filePath.toAbsolutePath().toString() + ":\n" : "") + msg);
    }

    /**
     * Creates a new {@link InvalidConfigurationException} with the given message
     * @param msg
     *            error message
     */
    public InvalidConfigurationException(String msg) {
        super(msg);
    }
}

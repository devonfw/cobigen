package com.capgemini.cobigen.impl.exceptions;

import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;

/**
 * Exception indicating a failed backup process.
 */
public class BackupFailedException extends CobiGenRuntimeException {

    /**
     * Default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link BackupFailedException} with the given message
     * @param message
     *            message
     * @author mbrunnli (Jun 24, 2015)
     */
    public BackupFailedException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link BackupFailedException} with the given message and cause.
     * @param message
     *            message
     * @param cause
     *            original cause
     * @author mbrunnli (Jun 24, 2015)
     */
    public BackupFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}

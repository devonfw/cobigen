package com.capgemini.cobigen.exceptions;

import java.io.File;

/**
 * This Exception indicates a problem while merging.
 */
public class MergeException extends RuntimeException {

    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = 1;

    /**
     * Creates an exception for the base file to be merged with the given message.
     * @param baseFile
     *            file to be merged
     * @param msg
     *            error message
     */
    public MergeException(File baseFile, String msg) {
        super("Unable to merge a generated patch into file " + baseFile.toURI() + ": " + msg);
    }

    /**
     * Creates an exception for the base file to be merged with the given message and root cause.
     * @param baseFile
     *            file to be merged
     * @param msg
     *            error message
     * @param cause
     *            root cause
     */
    public MergeException(File baseFile, String msg, Throwable cause) {
        super("Unable to merge a generated patch into file " + baseFile.toURI() + ": " + msg, cause);
    }
}

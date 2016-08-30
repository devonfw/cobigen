package com.capgemini.cobigen.api.extension;

import java.io.File;

/**
 * This interface should be inherited to declare a new component to handle document merges. An {@link Merger}
 * can be registered via an {@link TriggerInterpreter} implementation
 */
public interface Merger {

    /**
     * Returns the type, this merger should handle
     * @return the type (not null)
     */
    public String getType();

    /**
     * Merges the patch into the base file
     * @param base
     *            target {@link File} to be merged into
     * @param patch
     *            {@link String} patch, which should be applied to the base file
     * @param targetCharset
     *            target charset of the file to be read and write
     * @return Merged source code (not null)
     * @throws Exception
     *             if an exception occurs while merging the contents
     */
    public String merge(File base, String patch, String targetCharset) throws Exception;
}

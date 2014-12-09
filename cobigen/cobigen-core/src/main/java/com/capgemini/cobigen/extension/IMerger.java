package com.capgemini.cobigen.extension;

import java.io.File;

/**
 * This interface should be inherited to declare a new component to handle document merges. An {@link IMerger}
 * can be registered via an {@link ITriggerInterpreter} implementation
 * @author mbrunnli (06.04.2014)
 */
public interface IMerger {

    /**
     * Returns the type, this merger should handle
     * @return the type (not null)
     * @author mbrunnli (08.04.2014)
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
     * @author mbrunnli (19.03.2013)
     */
    public String merge(File base, String patch, String targetCharset) throws Exception;
}

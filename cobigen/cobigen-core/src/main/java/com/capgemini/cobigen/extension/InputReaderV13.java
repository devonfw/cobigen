package com.capgemini.cobigen.extension;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Extension interface of the {@link IInputReader} interface. By implementing this interface, you will be able
 * to handle recursive vs. non-recursive input object retrieval.
 * @author mbrunnli (18.01.2015)
 */
public interface InputReaderV13 extends IInputReader {

    /**
     * Will return the set of combined input objects if the given input combines multiple input objects (resp.
     * {@link #combinesMultipleInputObjects(Object)} returns <code>true</code>).
     * @param input
     *            the combined input object
     * @param inputCharset
     *            to be used for reading new inputs
     * @return a list of input objects, the generation should be triggered for each.
     * @author mbrunnli (03.06.2014)
     */
    public List<Object> getInputObjectsRecursively(Object input, Charset inputCharset);

}

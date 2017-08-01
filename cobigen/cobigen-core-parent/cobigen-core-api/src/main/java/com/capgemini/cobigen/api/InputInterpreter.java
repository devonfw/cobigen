package com.capgemini.cobigen.api;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import com.capgemini.cobigen.api.exception.InputReaderException;
import com.capgemini.cobigen.api.extension.InputReader;

/**
 * The implements operations for reading or processing inputs.
 */
public interface InputInterpreter {

    /**
     * Checks whether there is at least one input reader, which interprets the given input as combined input.
     * @param input
     *            object
     * @return <code>true</code> if there is at least one input reader, which interprets the given input as
     *         combined input,<code>false</code>, otherwise
     */
    public boolean combinesMultipleInputs(Object input);

    /**
     * Determines the set of input objects derived from the given container input recursively
     * @param input
     *            the combined input object
     * @param inputCharset
     *            to be used for reading new inputs
     * @return a list of input objects, the generation should be triggered for each.
     */
    public List<Object> getInputObjectsRecursively(Object input, Charset inputCharset);

    /**
     * Determines the set of input objects derived from the given container input
     * @param input
     *            the combined input object
     * @param inputCharset
     *            to be used for reading new inputs
     * @return a list of input objects, the generation should be triggered for each.
     */
    public List<Object> getInputObjects(Object input, Charset inputCharset);

    /**
     * Reads the content at a path via a fitting {@link InputReader} and returns a CobiGen compliant input
     * @param type
     *            of the input to be read. Is used to resolve a fitting InputReader
     * @param path
     *            the {@link Path} to the object. Can also point to a folder
     * @param inputCharset
     *            of the input to be used
     * @param additionalArguments
     *            depending on the InputReader implementation
     * @return Object that is a valid input
     * @throws InputReaderException
     *             if the Path cannot be read
     * @throws IllegalArgumentException
     *             if the provided additional arguments do not suffice the used InputReader
     */
    public abstract Object read(String type, Path path, Charset inputCharset, Object... additionalArguments)
        throws InputReaderException;

}
package com.capgemini.cobigen.api;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import com.capgemini.cobigen.api.exception.InputReaderException;
import com.capgemini.cobigen.api.extension.InputReader;

/** The implements operations for reading or processing inputs. */
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
     * Resolves any input, which serves as a container and returns the list of contained inputs, which are
     * valid for generation.
     * @param input
     *            container to resolve
     * @return the list of matching entries in the container or the input itself if it is not a container.
     */
    public List<Object> resolveContainers(Object input);

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
    public Object read(String type, Path path, Charset inputCharset, Object... additionalArguments)
        throws InputReaderException;

}
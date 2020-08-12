package com.devonfw.cobigen.api;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.extension.InputReader;

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
     * Reads the content at a path via a fitting {@link InputReader} and returns a CobiGen compliant input.
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

    /**
     * Reads the content at a path via the first fitting {@link InputReader} and returns a CobiGen compliant
     * input. Supports at least one EXTERNAL input reader with the same isMostLikelyReadable criteria than an
     * INTERNAL input reader.
     * @param path
     *            the {@link Path} to the object. Can also point to a folder
     * @param inputCharset
     *            of the input to be used
     * @param additionalArguments
     *            depending on the InputReader implementation
     * @return Object that is a valid input or null if the file cannot be read by any InputReader
     * @throws InputReaderException
     *             if the Path cannot be read
     */
    public Object read(Path path, Charset inputCharset, Object... additionalArguments) throws InputReaderException;

    /**
     * Try to determine, whether the input file is readable by the plug-ins parser. If false is returned by
     * the method, it is not assured, that the read method is not called at all. In case of the situation,
     * that no plug-in is most likely to read the input, CobiGen will try every input reader to read the
     * input. Please be aware, that this method should be kept highly performant. It does not have to be an
     * exact result.
     * @param path
     *            the file {@link Path}.
     * @param type
     *            of the input to be read. Is used to resolve a fitting InputReader
     * @return true, if it is most likely, that the file can be read by this external input reader's read
     *         method<br>
     *         null, if it is most likely, that the file can be read by this internal input reader's read
     *         method<br>
     *         false, if the reader is most likely not able to read the file.
     */
    public Boolean isMostLikelyReadable(String type, Path path);

}
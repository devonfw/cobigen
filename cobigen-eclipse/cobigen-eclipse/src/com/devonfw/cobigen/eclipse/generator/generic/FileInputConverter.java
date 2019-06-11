package com.devonfw.cobigen.eclipse.generator.generic;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.exception.PluginNotAvailableException;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorCreationException;
import com.google.common.collect.Lists;

/**
 * Converts IDE selection objects to valid CobiGen inputs
 * @author mbrunnli (06.12.2014)
 */
public class FileInputConverter {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(FileInputConverter.class);

    /**
     * Converts the eclipse internal resources selected to valid xml input types to be used for generation
     * using CobiGen
     * @param cobigen
     *            initialized {@link CobiGen} instance
     * @param resources
     *            {@link List} of eclipse internal resources selected as input
     * @return the converted {@link List} of CobiGen inputs
     * @throws GeneratorCreationException
     *             if any exception occurred during converting the inputs or creating the generator
     */
    public static List<Object> convertInput(CobiGen cobigen, List<Object> resources) throws GeneratorCreationException {
        List<Object> convertedInputs = Lists.newLinkedList();
        String inputType = null;

        for (Object resource : resources) {
            if (resource instanceof IFile) {
                IFile inputFile = (IFile) resource;
                Charset charset;
                try {
                    charset = Charset.forName(inputFile.getCharset());
                } catch (CoreException e) {
                    LOG.warn(
                        "Could not deterime charset for file " + inputFile.getLocationURI() + " reading with UTF-8.");
                    charset = Charset.forName("UTF-8");
                }
                Path inputFilePath = Paths.get(inputFile.getLocationURI());
                String readerType;

                if (cobigen.isMostLikelyReadable("xml", inputFilePath)) {
                    readerType = "xml";
                    readAndAddInput(cobigen, convertedInputs, inputType, readerType, inputFilePath, charset);
                    continue;
                }

                else if (cobigen.isMostLikelyReadable("openapi", inputFilePath)) {
                    readerType = "openapi";
                    readAndAddInput(cobigen, convertedInputs, inputType, readerType, inputFilePath, charset);
                    continue;
                }
                {
                    readerType = "xml";
                    try {
                        readAndAddInput(cobigen, convertedInputs, inputType, readerType, inputFilePath, charset);
                        continue;
                    } catch (InputReaderException e) {
                        LOG.trace("Could not read file {} with input reader of type '{}'", inputFile.getLocationURI(),
                            readerType, e);
                        // try next
                    } catch (PluginNotAvailableException e) {
                        LOG.trace(e.getMessage(), e);
                    }
                }
                // try openapi as last chance as it takes too many resources:
                // https://github.com/swagger-api/swagger-parser/issues/496
                {
                    readerType = "openapi";
                    try {
                        readAndAddInput(cobigen, convertedInputs, inputType, readerType, inputFilePath, charset);
                        continue;
                    } catch (InputReaderException e) {
                        throw new GeneratorCreationException(
                            "Could not read file " + inputFile.getLocationURI() + " with any input reader", e);
                    } catch (PluginNotAvailableException e) {
                        throw new GeneratorCreationException("Could not read file " + inputFile.getLocationURI()
                            + " as no Plug-in for type '" + readerType + "' could be found.", e);
                    }
                }
            } else {
                throw new GeneratorCreationException(
                    "Invalid combination of inputs. Please just select same file types in a mass selection.");
            }
        }
        return convertedInputs;
    }

    /**
     * Reads the input file content and convert it to CobiGen valid input.
     * @param cobigen
     *            initialized {@link CobiGen} instance
     * @param convertedInputs
     *            the converted {@link List} of CobiGen inputs
     * @param inputType
     *            Type of input file
     * @param readerType
     *            Type of the used reader
     * @param inputFilePath
     *            the {@link Path} to the object. Can also point to a folder
     * @param charset
     *            of the input to be used
     * @throws GeneratorCreationException
     *             States that an exception occured during generator instance creation
     */
    private static void readAndAddInput(CobiGen cobigen, List<Object> convertedInputs, String inputType,
        String readerType, Path inputFilePath, Charset charset) throws GeneratorCreationException {
        checkSameType(inputType, readerType);
        convertedInputs.add(cobigen.read(readerType, inputFilePath, charset));
        inputType = readerType;
    }

    /**
     * Checks whether the two given types are equal. Throws an exception if not.
     * @param inputType
     *            Type of input file
     * @param readerType
     *            Type of the used reader
     * @throws GeneratorCreationException
     *             States that an exception occured during generator instance creation
     */
    private static void checkSameType(String inputType, String readerType) throws GeneratorCreationException {
        if (inputType != null && !inputType.equals(readerType)) {
            throw new GeneratorCreationException(
                "Invalid combination of inputs. Please just select files of same language.");
        }
    }
}

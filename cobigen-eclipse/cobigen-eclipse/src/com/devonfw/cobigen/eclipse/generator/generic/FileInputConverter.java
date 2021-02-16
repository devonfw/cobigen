package com.devonfw.cobigen.eclipse.generator.generic;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.exception.PluginNotAvailableException;
import com.devonfw.cobigen.eclipse.common.exceptions.GeneratorCreationException;
import com.google.common.base.Charsets;
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

        for (Object resource : resources) {
            if (resource instanceof IFile) {
                convertedInputs.add(convertFile(cobigen, (IFile) resource));
            } else if (resource instanceof IFolder) {
                IFolder inputFolder = (IFolder) resource;
                Path inputFolderLocationUri = Paths.get(inputFolder.getLocationURI());
                if (inputFolderLocationUri != null) {
                    convertedInputs.add(cobigen.read(inputFolderLocationUri, Charsets.UTF_8));
                } else {
                    throw new GeneratorCreationException("corresponding resource in Filesystem couldn't be found");
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
     * @param inputFile
     *            the file with {@link Path} to the object and further information like charset.
     * @return the output Object corresponding to the inputFile
     * @throws GeneratorCreationException
     *             if the Reader couldn't read the input File or couldn't find the Plugin
     */
    private static Object convertFile(CobiGen cobigen, IFile inputFile) throws GeneratorCreationException {
        Object output = null;
        Charset charset;
        try {
            charset = Charset.forName(inputFile.getCharset());
        } catch (CoreException e) {
            LOG.warn("Could not deterime charset for file " + inputFile.getLocationURI() + " reading with UTF-8.");
            charset = Charset.forName("UTF-8");
        }
        Path inputFilePath = Paths.get(inputFile.getLocationURI());

        try {
            output = cobigen.read(inputFilePath, charset);
        } catch (InputReaderException e) {
            LOG.trace("Could not read file {}", inputFile.getLocationURI(), e);
            throw new GeneratorCreationException(
                "Could not read file " + inputFile.getLocationURI() + " with any input reader", e);
        } catch (PluginNotAvailableException e) {
            LOG.trace(e.getMessage(), e);
            throw new GeneratorCreationException("Could not read file " + inputFile.getLocationURI()
                + " as no Plug-in for the given type could be found.", e);
        }

        return output;
    }
}
package com.capgemini.cobigen.maven.validation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.plugin.MojoFailureException;
import org.xml.sax.SAXException;

import com.capgemini.cobigen.api.InputInterpreter;
import com.capgemini.cobigen.api.exception.InputReaderException;
import com.capgemini.cobigen.maven.utils.XmlUtil;
import com.google.common.base.Charsets;

/**
 * Input pre-processor, which tries to identify valid file inputs for CobiGen
 * @author mbrunnli (16.02.2015)
 */
public class InputPreProcessor {

    /**
     * Processes the given file to be converted into any CobiGen valid input format
     * @param file
     *            {@link File} converted into any CobiGen valid input format
     * @param cl
     *            {@link ClassLoader} to be used, when considering Java-related inputs
     * @param inputInterpreter
     *            parse cobiGen compliant input from the file
     * @throws MojoFailureException
     *             if the input retrieval did not result in a valid CobiGen input
     * @return a CobiGen valid input
     * @author mbrunnli (16.02.2015)
     */
    public static Object process(InputInterpreter inputInterpreter, File file, ClassLoader cl)
        throws MojoFailureException {

        if (!file.exists() || !file.canRead()) {
            throw new MojoFailureException("Could not read input file " + file.getAbsolutePath());
        }

        Object input = null;
        try {
            input = inputInterpreter.read("java", Paths.get(file.toURI()), Charsets.UTF_8, cl);
        } catch (InputReaderException e) {
            // was not a java resource, try something else
        }

        try {
            input = XmlUtil.parseXmlFileToDom(file);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            // was not a XML-Document, try something else
        }
        if (input == null) {
            throw new MojoFailureException("The file " + file.getAbsolutePath() + " is not a valid input for CobiGen.");
        }
        return input;
    }
}

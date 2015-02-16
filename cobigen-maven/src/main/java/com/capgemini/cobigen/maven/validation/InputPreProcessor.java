package com.capgemini.cobigen.maven.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.Charsets;
import org.apache.maven.plugin.MojoFailureException;
import org.xml.sax.SAXException;

import com.capgemini.cobigen.javaplugin.util.JavaParserUtil;
import com.capgemini.cobigen.xmlplugin.util.XmlUtil;
import com.thoughtworks.qdox.parser.ParseException;

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
     * @throws MojoFailureException
     *             if the input retrieval did not result in a valid CobiGen input
     * @return a CobiGen valid input
     * @author mbrunnli (16.02.2015)
     */
    public static Object process(File file, ClassLoader cl) throws MojoFailureException {

        if (!file.exists() || !file.canRead()) {
            throw new MojoFailureException("Could not read input file " + file.getAbsolutePath());
        }

        Object input = null;
        try {
            input =
                JavaParserUtil.getFirstJavaClass(cl, new InputStreamReader(new FileInputStream(file),
                    Charsets.UTF_8));
        } catch (ParseException | FileNotFoundException e) {
            // was not a java resource, try something else
        }

        try {
            input = XmlUtil.parseXmlFileToDom(file);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            // was not a XML-Document, try something else
        }
        if (input == null) {
            throw new MojoFailureException("The file " + file.getAbsolutePath()
                + " is not a valid input for CobiGen.");
        }
        return input;
    }
}

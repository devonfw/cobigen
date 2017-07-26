package com.capgemini.cobigen.eclipse.generator.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorCreationException;
import com.capgemini.cobigen.eclipse.generator.xml.util.XmlUtil;
import com.google.common.collect.Lists;

/**
 * Converts IDE selection objects to valid CobiGen inputs
 * @author mbrunnli (06.12.2014)
 */
public class XmlInputConverter {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(XmlInputConverter.class);

    /**
     * Converts the eclipse internal resources selected to valid xml input types to be used for generation
     * using CobiGen
     * @param resources
     *            {@link List} of eclipse internal resources selected as input
     * @return the converted {@link List} of CobiGen inputs
     * @throws GeneratorCreationException
     *             if any exception occurred during converting the inputs or creating the generator
     * @author mbrunnli (06.12.2014)
     */
    public static List<Object> convertInput(List<Object> resources) throws GeneratorCreationException {
        List<Object> convertedInputs = Lists.newLinkedList();
        for (Object resource : resources) {
            if (resource instanceof IFile) {
                try {
                    InputStream stream = ((IFile) resource).getContents();
                    Document domDocument = XmlUtil.parseXmlStreamToDom(stream);
                    convertedInputs.add(domDocument);
                } catch (SAXException e) {
                    LOG.error("Could not parse file {} as xml document.",
                        ((IFile) resource).getFullPath().toOSString(), e);
                    throw new GeneratorCreationException("Could not parse file "
                        + ((IFile) resource).getFullPath().toOSString() + " as xml document.", e);
                } catch (CoreException e) {
                    LOG.error("An eclipse internal exception occurred.", e);
                    throw new GeneratorCreationException("An eclipse internal exception occurred.", e);
                } catch (IOException e) {
                    LOG.error("Could not read file {}.", ((IFile) resource).getFullPath().toOSString(), e);
                    throw new GeneratorCreationException("Could not read file "
                        + ((IFile) resource).getFullPath().toOSString() + ".", e);
                } catch (ParserConfigurationException e) {
                    LOG.error("An internal xml parser exception occurred.", e);
                    throw new GeneratorCreationException("An internal xml parser exception occurred.", e);
                }
            }
        }
        return convertedInputs;
    }
}

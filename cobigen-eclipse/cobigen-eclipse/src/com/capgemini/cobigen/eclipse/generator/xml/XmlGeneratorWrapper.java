package com.capgemini.cobigen.eclipse.generator.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.capgemini.cobigen.eclipse.common.exceptions.GeneratorProjectNotExistentException;
import com.capgemini.cobigen.eclipse.common.exceptions.InvalidInputException;
import com.capgemini.cobigen.eclipse.generator.CobiGenWrapper;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.xmlplugin.util.XmlUtil;

/**
 * Generator wrapper covering xml documents as inputs
 * @author mbrunnli (06.12.2014)
 */
public class XmlGeneratorWrapper extends CobiGenWrapper {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(XmlGeneratorWrapper.class);

    /**
     * Creates a new {@link XmlGeneratorWrapper} to handle xml documents as input
     * @throws GeneratorProjectNotExistentException
     *             if the generator configuration project does not exist
     * @throws CoreException
     *             if an eclipse internal exception occurred
     * @throws IOException
     *             if the generator project could not be found or read
     * @throws InvalidConfigurationException
     *             if the context configuration is not valid
     * @author mbrunnli (06.12.2014)
     */
    public XmlGeneratorWrapper()
        throws GeneratorProjectNotExistentException, CoreException, InvalidConfigurationException, IOException {
        super();
    }

    @Override
    public void adaptModel(Map<String, Object> model) {
        // nothing to do
    }

    @Override
    public boolean isValidInput(IStructuredSelection selection) throws InvalidInputException {

        Iterator<?> it = selection.iterator();
        List<String> firstTriggers = null;

        boolean uniqueSourceSelected = false;

        while (it.hasNext()) {
            Object tmp = it.next();
            if (tmp instanceof IFile) {
                uniqueSourceSelected = true;
                try (InputStream stream = ((IFile) tmp).getContents()) {
                    LOG.debug("Try parsing file {} as xml...", ((IFile) tmp).getName());
                    Document domDocument = XmlUtil.parseXmlStreamToDom(stream);
                    firstTriggers = cobiGen.getMatchingTriggerIds(domDocument);
                } catch (CoreException e) {
                    throw new InvalidInputException("An eclipse internal exception occured! ", e);
                } catch (IOException e) {
                    throw new InvalidInputException("The file " + ((IFile) tmp).getName() + " could not be read!", e);
                } catch (ParserConfigurationException e) {
                    throw new InvalidInputException("The file " + ((IFile) tmp).getName()
                        + " could not be parsed, because of an internal configuration error!", e);
                } catch (SAXException e) {
                    throw new InvalidInputException("The contents of the file " + ((IFile) tmp).getName()
                        + " could not be detected as an instance of any CobiGen supported input language.");
                }
            } else {
                throw new InvalidInputException(
                    "You selected at least one input, which type is currently not supported as input for generation. "
                        + "Please choose a different one or read the CobiGen documentation for more details.");
            }

            if (uniqueSourceSelected && selection.size() > 1) {
                throw new InvalidInputException("You selected at least one input in a mass-selection,"
                    + " which type is currently not supported for batch processing. "
                    + "Please just select multiple inputs only if batch processing is supported for all inputs.");
            }
        }
        return firstTriggers != null && !firstTriggers.isEmpty();
    }
}

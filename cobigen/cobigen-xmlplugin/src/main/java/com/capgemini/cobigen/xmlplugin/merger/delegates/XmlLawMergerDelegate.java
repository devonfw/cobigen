package com.capgemini.cobigen.xmlplugin.merger.delegates;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.InputSource;

import com.capgemini.cobigen.extension.IMerger;
import com.capgemini.xmllawmerger.ConflictHandlingType;
import com.capgemini.xmllawmerger.XmlLawMerger;
import com.capgemini.xmllawmerger.common.exception.XMLMergeException;
import com.capgemini.xmllawmerger.common.util.JDom2Util;

/**
 * Provides a XmlLawMerger instance with the IMerger interface
 * @author sholzer (Aug 27, 2015)
 */
public class XmlLawMergerDelegate implements IMerger {

    /**
     *
     */
    private ConflictHandlingType conflictHandlingType = ConflictHandlingType.PATCHOVERWRITE;

    /**
     *
     */
    private XmlLawMerger merger;

    /**
     *
     * @param mergeSchemaLocation
     *            path to the folder containing the merge schemas to be used
     * @param conflictHandlingType
     *            the way how conflicts will be handled
     * @author sholzer (Aug 27, 2015)
     */
    public XmlLawMergerDelegate(String mergeSchemaLocation, ConflictHandlingType conflictHandlingType) {
        this.conflictHandlingType = conflictHandlingType;
        merger = new XmlLawMerger(mergeSchemaLocation);
    }

    /**
     * {@inheritDoc}
     * @author sholzer (Aug 27, 2015)
     */
    @Override
    public String getType() {
        return conflictHandlingType.name();
    }

    /**
     * {@inheritDoc}
     * @author sholzer (Aug 27, 2015)
     */
    @Override
    public String merge(File base, String patch, String targetCharset) throws Exception {
        org.jdom2.Document mergeResult = merge(base, patch, targetCharset, conflictHandlingType);
        XMLOutputter outputter = new XMLOutputter();
        Writer stringWriter = new StringWriter();
        outputter.output(mergeResult, stringWriter);
        return stringWriter.toString();
    }

    /**
     * Temporarily for testing purposes.
     * @param doc1
     * @param doc2
     * @param conflictHandling
     * @return
     * @throws XMLMergeException
     * @author sholzer (Aug 28, 2015)
     */
    private Document merge(Document doc1, Document doc2, ConflictHandlingType conflictHandling)
        throws XMLMergeException {
        Element mergeResult = merger.merge(doc1.getRootElement(), doc2.getRootElement(), conflictHandling);
        if (mergeResult.getDocument() == null) {
            Document newRoot = new Document();
            newRoot.setRootElement(mergeResult);
            return newRoot;
        }
        return mergeResult.getDocument();
    }

    /**
     * temporarily for testing purposes
     * @param base
     * @param patch
     * @param charSet
     * @param conflictHandling
     * @return
     * @throws XMLMergeException
     * @author sholzer (Aug 28, 2015)
     */
    public Document merge(File base, String patch, String charSet, ConflictHandlingType conflictHandling)
        throws XMLMergeException {
        if (conflictHandling == null) {
            conflictHandling = conflictHandlingType;
        }
        try {
            SAXBuilder builder = new SAXBuilder(XMLReaders.XSDVALIDATING);
            String baseString = JDom2Util.getInstance().readFile(base.getPath(), charSet);
            Document baseDoc = builder.build(new InputSource(new StringReader(baseString)));
            Document patchDoc = builder.build(new InputSource(new StringReader(patch)));
            return merge(baseDoc, patchDoc, conflictHandling);
        } catch (IOException | JDOMException e) {
            throw new XMLMergeException(e.getMessage());
        }

    }

}

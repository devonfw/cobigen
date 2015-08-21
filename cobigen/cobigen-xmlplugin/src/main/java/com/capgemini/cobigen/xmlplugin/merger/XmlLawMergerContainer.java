package com.capgemini.cobigen.xmlplugin.merger;

import java.io.File;

import com.capgemini.cobigen.extension.IMerger;
import com.capgemini.xmllawmerger.ConflictHandlingType;
import com.capgemini.xmllawmerger.XmlLawMerger;
import com.capgemini.xmllawmerger.common.exception.XMLMergeException;

/**
 * This class is used as a bridge between the IMerger from Cobigen and the XmlLawmerger
 * @author sholzer (Aug 20, 2015)
 */
public class XmlLawMergerContainer implements IMerger {

    /**
     * XmlLawMerger instance
     */
    private XmlLawMerger xmlLawMerger;

    /**
     * The used conflictHandlingType
     */
    private ConflictHandlingType conflictHandlingType;

    /**
     * @param mergeSchemaLocation
     *            the location of the merge schemas to be used
     * @param conflictHandlingType
     *            the enum describing the behaviour in case of conflicts
     * @author sholzer (Aug 20, 2015)
     */
    public XmlLawMergerContainer(String mergeSchemaLocation, ConflictHandlingType conflictHandlingType) {
        xmlLawMerger = new XmlLawMerger(mergeSchemaLocation);
        this.conflictHandlingType = conflictHandlingType;
    }

    /**
     * {@inheritDoc}
     * @author sholzer (Aug 20, 2015)
     */
    @Override
    public String getType() {
        return conflictHandlingType.toString();
    }

    /**
     * {@inheritDoc}
     * @author sholzer (Aug 20, 2015)
     */
    @Override
    public String merge(File base, String patch, String targetCharset) throws XMLMergeException {

        return xmlLawMerger.mergeInString(base, patch, targetCharset, conflictHandlingType);
    }
}

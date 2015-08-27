package com.capgemini.cobigen.xmlplugin.merger.delegates;

import java.io.File;

import com.capgemini.cobigen.extension.IMerger;
import com.capgemini.xmllawmerger.ConflictHandlingType;
import com.capgemini.xmllawmerger.XmlLawMerger;

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
        return merger.mergeInString(base, patch, targetCharset, conflictHandlingType);
    }

}

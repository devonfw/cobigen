package com.capgemini.cobigen.xmlplugin.merger.delegates;

import java.io.File;
import java.nio.file.Path;

import com.capgemini.cobigen.extension.IMerger;
import com.github.maybeec.lexeme.LeXeMerger;

/**
 * Provides a XmlLawMerger instance with the IMerger interface
 * @author sholzer (Aug 27, 2015)
 */
public class XmlMergerDelegate implements IMerger {

    /**
     *
     */
    private MergeType mergeType = MergeType.PATCHOVERWRITE;

    /**
     *
     */
    private LeXeMerger merger;

    /**
     *
     * @param mergeSchemaLocation
     *            path to the folder containing the merge schemas to be used
     * @param mergeType
     *            the way how conflicts will be handled
     * @author sholzer (Aug 27, 2015)
     */
    public XmlMergerDelegate(String mergeSchemaLocation, MergeType mergeType) {
        this.mergeType = mergeType;
        merger = new LeXeMerger(mergeSchemaLocation);
    }

    /**
     *
     * @param mergeSchemaLocation
     *            path to the folder containing the merge schemas to be used
     * @param mergeType
     *            the way how conflicts will be handled
     * @author sholzer (Aug 27, 2015)
     */
    public XmlMergerDelegate(Path mergeSchemaLocation, MergeType mergeType) {
        this.mergeType = mergeType;
        merger = new LeXeMerger(mergeSchemaLocation);
    }

    /**
     * {@inheritDoc}
     * @author sholzer (Aug 27, 2015)
     */
    @Override
    public String getType() {
        return mergeType.value;
    }

    /**
     * {@inheritDoc}
     * @author sholzer (Aug 27, 2015)
     */
    @Override
    public String merge(File base, String patch, String targetCharset) throws Exception {
        return merger.mergeInString(base, patch, targetCharset, mergeType.type);
    }

    /**
     * Sets the validation flag
     * @param validation
     *            true if a validation is desired. false otherwise. Default is true
     * @author sholzer (Sep 1, 2015)
     */
    public void setValidation(boolean validation) {
        merger.setValidation(validation);
    }

}

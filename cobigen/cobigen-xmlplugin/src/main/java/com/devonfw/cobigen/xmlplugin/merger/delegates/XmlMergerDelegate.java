package com.devonfw.cobigen.xmlplugin.merger.delegates;

import java.io.File;
import java.nio.file.Path;

import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.extension.Merger;
import com.github.maybeec.lexeme.LeXeMerger;
import com.github.maybeec.lexeme.common.exception.XMLMergeException;

/**
 * Provides a XmlLawMerger instance with the {@link Merger} interface
 */
public class XmlMergerDelegate implements Merger {

    /** Merger type of this instance. */

    private MergeType mergeType = MergeType.PATCHOVERWRITE;

    /** {@link LeXeMerger} instance to be used. */
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

    @Override
    public String getType() {
        return mergeType.value;
    }

    @Override
    public String merge(File base, String patch, String targetCharset) throws MergeException {
        try {
            return merger.mergeInString(base, patch, targetCharset, mergeType.type);
        } catch (XMLMergeException e) {
            throw new MergeException(base, "Error during merge processing by LeXeMe.", e);
        }
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

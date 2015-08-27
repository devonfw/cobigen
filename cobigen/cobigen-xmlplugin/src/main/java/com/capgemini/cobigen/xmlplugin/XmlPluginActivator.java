package com.capgemini.cobigen.xmlplugin;

import java.util.List;

import com.capgemini.cobigen.extension.IGeneratorPluginActivator;
import com.capgemini.cobigen.extension.IMerger;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.xmlplugin.merger.delegates.XmlLawMergerDelegate;
import com.capgemini.xmllawmerger.ConflictHandlingType;
import com.google.common.collect.Lists;

/**
 *
 * @author mbrunnli (06.04.2014)
 */
public class XmlPluginActivator implements IGeneratorPluginActivator {

    /**
     * defining the default location of the merge schemas
     */
    static private String defaultMergeSchemaLocation = "src/main/resources/mergeSchemas";

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014), edited by sholzer (20.08.15)
     *
     */
    @Override
    public List<IMerger> bindMerger() {
        List<IMerger> merger = Lists.newLinkedList();
        /*
         * merger.add(new XmlMerger("xmlmerge", new CompleteMergeAction())); merger.add(new
         * XmlMerger("xmlmerge_override", new OverrideMergeAction()));
         */
        merger.add(new XmlLawMergerDelegate(defaultMergeSchemaLocation, ConflictHandlingType.BASEOVERWRITE)); // Corresponds
                                                                                                              // with
                                                                                                              // the
                                                                                                              // old
                                                                                                              // CompleteMergeAction
        merger.add(new XmlLawMergerDelegate(defaultMergeSchemaLocation,
            ConflictHandlingType.BASEATTACHOROVERWRITE));
        merger.add(new XmlLawMergerDelegate(defaultMergeSchemaLocation, ConflictHandlingType.PATCHOVERWRITE)); // Corresponds
                                                                                                               // with
                                                                                                               // the
                                                                                                               // old
                                                                                                               // OverrideMergeAction
        merger.add(new XmlLawMergerDelegate(defaultMergeSchemaLocation,
            ConflictHandlingType.PATCHATTACHOROVERWRITE));
        return merger;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public List<ITriggerInterpreter> bindTriggerInterpreter() {
        return Lists.<ITriggerInterpreter> newArrayList(new XmlTriggerInterpreter("xml"));
    }

}

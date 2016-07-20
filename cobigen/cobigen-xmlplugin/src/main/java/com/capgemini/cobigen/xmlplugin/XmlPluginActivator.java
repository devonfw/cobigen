package com.capgemini.cobigen.xmlplugin;

import java.util.List;

import com.capgemini.cobigen.extension.IGeneratorPluginActivator;
import com.capgemini.cobigen.extension.IMerger;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.xmlplugin.merger.delegates.MergeType;
import com.capgemini.cobigen.xmlplugin.merger.delegates.XmlMergerDelegate;
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

    @Override
    public List<IMerger> bindMerger() {
        List<IMerger> merger = Lists.newLinkedList();

        merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.BASEOVERWRITE));
        merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.BASEATTACHOROVERWRITE));
        merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.PATCHOVERWRITE));
        merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.PATCHATTACHOROVERWRITE));
        return merger;
    }

    @Override
    public List<ITriggerInterpreter> bindTriggerInterpreter() {
        return Lists.<ITriggerInterpreter> newArrayList(new XmlTriggerInterpreter("xml"));
    }

}

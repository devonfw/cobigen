package com.devonfw.cobigen.xmlplugin;

import java.util.List;

import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.xmlplugin.merger.delegates.MergeType;
import com.devonfw.cobigen.xmlplugin.merger.delegates.XmlMergerDelegate;
import com.google.common.collect.Lists;

/**
 * Plug-in activator to be detected by CobiGen's service loader lookup.
 */
public class XmlPluginActivator implements GeneratorPluginActivator {

    /**
     * defining the default location of the merge schemas
     */
    static private String defaultMergeSchemaLocation = "src/main/resources/mergeSchemas";

    @Override
    public List<Merger> bindMerger() {
        List<Merger> merger = Lists.newLinkedList();

        merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.BASEOVERWRITE));
        merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.BASEATTACHOROVERWRITE));
        merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.PATCHOVERWRITE));
        merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.PATCHATTACHOROVERWRITE));
        return merger;
    }

    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return Lists.<TriggerInterpreter> newArrayList(new XmlTriggerInterpreter("xml"));
    }

}

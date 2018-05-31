package com.capgemini.cobigen.propertyplugin;

import java.util.List;

import com.capgemini.cobigen.api.extension.GeneratorPluginActivator;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.google.common.collect.Lists;

/**
 * Plug-in activator to be registered in CobiGen's PluginRegistry
 */
public class PropertyMergerPluginActivator implements GeneratorPluginActivator {

    @Override
    public List<Merger> bindMerger() {
        List<Merger> merger = Lists.newLinkedList();
        merger.add(new PropertyMerger("propertymerge", false));
        merger.add(new PropertyMerger("propertymerge_override", true));
        return merger;
    }

    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return null;
    }

}

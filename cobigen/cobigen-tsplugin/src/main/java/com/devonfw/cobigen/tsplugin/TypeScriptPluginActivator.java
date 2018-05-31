package com.capgemini.cobigen.tsplugin;

import java.util.List;

import com.capgemini.cobigen.api.extension.GeneratorPluginActivator;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.tsplugin.merger.TypeScriptMerger;
import com.google.common.collect.Lists;

/**
 * TypeScript Plug-in Activator to be registered in the PluginRegistry of CobiGen.
 */
public class TypeScriptPluginActivator implements GeneratorPluginActivator {

    @Override
    public List<Merger> bindMerger() {
        List<Merger> merger = Lists.newLinkedList();
        merger.add(new TypeScriptMerger("tsmerge", false));
        merger.add(new TypeScriptMerger("tsmerge_override", true));
        return merger;
    }

    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return null;
    }

}

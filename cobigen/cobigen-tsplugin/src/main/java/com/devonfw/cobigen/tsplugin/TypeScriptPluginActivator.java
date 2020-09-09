package com.devonfw.cobigen.tsplugin;

import java.util.List;

import com.devonfw.cobigen.api.annotation.Activation;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.tsplugin.inputreader.TypeScriptInputReader;
import com.devonfw.cobigen.tsplugin.merger.TypeScriptMerger;
import com.google.common.collect.Lists;

/**
 * TypeScript Plug-in Activator to be registered in the PluginRegistry of CobiGen.
 */
@Activation(byFileExtension = { TypeScriptInputReader.VALID_EXTENSION },
    byMergeStrategy = { TypeScriptPluginActivator.TSMERGE, TypeScriptPluginActivator.TSMERGE_OVERRIDE })
public class TypeScriptPluginActivator implements GeneratorPluginActivator {

    /** Merger type for typescript files (prefer patch) */
    static final String TSMERGE_OVERRIDE = "tsmerge_override";

    /** Merger type for typescript files (prefer base) */
    static final String TSMERGE = "tsmerge";

    /** Defines the trigger type */
    private static final String TRIGGER_TYPE = "typescript";

    @Override
    public List<Merger> bindMerger() {
        List<Merger> merger = Lists.newLinkedList();
        merger.add(new TypeScriptMerger(TSMERGE, false));
        merger.add(new TypeScriptMerger(TSMERGE_OVERRIDE, true));
        return merger;
    }

    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return Lists.<TriggerInterpreter> newArrayList(new TypeScriptTriggerInterpreter(TRIGGER_TYPE));
    }

}

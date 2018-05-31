package com.capgemini.cobigen.senchaplugin;

import java.util.List;

import com.capgemini.cobigen.api.extension.GeneratorPluginActivator;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.senchaplugin.merger.SenchaMerger;
import com.google.common.collect.Lists;

/**
 * Sencha Plug-in Activator to be registered in the PluginRegistry of CobiGen.
 */
public class SenchaPluginActivator implements GeneratorPluginActivator {

    @Override
    public List<Merger> bindMerger() {
        List<Merger> merger = Lists.newLinkedList();
        merger.add(new SenchaMerger("senchamerge", false));
        merger.add(new SenchaMerger("senchamerge_override", true));
        return merger;
    }

    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return null;
    }

}

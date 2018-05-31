package com.capgemini.cobigen.javaplugin;

import java.util.List;

import com.capgemini.cobigen.api.extension.GeneratorPluginActivator;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.javaplugin.merger.JavaMerger;
import com.google.common.collect.Lists;

/** Plug-in activator to be registered to the PluginRegistry of CobiGen by any client. */
public class JavaPluginActivator implements GeneratorPluginActivator {

    @Override
    public List<Merger> bindMerger() {
        List<Merger> merger = Lists.newLinkedList();
        merger.add(new JavaMerger("javamerge", false));
        merger.add(new JavaMerger("javamerge_override", true));
        return merger;
    }

    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return Lists.<TriggerInterpreter> newArrayList(new JavaTriggerInterpreter("java"));
    }

}

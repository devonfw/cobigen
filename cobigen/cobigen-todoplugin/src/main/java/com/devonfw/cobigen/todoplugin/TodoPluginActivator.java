package com.devonfw.cobigen.todoplugin;

import java.util.List;

import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.todoplugin.merger.TodoMerger;
import com.google.common.collect.Lists;

/**
 * Todo Plug-in Activator to be registered in the PluginRegistry of CobiGen.
 */
public class TodoPluginActivator implements GeneratorPluginActivator {
    
    /**
     * Defines the trigger type
     */
    private static final String TRIGGER_TYPE = "todo";

    @Override
    public List<Merger> bindMerger() {
        List<Merger> merger = Lists.newLinkedList();
        merger.add(new TodoMerger("todomerge", false));
        merger.add(new TodoMerger("todomerge_override", true));
        return merger;
    }

    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return Lists.<TriggerInterpreter> newArrayList(new TodoTriggerInterpreter(TRIGGER_TYPE));
    }

}

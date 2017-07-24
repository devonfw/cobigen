package com.capgemini.cobigen.openapiplugin;

import java.util.List;

import com.capgemini.cobigen.api.extension.GeneratorPluginActivator;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.impl.PluginRegistry;
import com.google.common.collect.Lists;

/**
 * Plug-in activator to be registered to the {@link PluginRegistry} of CobiGen by any client.
 */
public class SwaggerPluginActivator implements GeneratorPluginActivator {

    @Override
    public List<Merger> bindMerger() {
        return null;
    }

    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return Lists.<TriggerInterpreter> newArrayList(new SwaggerTriggerInterpreter("swagger"));
    }

}

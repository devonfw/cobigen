package com.devonfw.cobigen.openapiplugin;

import java.util.List;

import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.google.common.collect.Lists;

/**
 * Plug-in activator to be registered at CobiGen.
 */
public class OpenAPIPluginActivator implements GeneratorPluginActivator {

    /**
     * Defines the trigger type
     */
    private static final String TRIGGER_TYPE = "openapi";

    @Override
    public List<Merger> bindMerger() {
        return null;
    }

    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return Lists.<TriggerInterpreter> newArrayList(new OpenAPITriggerInterpreter(TRIGGER_TYPE));
    }

}

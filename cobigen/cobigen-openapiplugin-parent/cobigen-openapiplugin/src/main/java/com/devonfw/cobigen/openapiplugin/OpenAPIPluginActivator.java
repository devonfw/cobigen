package com.devonfw.cobigen.openapiplugin;

import java.util.List;

import com.devonfw.cobigen.api.annotation.Activation;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.openapiplugin.inputreader.OpenAPIInputReader;
import com.google.common.collect.Lists;

/**
 * Plug-in activator to be registered at CobiGen.
 */
@Activation(byFileExtension = { OpenAPIInputReader.VALID_EXTENSION_YAML, OpenAPIInputReader.VALID_EXTENSION_YML })
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

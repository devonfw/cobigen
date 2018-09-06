package com.devonfw.cobigen.htmlplugin;

import java.util.List;

import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.htmlplugin.merger.AngularMerger;
import com.google.common.collect.Lists;

/**
 * JSON Plug-in Activator to be registered in the PluginRegistry of CobiGen.
 */
public class HTMLPluginActivator implements GeneratorPluginActivator {

    @Override
    public List<Merger> bindMerger() {
        List<Merger> merger = Lists.newLinkedList();
        merger.add(new AngularMerger("html-ng*", false));
        merger.add(new AngularMerger("html-ng*_override", true));
        return merger;
    }

    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return null;
    }

}

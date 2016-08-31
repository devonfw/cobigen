package com.capgemini.cobigen.senchaplugin;

import java.util.List;

import com.capgemini.cobigen.api.extension.GeneratorPluginActivator;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.senchaplugin.merger.JSMerger;
import com.google.common.collect.Lists;

/**
 *
 * @author mbrunnli (06.04.2014)
 */
public class JSPluginActivator implements GeneratorPluginActivator {

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public List<Merger> bindMerger() {
        List<Merger> merger = Lists.newLinkedList();
        merger.add(new JSMerger("jsmerge", false));
        merger.add(new JSMerger("jsmerge_override", true));
        return merger;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return Lists.<TriggerInterpreter> newArrayList(new JSTriggerInterpreter("js"));
    }

}

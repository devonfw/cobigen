package com.capgemini.cobigen.senchaplugin;

import java.util.List;

import com.capgemini.cobigen.api.extension.GeneratorPluginActivator;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.senchaplugin.merger.JSMerger;
import com.google.common.collect.Lists;

/**
 *
 * @author rudiazma (26 de jul. de 2016)
 */
public class JSPluginActivator implements GeneratorPluginActivator {

    /**
     * {@inheritDoc}
     * @author rudiazma (26 de jul. de 2016)
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
     * @author rudiazma (26 de jul. de 2016)
     */
    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return Lists.<TriggerInterpreter> newArrayList(new JSTriggerInterpreter("js"));
    }

}

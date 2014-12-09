package com.capgemini.cobigen.xmlplugin;

import java.util.List;

import com.capgemini.cobigen.extension.IGeneratorPluginActivator;
import com.capgemini.cobigen.extension.IMerger;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.xmlplugin.merger.XmlMerger;
import com.capgemini.cobigen.xmlplugin.merger.action.CompleteMergeAction;
import com.capgemini.cobigen.xmlplugin.merger.action.OverrideMergeAction;
import com.google.common.collect.Lists;

/**
 *
 * @author mbrunnli (06.04.2014)
 */
public class XmlPluginActivator implements IGeneratorPluginActivator {

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public List<IMerger> bindMerger() {
        List<IMerger> merger = Lists.newLinkedList();
        merger.add(new XmlMerger("xmlmerge", new CompleteMergeAction()));
        merger.add(new XmlMerger("xmlmerge_override", new OverrideMergeAction()));
        return merger;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public List<ITriggerInterpreter> bindTriggerInterpreter() {
        return Lists.<ITriggerInterpreter> newArrayList(new XmlTriggerInterpreter("xml"));
    }

}

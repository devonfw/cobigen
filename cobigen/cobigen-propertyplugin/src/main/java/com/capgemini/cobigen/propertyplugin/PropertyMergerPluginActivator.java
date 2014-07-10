/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.propertyplugin;

import java.util.List;

import com.capgemini.cobigen.extension.IGeneratorPluginActivator;
import com.capgemini.cobigen.extension.IMerger;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.google.common.collect.Lists;

/**
 * 
 * @author mbrunnli (06.04.2014)
 */
public class PropertyMergerPluginActivator implements IGeneratorPluginActivator {

    /**
     * {@inheritDoc}
     * 
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public List<IMerger> bindMerger() {
        List<IMerger> merger = Lists.newLinkedList();
        merger.add(new PropertyMerger("propertymerge", false));
        merger.add(new PropertyMerger("propertymerge_override", true));
        return merger;
    }

    /**
     * {@inheritDoc}
     * 
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public List<ITriggerInterpreter> bindTriggerInterpreter() {
        return null;
    }

}

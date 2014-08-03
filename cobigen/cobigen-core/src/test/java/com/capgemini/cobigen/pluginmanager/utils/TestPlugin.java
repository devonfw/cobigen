package com.capgemini.cobigen.pluginmanager.utils;

import java.util.List;

import com.capgemini.cobigen.extension.IGeneratorPluginActivator;
import com.capgemini.cobigen.extension.IMerger;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.google.common.collect.Lists;

public class TestPlugin implements IGeneratorPluginActivator {

    @Override
    public List<IMerger> bindMerger() {

        return Lists.<IMerger> newArrayList(new TestMerger());
    }

    @Override
    public List<ITriggerInterpreter> bindTriggerInterpreter() {

        return null;
    }

}

package com.capgemini.cobigen.unittest.pluginmanager.utils;

import java.util.List;

import com.capgemini.cobigen.api.extension.GeneratorPluginActivator;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.google.common.collect.Lists;

public class TestPlugin implements GeneratorPluginActivator {

    @Override
    public List<Merger> bindMerger() {

        return Lists.<Merger> newArrayList(new TestMerger());
    }

    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {

        return null;
    }

}

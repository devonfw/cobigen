package com.capgemini.cobigen.unittest.pluginmanager.utils;

import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.extension.IMatcher;
import com.capgemini.cobigen.extension.ITriggerInterpreter;

public class TestTriggerInterpreter implements ITriggerInterpreter {

    @Override
    public IInputReader getInputReader() {

        return null;
    }

    @Override
    public IMatcher getMatcher() {

        return null;
    }

    @Override
    public String getType() {

        return "TestTriggerInterpreter";
    }
}

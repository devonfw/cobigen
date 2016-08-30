package com.capgemini.cobigen.unittest.pluginmanager.utils;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;

public class TestTriggerInterpreter implements TriggerInterpreter {

    @Override
    public InputReader getInputReader() {

        return null;
    }

    @Override
    public MatcherInterpreter getMatcher() {

        return null;
    }

    @Override
    public String getType() {

        return "TestTriggerInterpreter";
    }
}

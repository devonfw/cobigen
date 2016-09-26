package com.capgemini.cobigen.jsonplugin;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.jsonplugin.inputreader.JSONInputReader;
import com.capgemini.cobigen.jsonplugin.matcher.JSONMatcher;

/**
 *
 * @author rudiazma (Sep 22, 2016)
 */
public class JSONTriggerInterpreter implements TriggerInterpreter {

    private String type;

    /**
     * @param string
     * @author rudiazma (Sep 22, 2016)
     */
    public JSONTriggerInterpreter(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public InputReader getInputReader() {
        return new JSONInputReader();
    }

    @Override
    public MatcherInterpreter getMatcher() {
        return new JSONMatcher();
    }

}

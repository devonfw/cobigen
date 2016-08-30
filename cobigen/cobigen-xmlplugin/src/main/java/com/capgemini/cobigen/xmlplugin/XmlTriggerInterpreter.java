package com.capgemini.cobigen.xmlplugin;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.xmlplugin.inputreader.XmlInputReader;
import com.capgemini.cobigen.xmlplugin.matcher.XmlMatcher;

/**
 * {@link TriggerInterpreter} implementation of a Xml Interpreter
 */
public class XmlTriggerInterpreter implements TriggerInterpreter {

    /**
     * {@link TriggerInterpreter} type to be registered
     */
    public String type;

    /**
     * creates a new {@link XmlTriggerInterpreter}
     *
     * @param type
     *            to be registered
     * @author fkreis (18.11.2014)
     */
    public XmlTriggerInterpreter(String type) {
        super();
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public InputReader getInputReader() {
        return new XmlInputReader();
    }

    @Override
    public MatcherInterpreter getMatcher() {
        return new XmlMatcher();
    }

}

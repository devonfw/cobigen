package com.capgemini.cobigen.xmlplugin;

import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.extension.IMatcher;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.xmlplugin.inputreader.XmlInputReader;
import com.capgemini.cobigen.xmlplugin.matcher.XmlMatcher;

/**
 * {@link ITriggerInterpreter} implementation of a Xml Interpreter
 *
 * @author fkreis (18.11.2014)
 */
public class XmlTriggerInterpreter implements ITriggerInterpreter {

    /**
     * {@link ITriggerInterpreter} type to be registered
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
    public IInputReader getInputReader() {
        return new XmlInputReader();
    }

    @Override
    public IMatcher getMatcher() {
        return new XmlMatcher();
    }

}

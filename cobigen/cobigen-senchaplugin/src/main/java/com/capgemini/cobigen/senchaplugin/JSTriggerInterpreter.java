package com.capgemini.cobigen.senchaplugin;

import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.extension.IMatcher;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.senchaplugin.inputreader.JSInputReader;
import com.capgemini.cobigen.senchaplugin.matcher.JSMatcher;

/**
 * {@link ITriggerInterpreter} implementation of a Java Interpreter
 * @author mbrunnli (08.04.2014)
 */
public class JSTriggerInterpreter implements ITriggerInterpreter {

    /**
     * {@link ITriggerInterpreter} type to be registered
     */
    public String type;

    /**
     * Creates a new Java Interpreter
     * @param type
     *            to be registered
     * @author mbrunnli (08.04.2014)
     */
    public JSTriggerInterpreter(String type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public IInputReader getInputReader() {
        return new JSInputReader();
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public IMatcher getMatcher() {
        return new JSMatcher();
    }
}

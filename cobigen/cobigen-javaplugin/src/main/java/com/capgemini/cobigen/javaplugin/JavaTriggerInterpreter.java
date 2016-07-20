package com.capgemini.cobigen.javaplugin;

import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.extension.IMatcher;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.javaplugin.inputreader.JavaInputReader;
import com.capgemini.cobigen.javaplugin.matcher.JavaMatcher;

/**
 * {@link ITriggerInterpreter} implementation of a Java Interpreter
 * @author mbrunnli (08.04.2014)
 */
public class JavaTriggerInterpreter implements ITriggerInterpreter {

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
    public JavaTriggerInterpreter(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public IInputReader getInputReader() {
        return new JavaInputReader();
    }

    @Override
    public IMatcher getMatcher() {
        return new JavaMatcher();
    }
}

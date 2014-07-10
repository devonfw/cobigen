/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
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
        return new JavaInputReader();
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public IMatcher getMatcher() {
        return new JavaMatcher();
    }
}

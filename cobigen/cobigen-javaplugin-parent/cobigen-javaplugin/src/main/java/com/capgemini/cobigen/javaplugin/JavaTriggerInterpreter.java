package com.capgemini.cobigen.javaplugin;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.javaplugin.inputreader.JavaInputReader;
import com.capgemini.cobigen.javaplugin.matcher.JavaMatcher;

/** {@link TriggerInterpreter} implementation of a Java Interpreter */
public class JavaTriggerInterpreter implements TriggerInterpreter {

    /** {@link TriggerInterpreter} type to be registered */
    public String type;

    /**
     * Creates a new Java Interpreter
     * @param type
     *            to be registered
     */
    public JavaTriggerInterpreter(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public InputReader getInputReader() {
        return new JavaInputReader();
    }

    @Override
    public MatcherInterpreter getMatcher() {
        return new JavaMatcher();
    }
}

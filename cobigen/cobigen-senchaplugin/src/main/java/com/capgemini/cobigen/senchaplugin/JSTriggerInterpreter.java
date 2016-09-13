package com.capgemini.cobigen.senchaplugin;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.senchaplugin.inputreader.JSInputReader;
import com.capgemini.cobigen.senchaplugin.matcher.JSMatcher;

/**
 * {@link TriggerInterpreter} implementation of a Java Interpreter
 * @author rudiazma (28 de jul. de 2016)
 */
public class JSTriggerInterpreter implements TriggerInterpreter {

    /**
     * {@link TriggerInterpreter} type to be registered
     */
    public String type;

    /**
     * Creates a new Java Interpreter
     * @param type
     *            to be registered
     * @author rudiazma (26 de jul. de 2016)
     */
    public JSTriggerInterpreter(String type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     * @author rudiazma (28 de jul. de 2016)
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     * @author rudiazma (28 de jul. de 2016)
     */
    @Override
    public InputReader getInputReader() {
        return new JSInputReader();
    }

    /**
     * {@inheritDoc}
     * @author rudiazma (28 de jul. de 2016)
     */
    @Override
    public MatcherInterpreter getMatcher() {
        return new JSMatcher();
    }
}

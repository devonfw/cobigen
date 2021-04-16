package com.devonfw.cobigen.tsplugin;

import com.devonfw.cobigen.api.annotation.ReaderPriority;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.extension.MatcherInterpreter;
import com.devonfw.cobigen.api.extension.Priority;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.tsplugin.inputreader.TypeScriptInputReader;
import com.devonfw.cobigen.tsplugin.matcher.TypeScriptMatcher;

/**
 * {@link TriggerInterpreter} implementation of a Swagger Interpreter
 */
@ReaderPriority(Priority.LOW)
public class TypeScriptTriggerInterpreter implements TriggerInterpreter {

    /**
     * {@link TriggerInterpreter} type to be registered
     */
    public String type;

    /**
     * Creates a new Swagger Interpreter
     * @param type
     *            to be registered
     */
    public TypeScriptTriggerInterpreter(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public InputReader getInputReader() {
        return new TypeScriptInputReader();
    }

    @Override
    public MatcherInterpreter getMatcher() {
        return new TypeScriptMatcher();
    }

}

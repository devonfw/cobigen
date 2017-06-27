package com.capgemini.cobigen.swaggerplugin;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.swaggerplugin.inputreader.SwaggerInputReader;
import com.capgemini.cobigen.swaggerplugin.matcher.SwaggerMatcher;

/**
 * {@link TriggerInterpreter} implementation of a Swagger Interpreter
 */
public class SwaggerTriggerInterpreter implements TriggerInterpreter {

    /**
     * {@link TriggerInterpreter} type to be registered
     */
    public String type;

    /**
     * Creates a new Swagger Interpreter
     * @param type
     *            to be registered
     */
    public SwaggerTriggerInterpreter(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public InputReader getInputReader() {
        return new SwaggerInputReader();
    }

    @Override
    public MatcherInterpreter getMatcher() {
        return new SwaggerMatcher();
    }

}

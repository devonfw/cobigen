package com.capgemini.cobigen.openapiplugin;

import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.openapiplugin.inputreader.OpenAPIInputReader;
import com.capgemini.cobigen.openapiplugin.matcher.OpenAPIMatcher;

/**
 * {@link TriggerInterpreter} implementation of a Swagger Interpreter
 */
public class OpenAPITriggerInterpreter implements TriggerInterpreter {

    /**
     * {@link TriggerInterpreter} type to be registered
     */
    public String type;

    /**
     * Creates a new Swagger Interpreter
     * @param type
     *            to be registered
     */
    public OpenAPITriggerInterpreter(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public InputReader getInputReader() {
        return new OpenAPIInputReader();
    }

    @Override
    public MatcherInterpreter getMatcher() {
        return new OpenAPIMatcher();
    }

}

package com.devonfw.cobigen.openapiplugin;

import com.devonfw.cobigen.api.annotation.ReaderPriority;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.extension.MatcherInterpreter;
import com.devonfw.cobigen.api.extension.Priority;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.openapiplugin.inputreader.OpenAPIInputReader;
import com.devonfw.cobigen.openapiplugin.matcher.OpenAPIMatcher;

/**
 * {@link TriggerInterpreter} implementation of a Swagger Interpreter
 */
@ReaderPriority(Priority.LOW)
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

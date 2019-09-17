package com.devonfw.cobigen.todoplugin;

import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.extension.MatcherInterpreter;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.todoplugin.inputreader.TodoInputReader;
import com.devonfw.cobigen.todoplugin.matcher.TodoMatcher;

/**
 * {@link TriggerInterpreter} implementation of a Swagger Interpreter
 */
public class TodoTriggerInterpreter implements TriggerInterpreter {

    /**
     * {@link TriggerInterpreter} type to be registered
     */
    public String type;

    /**
     * Creates a new Swagger Interpreter
     * @param type
     *            to be registered
     */
    public TodoTriggerInterpreter(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public InputReader getInputReader() {
        return new TodoInputReader();
    }

    @Override
    public MatcherInterpreter getMatcher() {
        return new TodoMatcher();
    }

}

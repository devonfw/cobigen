package com.capgemini.cobigen.impl.generator;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import com.capgemini.cobigen.api.ConfigurationInterpreter;
import com.capgemini.cobigen.api.InputInterpreter;
import com.capgemini.cobigen.api.annotation.Cached;
import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;
import com.capgemini.cobigen.api.exception.InputReaderException;
import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.extension.PluginRegistry;

/**
 * Implementation of the CobiGen API for input processing
 */
public class InputInterpreterImpl implements InputInterpreter {

    /** Configuration interpreter instance */
    private ConfigurationInterpreterImpl configurationInterpreter;

    /**
     * Creates a new instance of the {@link InputInterpreterImpl} with the given
     * {@link ConfigurationInterpreter} for input matching capabilities.
     * @param configurationInterpreter
     *            {@link ConfigurationInterpreter}
     */
    public InputInterpreterImpl(ConfigurationInterpreterImpl configurationInterpreter) {
        this.configurationInterpreter = configurationInterpreter;
    }

    @Cached
    @Override
    public boolean combinesMultipleInputs(Object input) {
        List<Trigger> matchingTriggers = configurationInterpreter.getMatchingTriggers(input);
        for (Trigger trigger : matchingTriggers) {
            TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
            return triggerInterpreter.getInputReader().combinesMultipleInputObjects(input);
        }
        return false;
    }

    @Cached
    @Override
    public List<Object> getInputObjectsRecursively(Object input, Charset inputCharset) {
        List<Trigger> matchingTriggers = configurationInterpreter.getMatchingTriggers(input);
        for (Trigger trigger : matchingTriggers) {
            TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
            return triggerInterpreter.getInputReader().getInputObjectsRecursively(input, inputCharset);
        }
        throw new CobiGenRuntimeException("No trigger found matching the input.");
    }

    @Cached
    @Override
    public List<Object> getInputObjects(Object input, Charset inputCharset) {
        List<Trigger> matchingTriggers = configurationInterpreter.getMatchingTriggers(input);
        for (Trigger trigger : matchingTriggers) {
            TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
            return triggerInterpreter.getInputReader().getInputObjects(input, inputCharset);
        }
        throw new CobiGenRuntimeException("No trigger found matching the input.");
    }

    // not cached by intention
    @Override
    public Object read(String type, Path path, Charset inputCharset, Object... additionalArguments)
        throws InputReaderException {
        return getInputReader(type).read(path, inputCharset, additionalArguments);
    }

    /**
     * @param type
     *            of the input
     * @return InputReader for the given type.
     * @throws CobiGenRuntimeException
     *             if no InputReadercould be found
     */
    private InputReader getInputReader(String type) {
        TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(type);
        if (triggerInterpreter == null) {
            throw new CobiGenRuntimeException("No Plugin registered for type " + type);
        }
        if (triggerInterpreter.getInputReader() == null) {
            throw new CobiGenRuntimeException("No InputReader available for type " + type);
        }

        return triggerInterpreter.getInputReader();
    }

}

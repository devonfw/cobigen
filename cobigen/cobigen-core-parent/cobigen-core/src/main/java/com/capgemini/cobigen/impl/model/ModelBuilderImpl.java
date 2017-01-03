package com.capgemini.cobigen.impl.model;

import java.util.HashMap;
import java.util.Map;

import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.extension.ModelBuilder;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.impl.PluginRegistry;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.config.entity.VariableAssignment;
import com.capgemini.cobigen.impl.validator.InputValidator;

/**
 * The {@link ModelBuilderImpl} is responsible to create the object models for a given object. Therefore, it
 * uses {@link TriggerInterpreter} plug-in extensions to query available {@link InputReader}s and
 * {@link MatcherInterpreter}s
 */
public class ModelBuilderImpl implements ModelBuilder {

    /** Input object for which a new object model should be created */
    private Object generatorInput;

    /** Trigger, which has been activated for the given input */
    private Trigger trigger;

    /**
     * Creates a new {@link ModelBuilderImpl} instance for the given properties
     * @param generatorInput
     *            object for which a new object model should be created
     * @param trigger
     *            which has been activated for the given input
     */
    public ModelBuilderImpl(Object generatorInput, Trigger trigger) {
        if (generatorInput == null || trigger == null || trigger.getMatcher() == null) {
            throw new IllegalArgumentException(
                "Cannot create Model from input == null || trigger == null || trigger.getMatcher() == null");
        }
        this.generatorInput = generatorInput;
        this.trigger = trigger;
    }

    /**
     * Creates a new model by trying to retrieve the corresponding {@link TriggerInterpreter} from the plug-in
     * registry
     * @return the created model
     * @throws InvalidConfigurationException
     *             if there are {@link VariableAssignment}s, which could not be resolved
     */
    @Override
    public Map<String, Object> createModel() throws InvalidConfigurationException {
        TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
        InputValidator.validateTriggerInterpreter(triggerInterpreter, trigger);
        return createModel(triggerInterpreter);
    }

    /**
     * Creates a new model by using the given {@link TriggerInterpreter} to retrieve the {@link InputReader}
     * and {@link MatcherInterpreter} from.
     * @param triggerInterpreter
     *            to be used
     * @return the created model
     * @throws InvalidConfigurationException
     *             if there are {@link VariableAssignment}s, which could not be resolved
     */
    @Override
    public Map<String, Object> createModel(TriggerInterpreter triggerInterpreter) throws InvalidConfigurationException {
        Map<String, Object> model = new HashMap<>(triggerInterpreter.getInputReader().createModel(generatorInput));
        return model;
    }

    /**
     * Enriches the model by the context variables of the trigger.
     * @param model
     *            to be enriched
     * @param triggerInterpreter
     *            {@link TriggerInterpreter} to resolve the variables
     * @return the adapted model reference.
     */
    public Map<String, Object> enrichByContextVariables(Map<String, Object> model,
        TriggerInterpreter triggerInterpreter) {
        model.put("variables",
            new ContextVariableResolver(generatorInput, trigger).resolveVariables(triggerInterpreter));
        return model;
    }

}

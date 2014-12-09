package com.capgemini.cobigen.model;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.config.entity.VariableAssignment;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.extension.IMatcher;
import com.capgemini.cobigen.extension.IModelBuilder;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;
import com.capgemini.cobigen.validator.InputValidator;

/**
 * The {@link ModelBuilder} is responsible to create the object models for a given object. Therefore, it uses
 * {@link ITriggerInterpreter} plug-in extensions to query available {@link IInputReader}s and
 * {@link IMatcher}s
 * @author mbrunnli (08.04.2014)
 */
public class ModelBuilder implements IModelBuilder {

    /**
     * Input object activates a matcher and thus is target for context variable extraction. Possibly a
     * combined or wrapping object for multiple input objects
     */
    private Object matcherInput;

    /**
     * Input object for which a new object model should be created
     */
    private Object generatorInput;

    /**
     * Trigger, which has been activated for the given input
     */
    private Trigger trigger;

    /**
     * Creates a new {@link ModelBuilder} instance for the given properties
     * @param generatorInput
     *            object for which a new object model should be created
     * @param trigger
     *            which has been activated for the given input
     * @param matcherInput
     *            input object activates a matcher and thus is target for context variable extraction.
     *            Possibly a combined or wrapping object for multiple input objects
     * @author mbrunnli (09.04.2014)
     */
    public ModelBuilder(Object generatorInput, Trigger trigger, Object matcherInput) {
        if (generatorInput == null || trigger == null || trigger.getMatcher() == null) {
            throw new IllegalArgumentException(
                "Cannot create Model from input == null || trigger == null || trigger.getMatcher() == null");
        }
        this.generatorInput = generatorInput;
        this.trigger = trigger;
        this.matcherInput = matcherInput;
    }

    /**
     * Creates a new model by using the given {@link ITriggerInterpreter} to retrieve the {@link IInputReader}
     * and {@link IMatcher} from. Furthermore, the model will be directly converted to the DOM representation
     * to enable xPath within for FreeMarker.
     * @param triggerInterpreter
     *            to be used
     * @return the created model
     * @throws InvalidConfigurationException
     *             if there are {@link VariableAssignment}s, which could not be resolved
     * @author mbrunnli (08.04.2014)
     */
    public Document createModelAndConvertToDOM(ITriggerInterpreter triggerInterpreter)
        throws InvalidConfigurationException {
        return new ModelConverter(createModel(triggerInterpreter)).convertToDOM();
    }

    /**
     * Creates a new model by trying to retrieve the corresponding {@link ITriggerInterpreter} from the
     * plug-in registry
     * @return the created model
     * @throws InvalidConfigurationException
     *             if there are {@link VariableAssignment}s, which could not be resolved
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public Map<String, Object> createModel() throws InvalidConfigurationException {
        ITriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
        InputValidator.validateTriggerInterpreter(triggerInterpreter, trigger);
        return createModel(triggerInterpreter);
    }

    /**
     * Creates a new model by using the given {@link ITriggerInterpreter} to retrieve the {@link IInputReader}
     * and {@link IMatcher} from.
     * @param triggerInterpreter
     *            to be used
     * @return the created model
     * @throws InvalidConfigurationException
     *             if there are {@link VariableAssignment}s, which could not be resolved
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public Map<String, Object> createModel(ITriggerInterpreter triggerInterpreter)
        throws InvalidConfigurationException {
        Map<String, Object> model =
            new HashMap<>(triggerInterpreter.getInputReader().createModel(generatorInput));
        if (matcherInput != null) {
            model.put("variables",
                new ContextVariableResolver(matcherInput, trigger).resolveVariables(triggerInterpreter));
        }
        model.put("variables",
            new ContextVariableResolver(generatorInput, trigger).resolveVariables(triggerInterpreter));
        return model;
    }

}

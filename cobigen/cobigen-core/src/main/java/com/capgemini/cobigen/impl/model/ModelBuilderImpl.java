package com.capgemini.cobigen.impl.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.capgemini.cobigen.api.PluginRegistry;
import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.MatcherInterpreter;
import com.capgemini.cobigen.api.extension.ModelBuilder;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.config.entity.VariableAssignment;
import com.capgemini.cobigen.impl.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.impl.validator.InputValidator;

/**
 * The {@link ModelBuilderImpl} is responsible to create the object models for a given object. Therefore, it
 * uses {@link TriggerInterpreter} plug-in extensions to query available {@link InputReader}s and
 * {@link MatcherInterpreter}s
 * @author mbrunnli (08.04.2014)
 */
public class ModelBuilderImpl implements ModelBuilder {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ModelBuilderImpl.class);

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
     * Creates a new {@link ModelBuilderImpl} instance for the given properties
     * @param generatorInput
     *            object for which a new object model should be created
     * @param trigger
     *            which has been activated for the given input
     * @param matcherInput
     *            input object activates a matcher and thus is target for context variable extraction.
     *            Possibly a combined or wrapping object for multiple input objects
     * @author mbrunnli (09.04.2014)
     */
    public ModelBuilderImpl(Object generatorInput, Trigger trigger, Object matcherInput) {
        if (generatorInput == null || trigger == null || trigger.getMatcher() == null) {
            throw new IllegalArgumentException(
                "Cannot create Model from input == null || trigger == null || trigger.getMatcher() == null");
        }
        this.generatorInput = generatorInput;
        this.trigger = trigger;
        this.matcherInput = matcherInput;
    }

    /**
     * Creates a new model by using the given {@link TriggerInterpreter} to retrieve the {@link InputReader}
     * and {@link MatcherInterpreter} from. Furthermore, the model will be directly converted to the DOM
     * representation to enable xPath within for FreeMarker.
     * @param triggerInterpreter
     *            to be used
     * @return the created model
     * @throws InvalidConfigurationException
     *             if there are {@link VariableAssignment}s, which could not be resolved
     * @author mbrunnli (08.04.2014)
     */
    public Document createModelAndConvertToDOM(TriggerInterpreter triggerInterpreter)
        throws InvalidConfigurationException {
        return new ModelConverter(createModel(triggerInterpreter)).convertToDOM();
    }

    /**
     * Creates a new model by trying to retrieve the corresponding {@link TriggerInterpreter} from the plug-in
     * registry
     * @return the created model
     * @throws InvalidConfigurationException
     *             if there are {@link VariableAssignment}s, which could not be resolved
     * @author mbrunnli (08.04.2014)
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
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public Map<String, Object> createModel(TriggerInterpreter triggerInterpreter)
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

    /**
     * Enriches the model by reference by additional logic providing beans.
     * @param model
     *            to be enriched
     * @param logicClasses
     *            logic implementing beans to be made accessible
     */
    public void enrichByLogicBeans(Map<String, Object> model, List<Class<?>> logicClasses) {
        for (Class<?> logicClass : logicClasses) {
            try {
                model.put(logicClass.getSimpleName(), logicClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.warn(
                    "The Java class '{}' could not been instantiated for template processing and thus will be missing in the model.",
                    logicClass.getCanonicalName());
            }
        }
    }

}

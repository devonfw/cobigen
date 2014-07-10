/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.validator;

import java.util.Map;
import java.util.Map.Entry;

import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.exceptions.PluginProcessingException;
import com.capgemini.cobigen.extension.ITriggerInterpreter;

/**
 * The {@link InputValidator} takes care of valid API user input, e.g., checks for null references
 * @author mbrunnli (08.04.2014)
 */
public class InputValidator {

    /**
     * Validates an {@link ITriggerInterpreter} and the corresponding {@link Trigger} for null references
     * @param triggerInterpreter
     *            to be validated
     * @param trigger
     *            to be validated
     * @author mbrunnli (08.04.2014)
     */
    public static void validateTriggerInterpreter(ITriggerInterpreter triggerInterpreter, Trigger trigger) {
        if (trigger == null)
            throw new IllegalArgumentException("Invalid trigger == null");
        validateTriggerInterpreterInternal(triggerInterpreter, trigger.getType());
    }

    /**
     * Validates an {@link ITriggerInterpreter} for null references
     * @param triggerInterpreter
     *            to be validated
     * @author mbrunnli (08.04.2014)
     */
    public static void validateTriggerInterpreter(ITriggerInterpreter triggerInterpreter) {
        validateTriggerInterpreterInternal(triggerInterpreter, null);
    }

    /**
     * Validates an {@link ITriggerInterpreter} and the corresponding triggerType for null references
     * @param triggerInterpreter
     *            to be validated
     * @param triggerType
     *            to be validated
     * @author mbrunnli (08.04.2014)
     */
    private static void validateTriggerInterpreterInternal(ITriggerInterpreter triggerInterpreter,
        String triggerType) {
        if (triggerInterpreter == null)
            throw new IllegalArgumentException("No TriggerInterpreter "
                + (triggerType != null ? "for type '" + triggerType + "' " : "") + "found/provided!");

        if (triggerInterpreter.getInputReader() == null)
            throw new IllegalArgumentException("The TriggerInterpreter for type '"
                + triggerInterpreter.getType()
                + "' has to declare an InputReader, which is currently not the case!");
    }

    /**
     * Validates all given input objects for null references
     * @param objects
     *            to be validated
     * @author mbrunnli (10.04.2014)
     */
    public static void validateInputsUnequalNull(Object... objects) {
        for (Object o : objects) {
            if (o == null)
                throw new IllegalArgumentException("None of the input values must be null");
        }
    }

    /**
     * Validates a {@link Map} of resolved variables for null keys and values
     * @param resolvedVariables
     *            to be validated
     * @author mbrunnli (10.04.2014)
     */
    public static void validateResolvedVariables(Map<String, String> resolvedVariables) {
        if (resolvedVariables == null) {
            throw new PluginProcessingException("A Plug-In must not return null as resolved Variables");
        }

        for (Entry<String, String> var : resolvedVariables.entrySet()) {
            if (var.getKey() == null)
                throw new PluginProcessingException(
                    "A Plug-In must not add entries with null keys into the resolved variables Map");
            if (var.getValue() == null)
                throw new PluginProcessingException(
                    "A Plug-In must not add entries with null values into the resolved variables Map");
        }
    }

}

package com.capgemini.cobigen.impl.validator;

import java.util.Map;
import java.util.Map.Entry;

import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.impl.PluginRegistry;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.exceptions.PluginProcessingException;

/**
 * The {@link InputValidator} takes care of valid API user input, e.g., checks for null references
 */
public class InputValidator {

    /**
     * Validates the trigger to be not null as well as to be connected to any trigger interpreter.
     * @param trigger
     *            {@link Trigger} to be validated
     */
    public static void validateTrigger(Trigger trigger) {
        if (trigger == null) {
            throw new IllegalArgumentException("Invalid trigger == null");
        }
        TriggerInterpreter interpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
        validateTriggerInterpreterInternal(interpreter, trigger.getType());
    }

    /**
     * Validates an {@link TriggerInterpreter} and the corresponding {@link Trigger} for null references
     * @param triggerInterpreter
     *            to be validated
     * @param trigger
     *            to be validated
     */
    public static void validateTriggerInterpreter(TriggerInterpreter triggerInterpreter, Trigger trigger) {
        if (trigger == null) {
            throw new IllegalArgumentException("Invalid trigger == null");
        }
        validateTriggerInterpreterInternal(triggerInterpreter, trigger.getType());
    }

    /**
     * Validates an {@link TriggerInterpreter} for null references
     * @param triggerInterpreter
     *            to be validated
     * @author mbrunnli (08.04.2014)
     */
    public static void validateTriggerInterpreter(TriggerInterpreter triggerInterpreter) {
        validateTriggerInterpreterInternal(triggerInterpreter, null);
    }

    /**
     * Validates an {@link TriggerInterpreter} and the corresponding triggerType for null references
     * @param triggerInterpreter
     *            to be validated
     * @param triggerType
     *            to be validated
     * @author mbrunnli (08.04.2014)
     */
    private static void validateTriggerInterpreterInternal(TriggerInterpreter triggerInterpreter, String triggerType) {
        if (triggerInterpreter == null) {
            throw new InvalidConfigurationException("No TriggerInterpreter "
                + (triggerType != null ? "for type '" + triggerType + "' " : "") + "found/provided!");
        }

        if (triggerInterpreter.getInputReader() == null) {
            throw new PluginProcessingException("The TriggerInterpreter for type '" + triggerInterpreter.getType()
                + "' has to declare an InputReader, which is currently not the case!");
        }
    }

    /**
     * Validates all given input objects for null references
     * @param objects
     *            to be validated
     * @author mbrunnli (10.04.2014)
     */
    public static void validateInputsUnequalNull(Object... objects) {
        for (Object o : objects) {
            if (o == null) {
                throw new IllegalArgumentException("None of the input values must be null");
            }
        }
    }

    /**
     * Validates a {@link Map} of resolved variables for null keys
     * @param resolvedVariables
     *            to be validated
     */
    public static void validateResolvedVariables(Map<String, String> resolvedVariables) {
        if (resolvedVariables == null) {
            throw new PluginProcessingException("A Plug-In must not return null as resolved variables.");
        }

        for (Entry<String, String> var : resolvedVariables.entrySet()) {
            if (var.getKey() == null) {
                throw new PluginProcessingException(
                    "A Plug-In must not add entries with null keys into the resolved variables map.");
            }
        }
    }

}

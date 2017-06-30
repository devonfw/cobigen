package com.capgemini.cobigen.impl.model;

import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.api.to.MatcherTo;
import com.capgemini.cobigen.api.to.VariableAssignmentTo;
import com.capgemini.cobigen.impl.config.entity.Matcher;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.config.entity.VariableAssignment;
import com.capgemini.cobigen.impl.config.entity.Variables;
import com.capgemini.cobigen.impl.exceptions.PluginProcessingException;
import com.capgemini.cobigen.impl.validator.InputValidator;
import com.google.common.collect.Lists;

/**
 * Resolves all context variables for a given input and its trigger
 */
public class ContextVariableResolver {

    /**
     * Input object for which a new object model should be created
     */
    private Object input;

    /**
     * Trigger, which has been activated for the given input
     */
    private Trigger trigger;

    /**
     * Creates a new {@link ModelBuilderImpl} instance for the given properties
     *
     * @param input
     *            object for which a new object model should be created
     * @param trigger
     *            which has been activated for the given input
     */
    public ContextVariableResolver(Object input, Trigger trigger) {

        if (input == null || trigger == null || trigger.getMatcher() == null) {
            throw new IllegalArgumentException(
                "Cannot create Model from input == null || trigger == null || trigger.getMatcher() == null");
        }
        this.input = input;
        this.trigger = trigger;
    }

    /**
     * Resolves all {@link VariableAssignment}s by using the given {@link TriggerInterpreter}
     *
     * @param triggerInterpreter
     *            to be used
     * @return the mapping of variable to value
     * @throws InvalidConfigurationException
     *             if there are {@link VariableAssignment}s, which could not be resolved
     */
    public Variables resolveVariables(TriggerInterpreter triggerInterpreter) throws InvalidConfigurationException {

        return resolveVariables(triggerInterpreter, null);
    }

    /**
     * Resolves all {@link VariableAssignment}s by using the given {@link TriggerInterpreter}
     *
     * @param triggerInterpreter
     *            to be used
     * @param parent
     *            the parent {@link Variables} to inherit.
     * @return the mapping of variable to value
     * @throws InvalidConfigurationException
     *             if there are {@link VariableAssignment}s, which could not be resolved
     */
    public Variables resolveVariables(TriggerInterpreter triggerInterpreter, Variables parent)
        throws InvalidConfigurationException {

        Variables variables = new Variables(parent);
        for (Matcher m : trigger.getMatcher()) {
            MatcherTo matcherTo = new MatcherTo(m.getType(), m.getValue(), input);
            if (triggerInterpreter.getMatcher().matches(matcherTo)) {
                Map<String, String> resolvedVariables;
                try {
                    resolvedVariables =
                        triggerInterpreter.getMatcher().resolveVariables(matcherTo, getVariableAssignments(m));
                } catch (InvalidConfigurationException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new PluginProcessingException(e);
                }
                InputValidator.validateResolvedVariables(resolvedVariables);
                variables.putAll(resolvedVariables);
            }
        }
        return variables;
    }

    /**
     * Retrieves all {@link VariableAssignment}s from the given {@link Matcher} and converts them into
     * transfer objects
     *
     * @param m
     *            {@link Matcher} to retrieve the {@link VariableAssignment}s from
     * @return a {@link List} of {@link VariableAssignmentTo}s
     * @author mbrunnli (08.04.2014)
     */
    private List<VariableAssignmentTo> getVariableAssignments(Matcher m) {

        List<VariableAssignmentTo> variableAssignments = Lists.newLinkedList();
        for (VariableAssignment va : m.getVariableAssignments()) {
            variableAssignments.add(new VariableAssignmentTo(va.getType(), va.getVarName(), va.getValue()));
        }
        return variableAssignments;
    }
}

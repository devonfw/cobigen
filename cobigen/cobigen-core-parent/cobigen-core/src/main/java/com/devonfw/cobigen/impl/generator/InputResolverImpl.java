package com.capgemini.cobigen.impl.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import com.capgemini.cobigen.api.InputInterpreter;
import com.capgemini.cobigen.api.annotation.Cached;
import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.api.to.MatcherTo;
import com.capgemini.cobigen.impl.config.entity.ContainerMatcher;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.extension.PluginRegistry;
import com.capgemini.cobigen.impl.generator.api.InputResolver;
import com.capgemini.cobigen.impl.generator.api.MatcherEvaluator;

/** */
public class InputResolverImpl implements InputResolver {

    /** {@link MatcherEvaluator} instance */
    @Inject
    private MatcherEvaluator matcherEvaluator;

    /** {@link InputInterpreter} instance */
    @Inject
    private InputInterpreter inputInterpreter;

    @Cached
    @Override
    public List<Object> resolveContainerElements(Object input, Trigger trigger) {
        List<Object> inputObjects = new ArrayList<>();
        if (inputInterpreter.combinesMultipleInputs(input)) {
            TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
            InputReader inputReader = triggerInterpreter.getInputReader();

            // check whether the inputs should be retrieved recursively
            boolean retrieveInputsRecursively = false;
            for (ContainerMatcher containerMatcher : trigger.getContainerMatchers()) {
                MatcherTo matcherTo = new MatcherTo(containerMatcher.getType(), containerMatcher.getValue(), input);
                if (triggerInterpreter.getMatcher().matches(matcherTo)) {
                    if (!retrieveInputsRecursively) {
                        retrieveInputsRecursively = containerMatcher.isRetrieveObjectsRecursively();
                    } else {
                        break;
                    }
                }
            }

            if (retrieveInputsRecursively) {
                inputObjects = inputReader.getInputObjectsRecursively(input, trigger.getInputCharset());
            } else {
                inputObjects = inputReader.getInputObjects(input, trigger.getInputCharset());
            }

            // Remove non matching inputs
            Iterator<Object> it = inputObjects.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (!matcherEvaluator.matches(next, trigger.getMatcher(), triggerInterpreter)) {
                    it.remove();
                }
            }
        } else {
            inputObjects.add(input);
        }
        return inputObjects;
    }
}

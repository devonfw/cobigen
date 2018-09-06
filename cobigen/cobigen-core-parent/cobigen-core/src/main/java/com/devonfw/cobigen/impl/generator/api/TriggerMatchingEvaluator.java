package com.devonfw.cobigen.impl.generator.api;

import java.util.List;

import com.devonfw.cobigen.impl.config.entity.Trigger;

/** Evaluator of the trigger matching logic. Extracted into a separate class enabling caching by AOP. */
public interface TriggerMatchingEvaluator {

    /**
     * Returns all matching {@link Trigger}s for the given input object
     *
     * @param matcherInput
     *            object
     * @return the {@link List} of matching {@link Trigger}s
     */
    public List<Trigger> getMatchingTriggers(Object matcherInput);

}

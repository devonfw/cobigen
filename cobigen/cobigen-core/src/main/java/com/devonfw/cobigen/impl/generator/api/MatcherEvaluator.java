package com.devonfw.cobigen.impl.generator.api;

import java.util.List;

import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.impl.config.entity.Matcher;

/** Evaluator of the matcher logic. Extracted into a separate class enabling caching by AOP. */
public interface MatcherEvaluator {

    /**
     * Checks whether the list of matches matches the matcher input according to the given trigger
     * interpreter.
     * @param matcherInput
     *            input for the matcher
     * @param matcherList
     *            list of matchers to be checked
     * @param triggerInterpreter
     *            to called for checking retrieving the matchers matching result
     * @return <code>true</code> if the given matcher input matches the matcher list<br>
     *         <code>false</code>, otherwise
     */
    public boolean matches(Object matcherInput, List<Matcher> matcherList, TriggerInterpreter triggerInterpreter);
}

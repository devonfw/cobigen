package com.capgemini.cobigen.impl.generator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.annotation.Cached;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.api.to.MatcherTo;
import com.capgemini.cobigen.impl.config.entity.Matcher;
import com.capgemini.cobigen.impl.config.entity.io.AccumulationType;
import com.capgemini.cobigen.impl.generator.api.MatcherEvaluator;

/**
 * Implementation of the matching logic based on propositional logic.
 */
public class MatcherEvaluatorImpl implements MatcherEvaluator {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(MatcherEvaluatorImpl.class);

    @Cached
    @Override
    public boolean matches(Object matcherInput, List<Matcher> matcherList, TriggerInterpreter triggerInterpreter) {
        boolean matcherSetMatches = false;
        LOG.debug("Check matchers for TriggerInterpreter[type='{}'] ...", triggerInterpreter.getType());
        MATCHER_LOOP:
        for (Matcher matcher : matcherList) {
            MatcherTo matcherTo = new MatcherTo(matcher.getType(), matcher.getValue(), matcherInput);
            LOG.trace("Check {} ...", matcherTo);
            if (triggerInterpreter.getMatcher().matches(matcherTo)) {
                switch (matcher.getAccumulationType()) {
                case NOT:
                    LOG.trace("NOT Matcher matches -> trigger match fails.");
                    matcherSetMatches = false;
                    break MATCHER_LOOP;
                case OR:
                case AND:
                    LOG.trace("Matcher matches.");
                    matcherSetMatches = true;
                    break;
                default:
                }
            } else {
                if (matcher.getAccumulationType() == AccumulationType.AND) {
                    LOG.trace("AND Matcher does not match -> trigger match fails.");
                    matcherSetMatches = false;
                    break MATCHER_LOOP;
                }
            }
        }
        LOG.debug("Matcher declarations " + (matcherSetMatches ? "match the input." : "do not match the input."));
        return matcherSetMatches;
    }
}

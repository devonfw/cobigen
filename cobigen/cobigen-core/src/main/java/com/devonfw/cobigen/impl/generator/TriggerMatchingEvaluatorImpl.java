package com.devonfw.cobigen.impl.generator;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.annotation.Cached;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.api.to.MatcherTo;
import com.devonfw.cobigen.impl.config.ConfigurationHolder;
import com.devonfw.cobigen.impl.config.entity.ContainerMatcher;
import com.devonfw.cobigen.impl.config.entity.Matcher;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.generator.api.MatcherEvaluator;
import com.devonfw.cobigen.impl.generator.api.TriggerMatchingEvaluator;
import com.devonfw.cobigen.impl.validator.InputValidator;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

/**
 * This implementation's main focus is the implementation of the matching behavior of {@link Matcher Matchers} and
 * {@link ContainerMatcher ContainerMatchers}
 */
public class TriggerMatchingEvaluatorImpl implements TriggerMatchingEvaluator {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(TriggerMatchingEvaluatorImpl.class);

  /** Configuration Holder */
  @Inject
  private ConfigurationHolder configurationHolder;

  /** Matcher evaluator to match inputs */
  @Inject
  private MatcherEvaluator matcherEvaluator;

  @Cached
  @Override
  public List<Trigger> getMatchingTriggers(Object matcherInput) {

    LOG.debug("Retrieve matching trigger. input {}, hash: {}", matcherInput, matcherInput.hashCode());
    List<Trigger> matchingTrigger = Lists.newLinkedList();
    for (Trigger trigger : this.configurationHolder.readContextConfiguration().getTriggers()) {
      TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
      if (triggerInterpreter == null) {
        continue;
        // trigger interpreter not yet activated as the plug-in was not yet used.
        // unfortunately the invariant here is, that the CobiGen user has once called CobigenImpl#read
        // to get the matcher input
      }
      InputValidator.validateTriggerInterpreter(triggerInterpreter, trigger);
      LOG.debug("Check {} to match the input.", trigger);

      if (triggerInterpreter.getInputReader().isValidInput(matcherInput)) {
        LOG.debug("Matcher input is marked as valid.");
        boolean triggerMatches = this.matcherEvaluator.matches(matcherInput, trigger.getMatcher(), triggerInterpreter);
        if (triggerMatches) {
          matchingTrigger.add(trigger);
        }

        // if a match has been found do not check container matchers in addition. The input will be
        // recognized as a container.
        if (!triggerMatches) {
          LOG.debug("Check container matchers ...");
          FOR_CONTAINERMATCHER: for (ContainerMatcher containerMatcher : trigger.getContainerMatchers()) {
            MatcherTo containerMatcherTo = new MatcherTo(containerMatcher.getType(), containerMatcher.getValue(),
                matcherInput);
            LOG.debug("Check {} ...", containerMatcherTo);
            if (triggerInterpreter.getMatcher().matches(containerMatcherTo)) {
              LOG.debug("Match! Retrieve objects from container ...", containerMatcherTo);
              // keep backward-compatibility
              List<Object> containerResources;
              if (containerMatcher.isRetrieveObjectsRecursively()) {
                containerResources = triggerInterpreter.getInputReader().getInputObjectsRecursively(matcherInput,
                    Charsets.UTF_8);
              } else {
                // the charset does not matter as we just want to see whether there is one
                // matcher for one of the container resources
                containerResources = triggerInterpreter.getInputReader().getInputObjects(matcherInput, Charsets.UTF_8);
              }
              LOG.debug("{} objects retrieved.", containerResources.size());

              // check if at least one container element matches the matcher declarations
              for (Object resource : containerResources) {
                if (this.matcherEvaluator.matches(resource, trigger.getMatcher(), triggerInterpreter)) {
                  LOG.debug("At least one object from container matches.");
                  triggerMatches = true;
                  break FOR_CONTAINERMATCHER;
                }
              }
              LOG.debug("No element of the container is matched.");
            }
          }
          if (triggerMatches) {
            matchingTrigger.add(new Trigger(trigger, true));
          }
        }
        LOG.debug("{} {}", trigger, triggerMatches ? "matches." : "does not match.");
      }
    }
    return matchingTrigger;
  }
}

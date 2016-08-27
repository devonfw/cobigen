package com.capgemini.cobigen.generator;

import java.util.List;

import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.config.ConfigurationHolder;
import com.capgemini.cobigen.config.TemplatesConfiguration;
import com.capgemini.cobigen.config.entity.ContainerMatcher;
import com.capgemini.cobigen.config.entity.Increment;
import com.capgemini.cobigen.config.entity.Template;
import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.capgemini.cobigen.extension.InputReaderV13;
import com.capgemini.cobigen.extension.to.IncrementTo;
import com.capgemini.cobigen.extension.to.MatcherTo;
import com.capgemini.cobigen.extension.to.TemplateTo;
import com.capgemini.cobigen.pluginmanager.PluginRegistry;
import com.capgemini.cobigen.validator.InputValidator;
import com.google.common.collect.Lists;

public class InputProcessorCache {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(InputProcessorCache.class);

    private Object cachedInput;

    private ConfigurationHolder configurationHolder;

    /**
     * Creates a new cache instance for the given input object.
     */
    InputProcessorCache(Object input, ConfigurationHolder configurationHolder) {
        cachedInput = input;
        this.configurationHolder = configurationHolder;
    }

    /**
     * Returns all matching trigger ids for a given input object
     *
     * @param matcherInput
     *            object
     * @return the {@link List} of matching trigger ids
     */
    public List<String> getMatchingTriggerIds(Object matcherInput) {

        LOG.info("Matching trigger IDs requested.");
        List<String> matchingTriggerIds = Lists.newLinkedList();
        for (Trigger trigger : getMatchingTriggers(matcherInput)) {
            matchingTriggerIds.add(trigger.getId());
        }
        LOG.info("{} matching trigger IDs found.", matchingTriggerIds.size());
        return matchingTriggerIds;
    }

    /**
     * Returns all matching increments for a given input object
     *
     * @param matcherInput
     *            object
     * @return this {@link List} of matching increments
     * @throws InvalidConfigurationException
     *             if the configuration of CobiGen is not valid
     * @author mbrunnli (09.04.2014)
     */
    public List<IncrementTo> getMatchingIncrements(Object matcherInput) throws InvalidConfigurationException {

        LOG.info("Matching increments requested.");
        List<IncrementTo> increments = Lists.newLinkedList();
        for (TemplatesConfiguration templatesConfiguration : getMatchingTemplatesConfigurations(
            matcherInput)) {
            increments.addAll(convertIncrements(templatesConfiguration.getAllGenerationPackages(),
                templatesConfiguration.getTrigger(), templatesConfiguration.getTriggerInterpreter()));
        }
        LOG.info("{} matching increments found.", increments.size());
        return increments;
    }

    /**
     * Converts a {@link List} of {@link Increment}s with their parent {@link Trigger} to a {@link List} of
     * {@link IncrementTo}s
     *
     * @param increments
     *            the {@link List} of {@link Increment}s
     * @param trigger
     *            the parent {@link Trigger}
     * @return the {@link List} of {@link IncrementTo}s
     * @param triggerInterpreter
     *            {@link ITriggerInterpreter} the trigger has been interpreted with
     * @author mbrunnli (10.04.2014)
     */
    // TODO create ToConverter
    private List<IncrementTo> convertIncrements(List<Increment> increments, Trigger trigger,
        ITriggerInterpreter triggerInterpreter) {

        List<IncrementTo> incrementTos = Lists.newLinkedList();
        for (Increment increment : increments) {
            List<TemplateTo> templates = Lists.newLinkedList();
            for (Template template : increment.getTemplates()) {
                templates.add(new TemplateTo(template.getName(), template.getUnresolvedDestinationPath(),
                    template.getMergeStrategy(), trigger, triggerInterpreter));
            }
            incrementTos.add(
                new IncrementTo(increment.getName(), increment.getDescription(), trigger.getId(), templates,
                    convertIncrements(increment.getDependentIncrements(), trigger, triggerInterpreter)));
        }
        return incrementTos;
    }

    /**
     * Returns all matching {@link Trigger}s for the given input object
     *
     * @param matcherInput
     *            object
     * @return the {@link List} of matching {@link Trigger}s
     * @author mbrunnli (09.04.2014)
     */
    private List<Trigger> getMatchingTriggers(Object matcherInput) {

        LOG.debug("Retrieve matching triggers.");
        List<Trigger> matchingTrigger = Lists.newLinkedList();
        for (Trigger trigger : configurationHolder.readContextConfiguration().getTriggers()) {
            ITriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
            InputValidator.validateTriggerInterpreter(triggerInterpreter, trigger);
            LOG.debug("Check {} to match the input.", trigger);

            try {
                if (triggerInterpreter.getInputReader().isValidInput(matcherInput)) {
                    LOG.debug("Matcher input is marked as valid.");
                    boolean triggerMatches =
                        GenerationProcessor.matches(matcherInput, trigger.getMatcher(), triggerInterpreter);

                    // if a match has been found do not check container matchers in addition for performance
                    // issues.
                    if (!triggerMatches) {
                        LOG.debug("Check container matchers ...");
                        FOR_CONTAINERMATCHER:
                        for (ContainerMatcher containerMatcher : trigger.getContainerMatchers()) {
                            MatcherTo containerMatcherTo = new MatcherTo(containerMatcher.getType(),
                                containerMatcher.getValue(), matcherInput);
                            LOG.debug("Check {} ...", containerMatcherTo);
                            if (triggerInterpreter.getMatcher().matches(containerMatcherTo)) {
                                LOG.debug("Match! Retrieve objects from container ...", containerMatcherTo);
                                // keep backward-compatibility
                                List<Object> containerResources;
                                if (triggerInterpreter.getInputReader() instanceof InputReaderV13
                                    && containerMatcher.isRetrieveObjectsRecursively()) {
                                    containerResources =
                                        ((InputReaderV13) triggerInterpreter.getInputReader())
                                            .getInputObjectsRecursively(matcherInput, Charsets.UTF_8);
                                } else {
                                    // the charset does not matter as we just want to see whether there is one
                                    // matcher for one of the container resources
                                    containerResources = triggerInterpreter.getInputReader()
                                        .getInputObjects(matcherInput, Charsets.UTF_8);
                                }
                                LOG.debug("{} objects retrieved.", containerResources.size());

                                for (Object resource : containerResources) {
                                    if (GenerationProcessor.matches(resource, trigger.getMatcher(),
                                        triggerInterpreter)) {
                                        LOG.debug("At least one object from container matches.");
                                        triggerMatches = true;
                                        break FOR_CONTAINERMATCHER;
                                    }
                                }
                            }
                        }
                    }
                    LOG.info("{} {}", trigger, triggerMatches ? "matches." : "does not match.");
                    if (triggerMatches) {
                        matchingTrigger.add(trigger);
                    }
                }
            } catch (Throwable e) {
                LOG.error("The TriggerInterpreter[type='{}'] exited abruptly!", triggerInterpreter.getType(),
                    e);
            }
        }
        return matchingTrigger;
    }

    /**
     * Returns the {@link List} of matching templates for the given input object
     *
     * @param matcherInput
     *            input object activates a matcher and thus is target for context variable extraction.
     *            Possibly a combined or wrapping object for multiple input objects
     * @return the {@link List} of matching templates
     * @throws InvalidConfigurationException
     *             if the configuration is not valid
     * @author mbrunnli (09.04.2014)
     */
    public List<TemplateTo> getMatchingTemplates(Object matcherInput) throws InvalidConfigurationException {

        LOG.info("Matching templates requested.");
        List<TemplateTo> templates = Lists.newLinkedList();
        for (TemplatesConfiguration templatesConfiguration : getMatchingTemplatesConfigurations(
            matcherInput)) {
            for (Template template : templatesConfiguration.getAllTemplates()) {
                templates.add(new TemplateTo(template.getName(), template.getUnresolvedDestinationPath(),
                    template.getMergeStrategy(), templatesConfiguration.getTrigger(),
                    templatesConfiguration.getTriggerInterpreter()));
            }
        }
        LOG.info("{} matching templates found.", templates.size());
        return templates;
    }

    /**
     * Returns the {@link List} of matching {@link TemplatesConfiguration}s for the given input object
     *
     * @param matcherInput
     *            input object activates a matcher and thus is target for context variable extraction.
     *            Possibly a combined or wrapping object for multiple input objects
     * @return the {@link List} of matching {@link TemplatesConfiguration}s
     * @throws InvalidConfigurationException
     *             if the configuration is not valid
     * @author mbrunnli (09.04.2014)
     */
    private List<TemplatesConfiguration> getMatchingTemplatesConfigurations(Object matcherInput)
        throws InvalidConfigurationException {

        LOG.debug("Retrieve matching template configurations.");
        List<TemplatesConfiguration> templateConfigurations = Lists.newLinkedList();
        for (Trigger trigger : getMatchingTriggers(matcherInput)) {
            ITriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
            InputValidator.validateTriggerInterpreter(triggerInterpreter);

            TemplatesConfiguration templatesConfiguration =
                configurationHolder.readTemplatesConfiguration(trigger, triggerInterpreter);
            if (templatesConfiguration != null) {
                templateConfigurations.add(templatesConfiguration);
            }
        }
        return templateConfigurations;
    }

    /**
     * Checks whether there is at least one input reader, which interprets the given input as combined input.
     * @param input
     *            object
     * @return <code>true</code> if there is at least one input reader, which interprets the given input as
     *         combined input,<code>false</code>, otherwise
     * @author mbrunnli (03.12.2014)
     */
    public boolean combinesMultipleInputs(Object input) {
        List<Trigger> matchingTriggers = getMatchingTriggers(input);
        for (Trigger trigger : matchingTriggers) {
            ITriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
            return triggerInterpreter.getInputReader().combinesMultipleInputObjects(input);
        }
        return false;
    }

}

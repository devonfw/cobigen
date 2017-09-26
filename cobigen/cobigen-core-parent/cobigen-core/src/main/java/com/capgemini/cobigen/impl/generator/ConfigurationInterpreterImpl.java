package com.capgemini.cobigen.impl.generator;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.ConfigurationInterpreter;
import com.capgemini.cobigen.api.annotation.Cached;
import com.capgemini.cobigen.api.exception.CobiGenRuntimeException;
import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.api.extension.TriggerInterpreter;
import com.capgemini.cobigen.api.to.IncrementTo;
import com.capgemini.cobigen.api.to.MatcherTo;
import com.capgemini.cobigen.api.to.TemplateTo;
import com.capgemini.cobigen.impl.config.ConfigurationHolder;
import com.capgemini.cobigen.impl.config.TemplatesConfiguration;
import com.capgemini.cobigen.impl.config.entity.ContainerMatcher;
import com.capgemini.cobigen.impl.config.entity.Increment;
import com.capgemini.cobigen.impl.config.entity.Template;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.capgemini.cobigen.impl.config.entity.Variables;
import com.capgemini.cobigen.impl.config.resolver.PathExpressionResolver;
import com.capgemini.cobigen.impl.exceptions.UnknownContextVariableException;
import com.capgemini.cobigen.impl.extension.PluginRegistry;
import com.capgemini.cobigen.impl.model.ContextVariableResolver;
import com.capgemini.cobigen.impl.validator.InputValidator;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

/**
 * The configuration holder enables caching for several requests with the same input. Therefore, the cache
 * relies on the assumption, that the input objects will be hold in memory as long as they are referenced. Due
 * to the fact, that this cache is just utilizing a {@link WeakHashMap}, it will automatically discard entries
 * which are collected by the GC.
 */
public class ConfigurationInterpreterImpl implements ConfigurationInterpreter {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationInterpreterImpl.class);

    /** {@link ConfigurationHolder} holding CobiGen's configuration */
    private ConfigurationHolder configurationHolder;

    /**
     * Creates a new instance.
     * @param configurationHolder
     *            {@link ConfigurationHolder} holding CobiGen's configuration
     */
    ConfigurationInterpreterImpl(ConfigurationHolder configurationHolder) {
        this.configurationHolder = configurationHolder;
    }

    @Cached
    @Override
    public List<String> getMatchingTriggerIds(Object matcherInput) {

        LOG.debug("Matching trigger IDs requested.");
        List<String> matchingTriggerIds = Lists.newLinkedList();
        for (Trigger trigger : getMatchingTriggers(matcherInput)) {
            matchingTriggerIds.add(trigger.getId());
        }
        LOG.debug("{} matching trigger IDs found.", matchingTriggerIds.size());
        return matchingTriggerIds;
    }

    @Cached
    @Override
    public List<IncrementTo> getMatchingIncrements(Object matcherInput) throws InvalidConfigurationException {

        LOG.debug("Matching increments requested.");
        List<IncrementTo> increments = Lists.newLinkedList();
        for (TemplatesConfiguration templatesConfiguration : getMatchingTemplatesConfigurations(matcherInput)) {
            increments.addAll(convertIncrements(templatesConfiguration.getAllGenerationPackages(),
                templatesConfiguration.getTrigger(), templatesConfiguration.getTriggerInterpreter()));
        }
        LOG.debug("{} matching increments found.", increments.size());
        return increments;
    }

    @Cached
    @Override
    public List<TemplateTo> getMatchingTemplates(Object matcherInput) throws InvalidConfigurationException {

        LOG.debug("Matching templates requested.");
        List<TemplateTo> templates = Lists.newLinkedList();
        for (TemplatesConfiguration templatesConfiguration : getMatchingTemplatesConfigurations(matcherInput)) {
            for (Template template : templatesConfiguration.getAllTemplates()) {
                templates.add(new TemplateTo(template.getName(), template.getMergeStrategy(),
                    templatesConfiguration.getTrigger().getId()));
            }
        }
        LOG.debug("{} matching templates found.", templates.size());
        return templates;
    }

    @Override
    public Path resolveTemplateDestinationPath(Path targetRootPath, TemplateTo template, Object input) {
        Trigger trigger = configurationHolder.readContextConfiguration().getTrigger(template.getTriggerId());
        InputValidator.validateTrigger(trigger);

        TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
        Variables variables = new ContextVariableResolver(input, trigger).resolveVariables(triggerInterpreter);
        Template templateEty =
            configurationHolder.readTemplatesConfiguration(trigger, triggerInterpreter).getTemplate(template.getId());
        try {
            String resolvedDestinationPath =
                new PathExpressionResolver(variables).evaluateExpressions(templateEty.getUnresolvedTargetPath());
            return targetRootPath.resolve(resolvedDestinationPath).normalize();
        } catch (UnknownContextVariableException e) {
            throw new CobiGenRuntimeException("Could not resolve path '" + templateEty.getUnresolvedTargetPath()
                + "' for input '" + (input instanceof Object[] ? Arrays.toString((Object[]) input) : input.toString())
                + "' and template '" + templateEty.getAbsoluteTemplatePath() + "'. Available variables: "
                + variables.toString());
        }
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
     *            {@link TriggerInterpreter} the trigger has been interpreted with
     */
    // TODO create ToConverter
    private List<IncrementTo> convertIncrements(List<Increment> increments, Trigger trigger,
        TriggerInterpreter triggerInterpreter) {

        List<IncrementTo> incrementTos = Lists.newLinkedList();
        for (Increment increment : increments) {
            List<TemplateTo> templates = Lists.newLinkedList();
            for (Template template : increment.getTemplates()) {
                templates.add(new TemplateTo(template.getName(), template.getMergeStrategy(), trigger.getId()));
            }
            incrementTos.add(new IncrementTo(increment.getName(), increment.getDescription(), trigger.getId(),
                templates, convertIncrements(increment.getDependentIncrements(), trigger, triggerInterpreter)));
        }
        return incrementTos;
    }

    /**
     * Returns all matching {@link Trigger}s for the given input object
     *
     * @param matcherInput
     *            object
     * @return the {@link List} of matching {@link Trigger}s
     */
    List<Trigger> getMatchingTriggers(Object matcherInput) {

        LOG.debug("Retrieve matching triggers...");
        List<Trigger> matchingTrigger = Lists.newLinkedList();
        for (Trigger trigger : configurationHolder.readContextConfiguration().getTriggers()) {
            TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
            InputValidator.validateTriggerInterpreter(triggerInterpreter, trigger);
            LOG.debug("Check {} to match the input...", trigger);

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
                        MatcherTo containerMatcherTo =
                            new MatcherTo(containerMatcher.getType(), containerMatcher.getValue(), matcherInput);
                        LOG.debug("Check {} ...", containerMatcherTo);
                        if (triggerInterpreter.getMatcher().matches(containerMatcherTo)) {
                            LOG.debug("Match! Retrieve objects from container ...", containerMatcherTo);
                            // keep backward-compatibility
                            List<Object> containerResources;
                            if (containerMatcher.isRetrieveObjectsRecursively()) {
                                containerResources = triggerInterpreter.getInputReader()
                                    .getInputObjectsRecursively(matcherInput, Charsets.UTF_8);
                            } else {
                                // the charset does not matter as we just want to see whether there is one
                                // matcher for one of the container resources
                                containerResources =
                                    triggerInterpreter.getInputReader().getInputObjects(matcherInput, Charsets.UTF_8);
                            }
                            LOG.debug("{} objects retrieved.", containerResources.size());

                            for (Object resource : containerResources) {
                                if (GenerationProcessor.matches(resource, trigger.getMatcher(), triggerInterpreter)) {
                                    LOG.debug("At least one object from container matches.");
                                    triggerMatches = true;
                                    break FOR_CONTAINERMATCHER;
                                }
                            }
                        }
                    }
                }
                LOG.debug("{} {}", trigger, triggerMatches ? "matches." : "does not match.");
                if (triggerMatches) {
                    matchingTrigger.add(trigger);
                }
            } else if (LOG.isDebugEnabled()) {
                LOG.debug("Invalid input for trigger {} of type {}. Input of class '{}': {}", trigger.getId(),
                    trigger.getType(), matcherInput.getClass(),
                    matcherInput.getClass().isArray() ? Arrays.toString((Object[]) matcherInput) : matcherInput);
            }
        }
        return matchingTrigger;
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
     */
    private List<TemplatesConfiguration> getMatchingTemplatesConfigurations(Object matcherInput)
        throws InvalidConfigurationException {

        LOG.debug("Retrieve matching template configurations.");
        List<TemplatesConfiguration> templateConfigurations = Lists.newLinkedList();
        for (Trigger trigger : getMatchingTriggers(matcherInput)) {
            TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());

            TemplatesConfiguration templatesConfiguration =
                configurationHolder.readTemplatesConfiguration(trigger, triggerInterpreter);
            if (templatesConfiguration != null) {
                templateConfigurations.add(templatesConfiguration);
            }
        }
        return templateConfigurations;
    }

}

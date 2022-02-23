package com.devonfw.cobigen.impl.generator;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.ConfigurationInterpreter;
import com.devonfw.cobigen.api.annotation.Cached;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.config.ConfigurationHolder;
import com.devonfw.cobigen.impl.config.TemplatesConfiguration;
import com.devonfw.cobigen.impl.config.entity.Increment;
import com.devonfw.cobigen.impl.config.entity.Template;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.Variables;
import com.devonfw.cobigen.impl.config.resolver.PathExpressionResolver;
import com.devonfw.cobigen.impl.exceptions.UnknownContextVariableException;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.generator.api.TriggerMatchingEvaluator;
import com.devonfw.cobigen.impl.model.ContextVariableResolver;
import com.devonfw.cobigen.impl.validator.InputValidator;
import com.google.common.collect.Lists;

/**
 * The configuration holder enables caching for several requests with the same input. Therefore, the cache relies on the
 * assumption, that the input objects will be hold in memory as long as they are referenced. Due to the fact, that this
 * cache is just utilizing a {@link WeakHashMap}, it will automatically discard entries which are collected by the GC.
 */
public class ConfigurationInterpreterImpl implements ConfigurationInterpreter {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationInterpreterImpl.class);

  /** {@link ConfigurationHolder} holding CobiGen's configuration */
  @Inject
  private ConfigurationHolder configurationHolder;

  /** Matching evaluator for triggers */
  @Inject
  private TriggerMatchingEvaluator triggerMatchingEvaluator;

  @Cached
  @Override
  public List<String> getMatchingTriggerIds(Object matcherInput) {

    LOG.debug("Matching trigger IDs requested.");
    List<String> matchingTriggerIds = Lists.newLinkedList();
    for (Trigger trigger : this.triggerMatchingEvaluator.getMatchingTriggers(matcherInput)) {
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
    List<String> matchingTriggerIds = getMatchingTriggerIds(matcherInput);
    for (TemplatesConfiguration templatesConfiguration : getMatchingTemplatesConfigurations(matcherInput)) {
      increments.addAll(convertIncrements(templatesConfiguration.getAllGenerationPackages(),
          templatesConfiguration.getTrigger(), matchingTriggerIds));
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

    Trigger trigger = this.configurationHolder.readContextConfiguration().getTrigger(template.getTriggerId());
    InputValidator.validateTrigger(trigger);

    TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(trigger.getType());
    // the GenerationReportTo won't be further processed
    Variables variables = new ContextVariableResolver(input, trigger).resolveVariables(triggerInterpreter,
        new GenerationReportTo());
    Template templateEty = this.configurationHolder.readTemplatesConfiguration(trigger).getTemplate(template.getId());
    try {
      String resolvedDestinationPath = new PathExpressionResolver(variables)
          .evaluateExpressions(templateEty.getUnresolvedTargetPath());
      return targetRootPath.resolve(resolvedDestinationPath).normalize();
    } catch (UnknownContextVariableException e) {
      throw new CobiGenRuntimeException(
          "Could not resolve path '" + templateEty.getUnresolvedTargetPath() + "' for input '"
              + (input instanceof Object[] ? Arrays.toString((Object[]) input) : input.toString()) + "' and template '"
              + templateEty.getAbsoluteTemplatePath() + "'. Available variables: " + variables.toString());
    }
  }

  /**
   * Converts a {@link List} of {@link Increment}s with their parent {@link Trigger} to a {@link List} of
   * {@link IncrementTo}s
   *
   * @param increments the {@link List} of {@link Increment}s
   * @param trigger the parent {@link Trigger}
   * @param matchingTriggerIds the {@link List} of matching trigger Id
   * @return the {@link List} of {@link IncrementTo}s
   */
  // TODO create ToConverter
  private List<IncrementTo> convertIncrements(List<Increment> increments, Trigger trigger,
      List<String> matchingTriggerIds) {

    List<IncrementTo> incrementTos = Lists.newLinkedList();
    for (Increment increment : increments) {
      String triggerId = increment.getTrigger().getId();
      if (!triggerId.equals(trigger.getId())) {
        // Check if the external trigger also matches
        if (!matchingTriggerIds.contains(triggerId)) {
          // Abort generation
          throw new InvalidConfigurationException("An external incrementRef to " + increment.getTrigger().getId() + "::"
              + increment.getName() + " is referenced from " + trigger.getId() + " but its trigger does not match");
        }
      }
      List<TemplateTo> templates = Lists.newLinkedList();
      for (Template template : increment.getTemplates()) {
        templates.add(new TemplateTo(template.getName(), template.getMergeStrategy(), triggerId));
      }
      incrementTos.add(new IncrementTo(increment.getName(), increment.getDescription(), triggerId, templates,
          convertIncrements(increment.getDependentIncrements(), trigger, matchingTriggerIds)));
    }
    return incrementTos;
  }

  /**
   * Returns the {@link List} of matching {@link TemplatesConfiguration}s for the given input object
   *
   * @param matcherInput input object activates a matcher and thus is target for context variable extraction. Possibly a
   *        combined or wrapping object for multiple input objects
   * @return the {@link List} of matching {@link TemplatesConfiguration}s
   * @throws InvalidConfigurationException if the configuration is not valid
   */
  private List<TemplatesConfiguration> getMatchingTemplatesConfigurations(Object matcherInput)
      throws InvalidConfigurationException {

    LOG.debug("Retrieve matching template configurations.");
    List<TemplatesConfiguration> templateConfigurations = Lists.newLinkedList();

    for (Trigger trigger : this.triggerMatchingEvaluator.getMatchingTriggers(matcherInput)) {
      TemplatesConfiguration templatesConfiguration = this.configurationHolder.readTemplatesConfiguration(trigger);
      if (templatesConfiguration != null) {
        if (!templateConfigurations.contains(templatesConfiguration)) {
          templateConfigurations.add(templatesConfiguration);
        }
      }
    }
    return templateConfigurations;
  }
}

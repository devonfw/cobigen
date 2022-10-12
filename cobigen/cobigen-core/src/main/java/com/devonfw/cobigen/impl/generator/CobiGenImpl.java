package com.devonfw.cobigen.impl.generator;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import javax.inject.Inject;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.ConfigurationInterpreter;
import com.devonfw.cobigen.api.InputInterpreter;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.extension.ModelBuilder;
import com.devonfw.cobigen.api.to.GenerableArtifact;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.config.ConfigurationHolder;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.generator.api.InputResolver;
import com.devonfw.cobigen.impl.model.ModelBuilderImpl;
import com.google.common.collect.Lists;

/**
 * Implementation of the CobiGen API.
 */
public class CobiGenImpl implements CobiGen {

  /** CobiGen Configuration Cache */
  @Inject
  private ConfigurationHolder configurationHolder;

  /**
   * {@link ConfigurationInterpreter} which holds a cache to improve performance for multiple requests for the same
   * input
   */
  @Inject
  private ConfigurationInterpreter configurationInterpreter;

  /** {@link InputInterpreter} which handles InputReader delegates */
  @Inject
  private InputInterpreter inputInterpreter;

  /** {@link InputResolver} instance */
  @Inject
  private InputResolver inputResolver;

  @Override
  public GenerationReportTo generate(Object input, List<? extends GenerableArtifact> generableArtifacts,
      Path targetRootPath) {

    return generate(input, generableArtifacts, targetRootPath, false, null, (String taskName, Integer progress) -> {
    });
  }

  @Override
  public GenerationReportTo generate(Object input, List<? extends GenerableArtifact> generableArtifacts,
      Path targetRootPath, boolean forceOverride, Map<String, Object> rawModel) {

    return generate(input, generableArtifacts, targetRootPath, forceOverride, null,
        (String taskName, Integer progress) -> {
        });
  }

  @Override
  public GenerationReportTo generate(Object input, List<? extends GenerableArtifact> generableArtifacts,
      Path targetRootPath, boolean forceOverride) {

    return generate(input, generableArtifacts, targetRootPath, forceOverride, (taskname, progress) -> {
    });
  }

  @Override
  public GenerationReportTo generate(Object input, GenerableArtifact generableArtifact, Path targetRootPath,
      boolean forceOverride, boolean generateAnnotation) {

    return generate(input, generableArtifact, targetRootPath, forceOverride, null, (taskname, progress) -> {
    }, generateAnnotation);
  }

  @Override
  public GenerationReportTo generate(Object input, List<? extends GenerableArtifact> generableArtifacts,
      Path targetRootPath, boolean forceOverride, BiConsumer<String, Integer> progressCallback) {

    return generate(input, generableArtifacts, targetRootPath, forceOverride, null, progressCallback);
  }

  @Override
  public GenerationReportTo generate(Object input, List<? extends GenerableArtifact> generableArtifacts,
      Path targetRootPath, boolean forceOverride, Map<String, Object> rawModel,
      BiConsumer<String, Integer> progressCallback) {

    return generate(input, generableArtifacts, targetRootPath, forceOverride, rawModel, progressCallback, false);
  }

  @Override
  public GenerationReportTo generate(Object input, List<? extends GenerableArtifact> generableArtifacts,
      Path targetRootPath, boolean forceOverride, Map<String, Object> rawModel,
      BiConsumer<String, Integer> progressCallback, boolean generateAnnotation) {

    Objects.requireNonNull(input, "Input");
    Objects.requireNonNull(generableArtifacts, "List of Artifacts to be generated");
    if (generableArtifacts.contains(null)) {
      throw new CobiGenRuntimeException(
          "A collection of artifacts to be generated has been passed containing null values. "
              + "Aborting generation, as this has probably not been intended.");
    }
    Objects.requireNonNull(generableArtifacts, "List of Artifacts to be generated");
    Objects.requireNonNull(targetRootPath, "targetRootPath");
    return new GenerationProcessorImpl(this.configurationHolder, this.inputResolver).generate(input, generableArtifacts,
        targetRootPath, forceOverride, rawModel, progressCallback, generateAnnotation);
  }

  @Override
  public GenerationReportTo generate(Object input, GenerableArtifact generableArtifact, Path targetRootPath) {

    return generate(input, generableArtifact, targetRootPath, false, (String taskName, Integer progress) -> {
    });
  }

  @Override
  public GenerationReportTo generate(Object input, GenerableArtifact generableArtifact, Path targetRootPath,
      boolean forceOverride) {

    return generate(input, generableArtifact, targetRootPath, forceOverride, (taskname, progress) -> {
    });
  }

  @Override
  public GenerationReportTo generate(Object input, GenerableArtifact generableArtifact, Path targetRootPath,
      boolean forceOverride, BiConsumer<String, Integer> progressCallback) {

    return generate(input, generableArtifact, targetRootPath, forceOverride, null, progressCallback);
  }

  @Override
  public GenerationReportTo generate(Object input, GenerableArtifact generableArtifact, Path targetRootPath,
      boolean forceOverride, Map<String, Object> rawModel, BiConsumer<String, Integer> progressCallback) {

    return generate(input, generableArtifact, targetRootPath, forceOverride, rawModel, progressCallback, false);
  }

  @Override
  public GenerationReportTo generate(Object input, GenerableArtifact generableArtifact, Path targetRootPath,
      boolean forceOverride, Map<String, Object> rawModel, BiConsumer<String, Integer> progressCallback,
      boolean generateAnnotation) {

    Objects.requireNonNull(input, "Input");
    Objects.requireNonNull(generableArtifact, "Artifact to be generated");
    Objects.requireNonNull(targetRootPath, "targetRootPath");
    return new GenerationProcessorImpl(this.configurationHolder, this.inputResolver).generate(input,
        Lists.newArrayList(generableArtifact), targetRootPath, forceOverride, rawModel, progressCallback,
        generateAnnotation);
  }

  @Override
  public ModelBuilder getModelBuilder(Object input) {

    List<String> matchingTriggerIds = getMatchingTriggerIds(input);
    // Just take the first trigger as all trigger should have the same input reader. See javadoc.
    Trigger trigger = this.configurationHolder.readContextConfiguration().getTrigger(matchingTriggerIds.get(0));
    return new ModelBuilderImpl(input, trigger);
  }

  @Override
  public ModelBuilder getModelBuilder(Object generatorInput, String triggerId) {

    Trigger trigger = this.configurationHolder.readContextConfiguration().getTrigger(triggerId);
    if (trigger == null) {
      throw new IllegalArgumentException("Unknown Trigger with id '" + triggerId + "'.");
    }
    return new ModelBuilderImpl(generatorInput, trigger);
  }

  @Override
  public boolean combinesMultipleInputs(Object input) {

    return this.inputInterpreter.combinesMultipleInputs(input);
  }

  @Override
  public List<Object> resolveContainers(Object input) {

    return this.inputInterpreter.resolveContainers(input);
  }

  @Override
  public Object read(Path path, Charset inputCharset, Object... additionalArguments) throws InputReaderException {

    return this.inputInterpreter.read(path, inputCharset, additionalArguments);
  }

  @Override
  public List<IncrementTo> getMatchingIncrements(Object matcherInput) throws InvalidConfigurationException {

    return this.configurationInterpreter.getMatchingIncrements(matcherInput);
  }

  @Override
  public List<TemplateTo> getMatchingTemplates(Object matcherInput) throws InvalidConfigurationException {

    return this.configurationInterpreter.getMatchingTemplates(matcherInput);
  }

  @Override
  public List<String> getMatchingTriggerIds(Object matcherInput) {

    return this.configurationInterpreter.getMatchingTriggerIds(matcherInput);
  }

  @Override
  public Path resolveTemplateDestinationPath(Path targetRootPath, TemplateTo template, Object input) {

    return this.configurationInterpreter.resolveTemplateDestinationPath(targetRootPath, template, input);
  }

}

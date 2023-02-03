package com.devonfw.cobigen.impl.model;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.extension.MatcherInterpreter;
import com.devonfw.cobigen.api.extension.ModelBuilder;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.impl.config.entity.Template;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.config.entity.VariableAssignment;
import com.devonfw.cobigen.impl.config.entity.Variables;
import com.devonfw.cobigen.impl.config.reader.CobiGenPropertiesReader;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.validator.InputValidator;
import com.google.common.collect.Maps;

/**
 * The {@link ModelBuilderImpl} is responsible to create the object models for a given object. Therefore, it uses
 * {@link TriggerInterpreter} plug-in extensions to query available {@link InputReader}s and {@link MatcherInterpreter}s
 */
public class ModelBuilderImpl implements ModelBuilder {

  /** Namespace of the context and CobiGen variables retrieved by cobigen-core */
  public static final String NS_VARIABLES = "variables";

  /** Input object for which a new object model should be created */
  private Object generatorInput;

  /** Trigger, which has been activated for the given input */
  private Trigger trigger;

  /**
   * Creates a new {@link ModelBuilderImpl} instance for the given properties
   *
   * @param generatorInput object for which a new object model should be created
   * @param trigger which has been activated for the given input
   */
  public ModelBuilderImpl(Object generatorInput, Trigger trigger) {

    if (generatorInput == null || trigger == null || trigger.getMatcher() == null) {
      throw new IllegalArgumentException(
          "Cannot create Model from input == null || trigger == null || trigger.getMatcher() == null");
    }
    this.generatorInput = generatorInput;
    this.trigger = trigger;
  }

  /**
   * Creates a new model by trying to retrieve the corresponding {@link TriggerInterpreter} from the plug-in registry
   *
   * @return the created model
   * @throws InvalidConfigurationException if there are {@link VariableAssignment}s, which could not be resolved
   */
  @Override
  public Map<String, Object> createModel() throws InvalidConfigurationException {

    TriggerInterpreter triggerInterpreter = PluginRegistry.getTriggerInterpreter(this.trigger.getType());
    InputValidator.validateTriggerInterpreter(triggerInterpreter, this.trigger);
    return createModel(triggerInterpreter);
  }

  /**
   * Creates a new model by using the given {@link TriggerInterpreter} to retrieve the {@link InputReader} and
   * {@link MatcherInterpreter} from.
   *
   * @param triggerInterpreter to be used
   * @return the created model
   * @throws InvalidConfigurationException if there are {@link VariableAssignment}s, which could not be resolved
   */
  @Override
  public Map<String, Object> createModel(TriggerInterpreter triggerInterpreter) throws InvalidConfigurationException {

    Map<String, Object> model = new HashMap<>(triggerInterpreter.getInputReader().createModel(this.generatorInput));
    return model;
  }

  /**
   * Enriches the model by the context variables of the trigger.
   *
   * @param model to be enriched
   * @param triggerInterpreter {@link TriggerInterpreter} to resolve the variables
   * @param template the internal {@link Template} representation
   * @param targetRootPath root path template destinations should be resolved against
   * @param report is getting filled as side-effect
   * @return the adapted model reference.
   */
  public Map<String, Object> enrichByContextVariables(Map<String, Object> model, TriggerInterpreter triggerInterpreter,
      Template template, Path targetRootPath, GenerationReportTo report) {

    Map<String, Object> variables = Maps.newHashMap();
    Map<String, Object> contextVariables = new ContextVariableResolver(this.generatorInput, this.trigger)
        .resolveVariables(triggerInterpreter, report).asMap();
    Map<String, Object> templateProperties = template.getVariables().asMap();
    Properties targetCobiGenProperties = CobiGenPropertiesReader.load(targetRootPath);
    // if there are properties overriding each other, throw an exception for better usability.
    // This is most probably a not intended mechanism such that we simply will not support it.
    Set<String> intersection = new HashSet<>(contextVariables.keySet());
    intersection.retainAll(templateProperties.keySet());
    Set<String> intersection2 = new HashSet<>(contextVariables.keySet());
    intersection2.retainAll(targetCobiGenProperties.keySet());
    if (!intersection.isEmpty() || !intersection2.isEmpty()) {
      throw new CobiGenRuntimeException("There are conflicting variables coming from the context configuration "
          + "as well as coming from the " + ConfigurationConstants.COBIGEN_PROPERTIES + " file. "
          + "This is most probably an unintended behavior and thus is not supported. The following variables are "
          + "declared twice (once in " + ConfigurationConstants.CONTEXT_CONFIG_FILENAME + " and once in "
          + ConfigurationConstants.COBIGEN_PROPERTIES + " file): " + Arrays.toString(intersection.toArray()));
    }
    variables.putAll(contextVariables);
    variables.putAll(templateProperties);
    variables.putAll(new Variables(targetCobiGenProperties).asMap());
    model.put(NS_VARIABLES, variables);
    return model;
  }

}

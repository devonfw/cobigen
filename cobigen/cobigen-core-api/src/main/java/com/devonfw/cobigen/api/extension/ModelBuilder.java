package com.devonfw.cobigen.api.extension;

import java.util.Map;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.annotation.ExceptionFacade;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.to.VariableAssignmentTo;

/**
 * The {@link ModelBuilder} is an interface for accessing the internal model builder instance. Such instance can be
 * retrieved by calling {@link CobiGen#getModelBuilder(Object, String)}
 */
@ExceptionFacade
public interface ModelBuilder {

  /**
   * Creates the model by retrieving the necessary {@link TriggerInterpreter} from the plug-in registry
   *
   * @return the created object model (not null)
   * @throws InvalidConfigurationException if no corresponding {@link TriggerInterpreter} could be found or one of the
   *         {@link VariableAssignmentTo}s could not be resolved
   */
  public Map<String, Object> createModel() throws InvalidConfigurationException;

  /**
   * Creates the model using the given {@link TriggerInterpreter}
   *
   * @param triggerInterpreter to be used for model creation and variable resolving
   * @return the created object model (not null)
   * @throws InvalidConfigurationException if one of the {@link VariableAssignmentTo}s could not be resolved
   */
  public Map<String, Object> createModel(TriggerInterpreter triggerInterpreter) throws InvalidConfigurationException;
}

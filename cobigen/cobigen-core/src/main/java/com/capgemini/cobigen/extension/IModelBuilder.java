package com.capgemini.cobigen.extension;

import java.util.Map;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.config.entity.VariableAssignment;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;

/**
 * The {@link IModelBuilder} is an interface for accessing the internal model builder instance. Such instance
 * can be retrieved by calling {@link CobiGen#getModelBuilder(Object, String)}
 * @author mbrunnli (09.04.2014)
 */
public interface IModelBuilder {

    /**
     * Creates the model by retrieving the necessary {@link ITriggerInterpreter} from the plug-in registry
     * @return the created object model (not null)
     * @throws InvalidConfigurationException
     *             if no corresponding {@link ITriggerInterpreter} could be found or one of the
     *             {@link VariableAssignment}s could not be resolved
     * @author mbrunnli (14.04.2014)
     */
    public Map<String, Object> createModel() throws InvalidConfigurationException;

    /**
     * Creates the model using the given {@link ITriggerInterpreter}
     * @param triggerInterpreter
     *            to be used for model creation and variable resolving
     * @return the created object model (not null)
     * @throws InvalidConfigurationException
     *             if one of the {@link VariableAssignment}s could not be resolved
     * @author mbrunnli (14.04.2014)
     */
    public Map<String, Object> createModel(ITriggerInterpreter triggerInterpreter)
        throws InvalidConfigurationException;
}

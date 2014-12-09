package com.capgemini.cobigen.extension;

import java.util.List;

/**
 * This interface should be inherited for all plug-ins to extend the generators logic by additional
 * {@link IMerger}s or {@link ITriggerInterpreter}s.
 * @author mbrunnli (06.04.2014)
 */
public interface IGeneratorPluginActivator {

    /**
     * This function should return all {@link IMerger} implementations, which should be provided by this
     * plug-in implementation
     * @return a {@link List} of all {@link IMerger}s, which should be registered (not null)
     * @author mbrunnli (07.04.2014)
     */
    public List<IMerger> bindMerger();

    /**
     * This function should return all {@link ITriggerInterpreter} implementations, which should be provided
     * by this plug-in implementation
     * @return a {@link List} of all {@link ITriggerInterpreter}s, which should be registered (not null)
     * @author mbrunnli (08.04.2014)
     */
    public List<ITriggerInterpreter> bindTriggerInterpreter();

}

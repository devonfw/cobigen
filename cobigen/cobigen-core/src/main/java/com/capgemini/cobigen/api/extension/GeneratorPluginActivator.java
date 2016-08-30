package com.capgemini.cobigen.api.extension;

import java.util.List;

/**
 * This interface should be inherited for all plug-ins to extend the generators logic by additional
 * {@link Merger}s or {@link TriggerInterpreter}s.
 * @author mbrunnli (06.04.2014)
 */
public interface GeneratorPluginActivator {

    /**
     * This function should return all {@link Merger} implementations, which should be provided by this
     * plug-in implementation
     * @return a {@link List} of all {@link Merger}s, which should be registered (not null)
     * @author mbrunnli (07.04.2014)
     */
    public List<Merger> bindMerger();

    /**
     * This function should return all {@link TriggerInterpreter} implementations, which should be provided
     * by this plug-in implementation
     * @return a {@link List} of all {@link TriggerInterpreter}s, which should be registered (not null)
     * @author mbrunnli (08.04.2014)
     */
    public List<TriggerInterpreter> bindTriggerInterpreter();

}

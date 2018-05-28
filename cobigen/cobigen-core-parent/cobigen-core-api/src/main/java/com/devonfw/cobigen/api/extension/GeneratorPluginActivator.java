package com.devonfw.cobigen.api.extension;

import java.util.List;

import com.devonfw.cobigen.api.annotation.ExceptionFacade;

/**
 * This interface should be inherited for all plug-ins to extend the generators logic by additional
 * {@link Merger}s or {@link TriggerInterpreter}s.
 */
@ExceptionFacade
public interface GeneratorPluginActivator {

    /**
     * This function should return all {@link Merger} implementations, which should be provided by this
     * plug-in implementation
     * @return a {@link List} of all {@link Merger}s, which should be registered (not null)
     */
    public List<Merger> bindMerger();

    /**
     * This function should return all {@link TriggerInterpreter} implementations, which should be provided by
     * this plug-in implementation
     * @return a {@link List} of all {@link TriggerInterpreter}s, which should be registered (not null)
     */
    public List<TriggerInterpreter> bindTriggerInterpreter();

}

package com.devonfw.cobigen.api.extension;

import java.nio.file.Path;
import java.util.List;

import com.devonfw.cobigen.api.annotation.ExceptionFacade;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;

/**
 * This interface should be inherited for all plug-ins to extend the generators logic by additional {@link Merger}s or
 * {@link TriggerInterpreter}s.
 */
@ExceptionFacade
public interface GeneratorPluginActivator {

  /**
   * This function should return all {@link Merger} implementations, which should be provided by this plug-in
   * implementation
   *
   * @return a {@link List} of all {@link Merger}s, which should be registered (not null)
   */
  public List<Merger> bindMerger();

  /**
   * This function should be called by a plugin if the path to the template root was changed
   *
   * @param path Path to project root folder
   *
   * @throws NotYetSupportedException if not implemented yet
   */
  default void setProjectRoot(@SuppressWarnings("unused") Path path) {

    // do nothing
  }

  /**
   * This function should return all {@link TriggerInterpreter} implementations, which should be provided by this
   * plug-in implementation
   *
   * @return a {@link List} of all {@link TriggerInterpreter}s, which should be registered (not null)
   */
  public List<TriggerInterpreter> bindTriggerInterpreter();

}

package com.devonfw.cobigen.impl.config.reader.interfaces;

import java.util.List;
import java.util.Map;

import com.devonfw.cobigen.impl.config.entity.Trigger;

/**
 * Interface to implement for every reader that handles triggers
 *
 */
public interface ContextConfigurationInterface {

  /**
   * Reads the configuration, so the triggers can be loaded
   */
  void readConfiguration();

  /**
   * Loads all {@link Trigger}s of the static context into the local representation
   *
   * @return a {@link List} containing all the {@link Trigger}s
   */
  Map<String, Trigger> loadTriggers();

}
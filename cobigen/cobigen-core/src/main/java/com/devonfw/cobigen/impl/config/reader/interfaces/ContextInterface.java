package com.devonfw.cobigen.impl.config.reader.interfaces;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.devonfw.cobigen.impl.config.entity.Trigger;

/**
 * TODO khucklen This type ...
 *
 */
public interface ContextInterface {

  /**
   * @return the path of the context file
   */
  Path getContextRoot();

  /**
   * @return the list of the context files
   */
  List<Path> getContextFiles();

  /**
   * Loads all {@link Trigger}s of the static context into the local representation
   *
   * @return a {@link List} containing all the {@link Trigger}s
   */
  Map<String, Trigger> loadTriggers();

}
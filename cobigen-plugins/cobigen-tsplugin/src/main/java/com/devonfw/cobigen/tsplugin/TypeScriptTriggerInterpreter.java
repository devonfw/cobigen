package com.devonfw.cobigen.tsplugin;

import com.devonfw.cobigen.api.annotation.ReaderPriority;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.extension.MatcherInterpreter;
import com.devonfw.cobigen.api.extension.Priority;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.api.externalprocess.ExternalProcess;
import com.devonfw.cobigen.tsplugin.inputreader.TypeScriptInputReader;
import com.devonfw.cobigen.tsplugin.matcher.TypeScriptMatcher;

/**
 * {@link TriggerInterpreter} implementation of a Swagger Interpreter
 */
@ReaderPriority(Priority.LOW)
public class TypeScriptTriggerInterpreter implements TriggerInterpreter {

  /**
   * {@link TriggerInterpreter} type to be registered
   */
  private String type;

  /** The external process for the plugin */
  private ExternalProcess externalProcess;

  /**
   * Creates a new Swagger Interpreter
   *
   * @param externalProcess the external process instance for this plugin
   * @param type to be registered
   */
  public TypeScriptTriggerInterpreter(ExternalProcess externalProcess, String type) {

    this.externalProcess = externalProcess;
    this.type = type;
  }

  @Override
  public String getType() {

    return this.type;
  }

  @Override
  public InputReader getInputReader() {

    return new TypeScriptInputReader(this.externalProcess);
  }

  @Override
  public MatcherInterpreter getMatcher() {

    return new TypeScriptMatcher();
  }

}

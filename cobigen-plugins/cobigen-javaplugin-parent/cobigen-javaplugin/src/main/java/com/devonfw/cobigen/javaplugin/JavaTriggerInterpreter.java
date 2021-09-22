package com.devonfw.cobigen.javaplugin;

import com.devonfw.cobigen.api.annotation.ReaderPriority;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.extension.MatcherInterpreter;
import com.devonfw.cobigen.api.extension.Priority;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.javaplugin.inputreader.JavaInputReader;
import com.devonfw.cobigen.javaplugin.matcher.JavaMatcher;

/** {@link TriggerInterpreter} implementation of a Java Interpreter */
@ReaderPriority(Priority.LOW)
public class JavaTriggerInterpreter implements TriggerInterpreter {

  /** {@link TriggerInterpreter} type to be registered */
  public String type;

  /**
   * Creates a new Java Interpreter
   *
   * @param type to be registered
   */
  public JavaTriggerInterpreter(String type) {

    this.type = type;
  }

  @Override
  public String getType() {

    return this.type;
  }

  @Override
  public InputReader getInputReader() {

    return new JavaInputReader();
  }

  @Override
  public MatcherInterpreter getMatcher() {

    return new JavaMatcher();
  }
}

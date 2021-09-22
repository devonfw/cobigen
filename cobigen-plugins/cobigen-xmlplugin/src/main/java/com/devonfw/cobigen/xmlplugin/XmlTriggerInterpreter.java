package com.devonfw.cobigen.xmlplugin;

import com.devonfw.cobigen.api.annotation.ReaderPriority;
import com.devonfw.cobigen.api.extension.InputReader;
import com.devonfw.cobigen.api.extension.MatcherInterpreter;
import com.devonfw.cobigen.api.extension.Priority;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.xmlplugin.inputreader.XmlInputReader;
import com.devonfw.cobigen.xmlplugin.matcher.XmlMatcher;

/**
 * {@link TriggerInterpreter} implementation of a Xml Interpreter
 */
@ReaderPriority(Priority.LOW)
public class XmlTriggerInterpreter implements TriggerInterpreter {

  /**
   * {@link TriggerInterpreter} type to be registered
   */
  public String type;

  /**
   * creates a new {@link XmlTriggerInterpreter}
   *
   * @param type to be registered
   * @author fkreis (18.11.2014)
   */
  public XmlTriggerInterpreter(String type) {

    super();
    this.type = type;
  }

  @Override
  public String getType() {

    return this.type;
  }

  @Override
  public InputReader getInputReader() {

    return new XmlInputReader();
  }

  @Override
  public MatcherInterpreter getMatcher() {

    return new XmlMatcher();
  }

}

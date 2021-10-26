package com.devonfw.cobigen.textmerger.anchorextension;

/**
 *
 */
public enum MergeStrategy {
  /**
   * Signals that the base text should be overwritten by the patch
   */
  OVERRIDE("override", false),
  /**
   * Signals that the patch should be appended before the base text
   */
  APPENDBEFORE("appendbefore", true),
  /**
   * Signals that the patch should be appended after the base text
   */
  APPENDAFTER("appendafter", true),
  /**
   * Signals that the patch should not be appended
   */
  NOMERGE("nomerge", false),
  /**
   * Signals that the patch should be appended after the base text
   */
  APPEND("append", true),
  /**
   * Signals that a newline should be appended after the patch
   */
  NEWLINE("newline", false),
  /**
   * Mergestrategy that will be defaulted to if there is no valid one given.
   */
  ERROR("", false);

  private final String name;

  private final boolean supportsNewLine;

  /**
   * @param name The name of the merge strategy
   * @param supportsNewLine True if newline works with this strategy, false if not
   */
  private MergeStrategy(String name, boolean supportsNewLine) {

    this.name = name;
    this.supportsNewLine = supportsNewLine;
  }

  public String getName() {

    return this.name;
  }

  public boolean supportsNewLine() {

    return this.supportsNewLine;
  }

}

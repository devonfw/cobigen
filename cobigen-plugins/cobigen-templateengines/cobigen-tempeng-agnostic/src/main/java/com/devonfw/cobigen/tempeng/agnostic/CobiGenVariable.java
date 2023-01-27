package com.devonfw.cobigen.tempeng.agnostic;

import java.util.Locale;

/**
 * A variable of the {@link CobiGenModel}.
 */
public class CobiGenVariable {

  private final String name;

  private final Object value;

  /**
   * The constructor.
   *
   * @param name the {@link #getName() name}.
   * @param value the {@link #getValue() value}.
   */
  public CobiGenVariable(String name, Object value) {

    super();
    this.name = name;
    this.value = value;
  }

  /**
   * @return the name of this {@link CobiGenVariable}.
   */
  public String getName() {

    return this.name;
  }

  /**
   * @return the value of this {@link CobiGenVariable}.
   */
  public Object getValue() {

    return this.value;
  }

  @Override
  public String toString() {

    return this.name + "=" + this.value;
  }

  /**
   * @param key the {@link #getName() variable name} to normalize.
   * @return the normalized name.
   */
  public static String normalize(String key) {

    return key.toLowerCase(Locale.ROOT);
  }

}

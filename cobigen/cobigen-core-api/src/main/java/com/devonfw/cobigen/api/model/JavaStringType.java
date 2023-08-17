package com.devonfw.cobigen.api.model;

import java.lang.reflect.Type;

/**
 * Implementation of {@link Type} as a simple container for its {@link #getTypeName() type name}.
 */
public class JavaStringType implements Type {

  private final String typeName;

  /**
   * The constructor.
   *
   * @param typeName the {@link #getTypeName() type name}.
   */
  public JavaStringType(String typeName) {

    super();
    this.typeName = typeName;
  }

  @Override
  public String getTypeName() {

    return this.typeName;
  }

  @Override
  public String toString() {

    return this.typeName;
  }

}

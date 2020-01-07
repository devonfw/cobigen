package com.devonfw.cobigen.templates.oasp4js.utils.javascript;

/**
 * This enum contains the primitive types of JSON and JavaScript/TypeScript.
 */
public enum JavaScriptBasicType implements JavaScriptType {

  /** The {@link String} type in JavaScript. */
  STRING,

  /** The {@link Number} type in JavaScript. */
  NUMBER,

  /** The {@link Boolean} type in JavaScript. */
  BOOLEAN,

  /** An untyped {@link Object} in JavaScript that may be used for {@link java.util.Map} or similar Java types. */
  OBJECT,

  /**
   * An untyped array in JavaScript that may be used as fallback if {@link #getComponentType() component type} is
   * undefined.
   */
  ARRAY;

  @Override
  public String getSimpleName() {

    if (this == ARRAY) {
      return "any[]";
    }
    return name().toLowerCase();
  }

  @Override
  public String getQualifiedName() {

    return null;
  }

  @Override
  public boolean isPrimitive() {

    return true;
  }

  @Override
  public boolean isArray() {

    return (this == ARRAY);
  }

  @Override
  public JavaScriptType getComponentType() {

    return null;
  }

  @Override
  public String toString() {

    return getSimpleName();
  }

}

package com.devonfw.cobigen.templates.oasp4js.utils.javascript;

/**
 * Interface of a type in JSON and JavaScript (or TypeScript).
 */
public interface JavaScriptType {

  /** Empty {@link JavaScriptType} array. */
  JavaScriptType[] NO_TYPES = new JavaScriptType[0];

  /**
   * @return {@code true} if this type is a primitive {@link JavaScriptBasicType}, {@code false} otherwise.
   */
  boolean isPrimitive();

  /**
   * @return {@code true} if this type is an array (aka {@link java.util.Collection}), {@code false} otherwise.
   */
  boolean isArray();

  /**
   * @return the type of the elements contained in this {@link JavaScriptType} in case this is an {@link #isArray()
   *         array type}, {@code null} otherwise.
   */
  JavaScriptType getComponentType();

  /**
   * @return this {@link JavaScriptType} or the {@link #getComponentType() component type} recursively determined while
   *         this is an {@link #isArray()}.
   */
  default JavaScriptType getNonArrayType() {

    JavaScriptType result = this;
    while (true) {
      JavaScriptType componentType = result.getComponentType();
      if (componentType == null) {
        return result;
      }
      result = componentType;
    }
  }

  /**
   * @return the simple name of this {@link JavaScriptType} such as "string", "number", "boolean", "object", "Date",
   *         etc.
   */
  String getSimpleName();

  /**
   * @return if this is no {@link #isArray() array type} and a non-{@link #isPrimitive() primitive} TypeScript type the
   *         path for an import statement is returned, otherwise {@code null}.
   */
  String getQualifiedName();

  /**
   * @return the generic {@link JavaScriptType}s.
   */
  default JavaScriptType[] getGenericTypes() {

    return NO_TYPES;
  }

}

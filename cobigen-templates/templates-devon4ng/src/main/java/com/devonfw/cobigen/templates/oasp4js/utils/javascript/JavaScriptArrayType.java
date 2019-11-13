package com.devonfw.cobigen.templates.oasp4js.utils.javascript;

import java.util.Objects;

/**
 * Implementation of {@link JavaScriptType} for {@link #isArray() array types}.
 */
public class JavaScriptArrayType extends AbstractJavaScriptType {

  private final JavaScriptType componentType;

  /**
   * The constructor.
   *
   * @param componentType the {@link #getComponentType() component type}.
   */
  public JavaScriptArrayType(JavaScriptType componentType) {
    super();
    this.componentType = componentType;
  }

  @Override
  public boolean isArray() {

    return true;
  }

  @Override
  public JavaScriptType getComponentType() {

    return this.componentType;
  }

  @Override
  public String getSimpleName() {

    return this.componentType.getSimpleName() + "[]";
  }

  @Override
  public String getQualifiedName() {

    return null;
  }

  @Override
  public int hashCode() {

    return Objects.hash(this.componentType);
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    JavaScriptArrayType other = (JavaScriptArrayType) obj;
    if (!Objects.equals(this.componentType, other.componentType)) {
      return false;
    }
    return true;
  }

}

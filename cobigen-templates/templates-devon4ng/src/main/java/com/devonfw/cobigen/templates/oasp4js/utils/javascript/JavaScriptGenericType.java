package com.devonfw.cobigen.templates.oasp4js.utils.javascript;

/**
 * Implementation of {@link JavaScriptType} for a generic {@link JavaScriptComplexType type}.
 */
public class JavaScriptGenericType extends JavaScriptComplexType {

  private final JavaScriptType[] genericTypes;

  /**
   * Der Konstruktor.
   *
   * @param path the path for an import statement (e.g. "adamas-main/lib/adamas/common/datatype/").
   * @param simpleName the {@link #getSimpleName() simple name}.
   * @param genericTypes the {@link #getGenericTypes() generic types}.
   */
  public JavaScriptGenericType(String path, String simpleName, JavaScriptType[] genericTypes) {
    super(path, simpleName);
    this.genericTypes = genericTypes;
  }

  @Override
  public JavaScriptType[] getGenericTypes() {

    return this.genericTypes;
  }

  @Override
  public String toString() {

    StringBuilder buffer = new StringBuilder(super.toString());
    buffer.append('<');
    String separator = "";
    for (JavaScriptType type : this.genericTypes) {
      buffer.append(separator);
      buffer.append(type.getSimpleName());
      separator = ", ";
    }
    buffer.append('>');
    return buffer.toString();
  }

}

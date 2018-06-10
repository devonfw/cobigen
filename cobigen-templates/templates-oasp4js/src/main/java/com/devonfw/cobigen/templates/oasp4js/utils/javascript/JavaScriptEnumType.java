package com.devonfw.cobigen.templates.oasp4js.utils.javascript;

/**
 * Like {@link JavaScriptComplexType} but corresponding to a Java {@link Enum}.
 */
public class JavaScriptEnumType extends JavaScriptComplexType {

  /**
   * Der Konstruktor.
   *
   * @param path the path for an import statement (e.g. "adamas-main/lib/adamas/common/datatype/").
   * @param simpleName the {@link #getSimpleName() simple name}.
   */
  public JavaScriptEnumType(String path, String simpleName) {
    super(path, simpleName);
  }

}

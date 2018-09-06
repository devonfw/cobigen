package com.devonfw.cobigen.templates.oasp4js.utils.javascript;

import java.util.Objects;

/**
 * Implementation of {@link JavaScriptType} for a complex type (TypeScript, ES2016+, etc.).
 */
public class JavaScriptComplexType extends AbstractJavaScriptType {

  static final String PATH_OASP_COMMON = "oasp/common/";

  static final String PATH_OASP_DATATYPE = PATH_OASP_COMMON + "datatype/";

  static final String PATH_OASP_TO = PATH_OASP_COMMON + "to/";

  /** {@link JavaScriptType} for {@link java.time.LocalDateTime date/time}. */
  public static final JavaScriptComplexType DATE = new JavaScriptComplexType(null, "Date");

  /** {@link JavaScriptType} for {@link java.time.YearMonth}. */
  public static final JavaScriptComplexType YEAR_MONTH =
      new JavaScriptComplexType(JavaScriptComplexType.PATH_OASP_DATATYPE, "YearMonth");

  /** {@link JavaScriptType} for {@link io.oasp.module.basic.common.api.to.AbstractTo}. */
  public static final JavaScriptComplexType ABSTRACT_TO = new JavaScriptComplexType(PATH_OASP_TO, "AbstractTo");

  /** {@link JavaScriptType} for {@link io.oasp.module.basic.common.api.to.AbstractEto}. */
  public static final JavaScriptComplexType ABSTRACT_ETO = new JavaScriptComplexType(PATH_OASP_TO, "AbstractEto");

  /** {@link JavaScriptType} for {@link io.oasp.module.basic.common.api.to.AbstractCto}. */
  public static final JavaScriptComplexType ABSTRACT_CTO = new JavaScriptComplexType(PATH_OASP_TO, "AbstractCto");

  private final String path;

  private final String simpleName;

  /**
   * Der Konstruktor.
   *
   * @param path the path for an import statement (e.g. "adamas-main/lib/adamas/common/datatype/").
   * @param simpleName the {@link #getSimpleName() simple name}.
   */
  public JavaScriptComplexType(String path, String simpleName) {
    super();
    if ((path == null) || path.endsWith("/")) {
      this.path = path;
    } else {
      this.path = path + '/';
    }
    this.simpleName = simpleName;
  }

  @Override
  public boolean isPrimitive() {

    return false;
  }

  @Override
  public boolean isArray() {

    return false;
  }

  @Override
  public JavaScriptType getComponentType() {

    return null;
  }

  @Override
  public String getSimpleName() {

    return this.simpleName;
  }

  @Override
  public String getQualifiedName() {

    if (this.path == null) {
      return null;
    }
    return this.path + this.simpleName;
  }

  @Override
  public int hashCode() {

    return Objects.hash(this.path, this.simpleName);
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    JavaScriptComplexType other = (JavaScriptComplexType) obj;
    if (!Objects.equals(this.path, other.path)) {
      return false;
    }
    if (!Objects.equals(this.simpleName, other.simpleName)) {
      return false;
    }
    return true;
  }

}

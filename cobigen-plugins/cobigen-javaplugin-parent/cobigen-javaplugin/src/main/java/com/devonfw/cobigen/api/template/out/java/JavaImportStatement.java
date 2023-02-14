package com.devonfw.cobigen.api.template.out.java;

import java.util.Objects;

import com.devonfw.cobigen.api.template.out.ImportStatement;
import com.devonfw.cobigen.api.template.out.QualifiedName;

/**
 * Implementation of {@link IllegalStateException} for Java.
 */
public class JavaImportStatement implements ImportStatement {

  private final QualifiedName type;

  private final String staticReference;

  /**
   * The constructor.
   *
   * @param type the {@link QualifiedName} of the type to import.
   */
  public JavaImportStatement(QualifiedName type) {

    this(type, null);
  }

  /**
   * The constructor.
   *
   * @param type the {@link QualifiedName} of the type to import.
   * @param staticReference the optional static reference (method, constant, etc.) to import from the specified type.
   */
  public JavaImportStatement(QualifiedName type, String staticReference) {

    super();
    this.type = type;
    this.staticReference = staticReference;
  }

  @Override
  public int getKeyCount() {

    return 1;
  }

  @Override
  public String getKey(int i) {

    if (i == 0) {
      if (this.staticReference == null) {
        return this.type.getSimpleName();
      }
      return this.staticReference;
    }
    return null;
  }

  @Override
  public String getTarget() {

    return this.type.getQualifiedName();
  }

  @Override
  public String getStaticReference() {

    return this.staticReference;
  }

  @Override
  public int hashCode() {

    return Objects.hash(this.staticReference, this.type);
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    } else if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    JavaImportStatement other = (JavaImportStatement) obj;
    return Objects.equals(this.staticReference, other.staticReference) && Objects.equals(this.type, other.type);
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder("import ");
    if (isStatic()) {
      sb.append("static ");
    }
    sb.append(getTarget());
    if (this.staticReference != null) {
      sb.append('.');
      sb.append(this.staticReference);
    }
    sb.append(";");
    return sb.toString();
  }

}

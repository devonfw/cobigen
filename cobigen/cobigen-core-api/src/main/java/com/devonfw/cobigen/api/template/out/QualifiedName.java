package com.devonfw.cobigen.api.template.out;

import java.util.Objects;

/**
 * A qualified name with {@link #getNamespace() namespace}, {@link #getSimpleName() simple name} and its
 * {@link #getQualifiedName() qualified name}.
 */
public class QualifiedName {

  private final String namespace;

  private final String simpleName;

  private final String qualifiedName;

  /**
   * The constructor.
   *
   * @param namespace the {@link #getNamespace() namespace}.
   * @param simpleName the {@link #getSimpleName() simple name}.
   * @param qualifiedName the {@link #getQualifiedName() qualified name}.
   */
  public QualifiedName(String namespace, String simpleName, String qualifiedName) {

    super();
    this.namespace = namespace;
    this.simpleName = simpleName;
    this.qualifiedName = qualifiedName;
  }

  /**
   * @return the namespace (e.g. package name or XML namespace URI).
   */
  public String getNamespace() {

    return this.namespace;
  }

  /**
   * @return the simple name (also called local name or unqualified name).
   */
  public String getSimpleName() {

    return this.simpleName;
  }

  /**
   * @return the qualified name.
   */
  public String getQualifiedName() {

    return this.qualifiedName;
  }

  @Override
  public int hashCode() {

    return Objects.hash(this.namespace, this.qualifiedName, this.simpleName);
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    } else if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    QualifiedName other = (QualifiedName) obj;
    return Objects.equals(this.namespace, other.namespace) && Objects.equals(this.qualifiedName, other.qualifiedName)
        && Objects.equals(this.simpleName, other.simpleName);
  }

  @Override
  public String toString() {

    return this.qualifiedName;
  }

  /**
   * Factory method to create {@link QualifiedName} for Java like naming.
   *
   * @param qname the {@link #getQualifiedName() qualified name}.
   * @return the parsed {@link QualifiedName}.
   */
  public static QualifiedName of(String qname) {

    return of(qname, ".");
  }

  /**
   * Factory method to create {@link QualifiedName}.
   *
   * @param qname the {@link #getQualifiedName() qualified name}.
   * @param separator the seperator {@link String} (e.g. "." for Java or ":" for XML).
   * @return the parsed {@link QualifiedName}.
   */
  public static QualifiedName of(String qname, String separator) {

    String namespace;
    String simpleName;
    int lastSeparator = qname.lastIndexOf(separator);
    if (lastSeparator > 0) {
      namespace = qname.substring(0, lastSeparator);
      simpleName = qname.substring(lastSeparator + separator.length());
    } else {
      namespace = "";
      simpleName = qname;
    }
    return new QualifiedName(namespace, simpleName, qname);
  }

  /**
   * Factory method to create {@link QualifiedName}.
   *
   * @param namespace the {@link #getNamespace() namespace}.
   * @param simpleName the {@link #getSimpleName() simple name}.
   * @param separator the seperator character (e.g. '.' for Java).
   * @return the composed {@link QualifiedName}.
   */
  public static QualifiedName of(String namespace, String simpleName, char separator) {

    String qname = namespace + separator + simpleName;
    return new QualifiedName(namespace, simpleName, qname);
  }

}

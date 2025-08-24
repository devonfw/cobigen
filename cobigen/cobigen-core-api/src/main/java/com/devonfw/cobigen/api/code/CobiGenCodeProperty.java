package com.devonfw.cobigen.api.code;

import java.util.Objects;

/**
 * Simple container for property of a code-type.
 */
public final class CobiGenCodeProperty {

  private final String name;

  private final String type;

  private final String description;

  /**
   * The constructor.
   *
   * @param name the {@link #getName() property name}.
   * @param type the {@link #getType() qualified name of the property type}.
   * @param description the {@link #getDescription() description}.
   */
  public CobiGenCodeProperty(String name, String type, String description) {

    super();
    this.name = name;
    this.type = type;
    this.description = description;
  }

  /**
   * @return name the name of the property (e.g. "id", "firstName", etc.).
   * @see java.lang.reflect.Field#getName()
   */
  public String getName() {

    return this.name;
  }

  /**
   * @return type the {@link com.devonfw.cobigen.api.template.out.QualifiedName#getQualifiedName() qualified name} of
   *         the property type.
   * @see java.lang.reflect.Field#getType()
   */
  public String getType() {

    return this.type;
  }

  /**
   * @return description the optional property description for API documentation. May be {@code null} what will suppress
   *         generation of API documentation (e.g. JavaDoc).
   */
  public String getDescription() {

    return this.description;
  }

  @Override
  public int hashCode() {

    return Objects.hash(this.description, this.name, this.type);
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    } else if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    CobiGenCodeProperty other = (CobiGenCodeProperty) obj;
    return Objects.equals(this.description, other.description) && Objects.equals(this.name, other.name)
        && Objects.equals(this.type, other.type);
  }

  @Override
  public String toString() {

    return this.name + ":" + this.type + "(" + this.description + ")";
  }

}

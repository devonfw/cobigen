package com.devonfw.cobigen.api.template.out;

import com.devonfw.cobigen.api.code.CobiGenCodeBlock;

/**
 * Interface for the output artifact currently generated by CobiGen.<br>
 * <b>ATTENTION:</b> The output artifact can be of any type. Methods offered by this interface may only be supported
 * depending on that type. So when data or text is generated (XML, JSON, YAML, AsciiDoc, TXT, etc.) there are no import
 * statements and hence methods like {@link #addImport(String)} will fail.
 */
public interface CobiGenOutput {

  /** {@link #getCategory() Category} for a regular class. */
  String CATEGORY_CLASS = "class";

  /** {@link #getCategory() Category} for a an interface. */
  String CATEGORY_INTERFACE = "interface";

  /** {@link #getCategory() Category} for a an enumeration type. */
  String CATEGORY_ENUMERATION = "enumeration";

  /** {@link #getCategory() Category} for a a record type. */
  String CATEGORY_RECORD = "record";

  /** {@link #getCategory() Category} for a an annotation type. */
  String CATEGORY_ANNOTATION = "annotation";

  /** {@link #getCategory() Category} if undefined (e.g. textual output). */
  String CATEGORY_UNDEFINED = "undefined";

  /**
   * @return the resolved filename of the generated output.
   */
  String getFilename();

  /**
   * @return the category of the generated output type.
   */
  default String getCategory() {

    return CATEGORY_UNDEFINED;
  }

  /**
   * @return {@code true} if {@link #getCategory() category} is {@link CobiGenOutput#CATEGORY_INTERFACE interface}.
   */
  default boolean isInterface() {

    return CATEGORY_INTERFACE.equals(getCategory());
  }

  /**
   * @return {@code true} if {@link #getCategory() category} is {@link CobiGenOutput#CATEGORY_RECORD record}.
   */
  default boolean isRecord() {

    return CATEGORY_RECORD.equals(getCategory());
  }

  /**
   * @return {@code true} if {@link #getCategory() category} is {@link CobiGenOutput#CATEGORY_ENUMERATION enumeration}.
   */
  default boolean isEnumeration() {

    return CATEGORY_ENUMERATION.equals(getCategory());
  }

  /**
   * @return {@code true} if {@link #getCategory() category} is {@link CobiGenOutput#CATEGORY_ANNOTATION annotation}.
   */
  default boolean isAnnotation() {

    return CATEGORY_ANNOTATION.equals(getCategory());
  }

  /**
   * @return {@code true} if {@link #getCategory() category} is {@link CobiGenOutput#CATEGORY_CLASS class}.
   */
  default boolean isClass() {

    return CATEGORY_CLASS.equals(getCategory());
  }

  /**
   * @return the {@link CobiGenCodeBlock} with the source-code of this output.
   */
  CobiGenCodeBlock getCode();

  /**
   * @param name the {@link Class#getSimpleName() simple name} of the type to check.
   * @return {@code true} if the specified type name is imported, {@code false} otherwise.
   */
  default boolean hasImport(String name) {

    return getImport(name) != null;
  }

  /**
   * @param name the {@link QualifiedName#getSimpleName() simple name} of the type to resolve.
   * @return the {@link ImportStatement} for the given {@link QualifiedName#getSimpleName() simple name} or {@code null}
   *         if no such import exists.
   */
  ImportStatement getImport(String name);

  /**
   * @param qualifiedName the fully qualified name.
   * @return {@code true} if the {@link ImportStatement} has been added, {@code false} otherwise (it was already present
   *         before).
   * @throws UnsupportedOperationException if the import is not supported.
   */
  boolean addImport(String qualifiedName);

  /**
   * @param qualifiedName the {@link QualifiedName}.
   * @return {@code true} if the {@link ImportStatement} has been added, {@code false} otherwise (it was already present
   *         before).
   * @throws UnsupportedOperationException if the import is not supported.
   */
  boolean addImport(QualifiedName qualifiedName);

  /**
   * @param importStatement the {@link ImportStatement} to add.
   * @return {@code true} if the {@link ImportStatement} has been added, {@code false} otherwise (it was already present
   *         before).
   * @throws UnsupportedOperationException if the import is not supported.
   */
  boolean addImport(ImportStatement importStatement);

  /**
   * @param name the name of the property (field).
   * @param qualifiedTypeName the {@link QualifiedName} of the property type as {@link String}.
   * @param description the description of the property used for documentation generation. Simply provide {@code name}
   *        if unknown. May also be {@code null} to suppress documentation generation.
   */
  void addProperty(String name, String qualifiedTypeName, String description);

  /**
   * @param name the name of the property (field).
   * @param type the {@link QualifiedName} of the property type.
   * @param description the description of the property used for documentation generation. Simply provide {@code name}
   *        if unknown. May also be {@code null} to suppress documentation generation.
   */
  void addProperty(String name, QualifiedName type, String description);

  /**
   * @param name the name of the property (field).
   * @param type the {@link QualifiedName} of the property type.
   * @param description the description of the property used for documentation generation. Simply provide {@code name}
   *        if unknown. May also be {@code null} to suppress documentation generation.
   * @param addImport - {@code true} to automatically {@link #addImport(String) add an import}, {@code false} otherwise.
   * @param addField - {@code true} to automatically add a field declaration (will still be omitted in interface),
   *        {@code false} otherwise.
   * @param addGetter - {@code true} to automatically add a getter method.
   * @param addSetter - {@code true} to automatically add a setter method.
   */
  void addProperty(String name, QualifiedName type, String description, boolean addImport, boolean addField,
      boolean addGetter, boolean addSetter);

}
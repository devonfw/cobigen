package com.devonfw.cobigen.api.template.out;

/**
 * Interface for an import statement of whatever programming language.
 */
public interface ImportStatement {

  /**
   * @return {@code true} in case of a static import that imports an element of a type rather than the type itself,
   *         {@code false} otherwise.
   */
  default boolean isStatic() {

    return getStaticReference() != null;
  }

  /**
   * @return the number of {@link #getKey(int) keys}. Will always be {@code 1} for Java. E.g. in TypeScript you can
   *         import multiple items in a single import what will result in a higher count.
   */
  int getKeyCount();

  /**
   * @return the first {@link #getKey(int) key} to import.
   */
  default String getKey() {

    return getKey(0);
  }

  /**
   * @param i the index of the requested key to import. In Java this always has to be zero.
   * @return the key to import for the given index. Will be the {@link Class#getSimpleName() simple name} in Java.
   */
  String getKey(int i);

  /**
   * @return the target that is imported. Will be the {@link Class#getName() qualified name} in case of Java. E.g. in
   *         TypeScript it will be the quoted path to the imported file.
   */
  String getTarget();

  /**
   * @return the optional reference on the {@link #getTarget() target} in case of a {@link #isStatic() static import}.
   *         So in that case this will be the method or constant to import. Will be {@code null} if not a
   *         {@link #isStatic() static import}.
   */
  default String getStaticReference() {

    return null;
  }

  /**
   * @return the optional alias. E.g. for the TypeScript statement "import * as shapes from './shapes';" this will
   *         return "shapes".
   */
  default String getAlias() {

    return null;
  }

}

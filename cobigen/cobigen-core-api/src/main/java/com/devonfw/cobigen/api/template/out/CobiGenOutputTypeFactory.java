package com.devonfw.cobigen.api.template.out;

/**
 * Interface for a factory to {@link #create(String) create} the {@link CobiGenOutput} for a specific {@link #getType()
 * type}.
 */
public interface CobiGenOutputTypeFactory {

  /**
   * @return the default type (extension in lower-case excluding the dot like "java"). May only be {@code null} for the
   *         fallback implementation that is build-in CobiGen.
   */
  String getType();

  /**
   * @param extension the file extension in lower-case excluding the dot (e.g. "java", "ts", "tsx", "cs").
   * @return {@code true} if this {@link CobiGenOutputTypeFactory} is responsible for the given {@code extension},
   *         {@code false} otherwise.
   */
  default boolean isResponsible(String extension) {

    if (extension.equals(getType())) {
      return true;
    }
    return false;
  }

  /**
   * @param filename the {@link CobiGenOutput#getFilename() filename}.
   * @return the newly created {@link CobiGenOutput}.
   */
  CobiGenOutput create(String filename);

}

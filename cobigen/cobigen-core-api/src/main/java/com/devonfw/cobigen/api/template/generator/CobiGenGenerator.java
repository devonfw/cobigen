package com.devonfw.cobigen.api.template.generator;

import java.io.IOException;

import com.devonfw.cobigen.api.model.CobiGenModel;

/**
 * Interface for a code generator of a specific aspect. E.g. generation of getters and setters.
 */
public interface CobiGenGenerator {

  /**
   * @param model the {@link CobiGenModel} with the variables and metadata of the generation.
   * @return the generated code as {@link String}.
   */
  default String generate(CobiGenModel model) {

    try {
      StringBuilder sb = new StringBuilder();
      generate(model, sb);
      return sb.toString();
    } catch (IOException e) {
      throw new IllegalStateException("Generator produced I/O exception.", e);
    }
  }

  /**
   * @param model the {@link CobiGenModel} with the variables and metadata of the generation.
   * @param code the {@link Appendable} where to {@link Appendable#append(CharSequence) write} the generated code to.
   * @throws IOException in case of an I/O error.
   */
  void generate(CobiGenModel model, Appendable code) throws IOException;

}

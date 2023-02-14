package com.devonfw.cobigen.api.template.generator;

import java.io.IOException;
import java.lang.reflect.Field;

import com.devonfw.cobigen.api.model.CobiGenModel;

/**
 * Implementation of {@link CobiGenGenerator} to generate setter methods from the fields.
 */
public class CobiGenGeneratorJavaTypeSetters extends CobiGenGeneratorJavaType {

  @Override
  protected void generate(Field field, CobiGenModel model, Appendable code) throws IOException {

    generateSetter(field, model, code);
  }

}

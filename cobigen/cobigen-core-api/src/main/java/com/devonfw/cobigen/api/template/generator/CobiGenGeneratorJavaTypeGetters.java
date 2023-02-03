package com.devonfw.cobigen.api.template.generator;

import java.io.IOException;
import java.lang.reflect.Field;

import com.devonfw.cobigen.api.model.CobiGenModel;

/**
 * Implementation of {@link CobiGenGenerator} to generate getter methods from the fields.
 */
public class CobiGenGeneratorJavaTypeGetters extends CobiGenGeneratorJavaType {

  @Override
  protected void generate(Field field, CobiGenModel model, Appendable code) throws IOException {

    generateGetter(field, model, code);
  }

}

package com.devonfw.cobigen.api.template.provider;

import java.lang.reflect.Field;

import com.devonfw.cobigen.api.model.CobiGenModel;
import com.devonfw.cobigen.api.model.CobiGenModelDefault;
import com.devonfw.cobigen.api.model.CobiGenVariableDefinitions;

/**
 * {@link CobiGenModel} for a Java {@link Field}.
 */
public class JavaFieldModel extends CobiGenModelDefault {

  /**
   * The constructor.
   */
  public JavaFieldModel(Field field, CobiGenModel parent) {

    super(parent);
    put(CobiGenVariableDefinitions.FIELD_NAME, field.getName());
    put(CobiGenVariableDefinitions.FIELD_TYPE, field.getType());
  }

}

package com.devonfw.cobigen.api.template.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.devonfw.cobigen.api.code.CobiGenCodeProperty;
import com.devonfw.cobigen.api.model.CobiGenModel;
import com.devonfw.cobigen.api.model.CobiGenVariableDefinitions;
import com.devonfw.cobigen.api.template.out.CobiGenOutput;

/**
 * {@link CobiGenOutputGenerator} to generate properties of an ETO.
 */
public class CobiGenGenertorJavaTypeProperties extends CobiGenOutputGenerator {

  @Override
  protected void doGenerate(CobiGenOutput out, CobiGenModel model) {

    Class<?> type = CobiGenVariableDefinitions.JAVA_TYPE.getValue(model);
    for (Field field : type.getDeclaredFields()) {
      generate(field, out, model);
    }
  }

  /**
   * @param field the {@link Field} to generate.
   * @param out the {@link CobiGenOutput} where to generate the code.
   * @param model the {@link CobiGenModel}. Typically not needed but available for complex cases.
   */
  protected void generate(Field field, CobiGenOutput out, CobiGenModel model) {

    CobiGenCodeProperty property = map(field, out, model);
    if (property != null) {
      out.addProperty(property.getName(), property.getType(), property.getDescription());
    }
  }

  /**
   * @param field the {@link Field} to map.
   * @param out the {@link CobiGenOutput}. Typically not needed but available for complex cases. If used here then only
   *        to read information like imports but never write in this method.
   * @param model the {@link CobiGenModel}. Typically not needed but available for complex cases.
   * @return the mapped {@link CobiGenCodeProperty} or {@code null} to filter and ignore the given {@link Field}.
   */
  protected CobiGenCodeProperty map(Field field, CobiGenOutput out, CobiGenModel model) {

    if (Modifier.isStatic(field.getModifiers())) {
      return null; // ignore static fields.
    }
    String name = field.getName();
    String description = null;
    if (out.isInterface()) {
      description = "the " + name; // kind of hack, only generate JavaDoc in case of interface
    }
    return new CobiGenCodeProperty(name, field.getType().getName(), description);
  }

}

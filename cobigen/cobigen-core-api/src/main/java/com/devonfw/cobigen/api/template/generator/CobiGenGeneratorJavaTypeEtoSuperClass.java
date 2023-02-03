package com.devonfw.cobigen.api.template.generator;

import java.io.IOException;
import java.lang.reflect.Field;

import com.devonfw.cobigen.api.model.CobiGenModel;
import com.devonfw.cobigen.api.model.CobiGenVariableDefinitions;

/**
 * Implementation of {@link CobiGenGenerator} to generate private {@link Field}s.
 *
 */
public class CobiGenGeneratorJavaTypeEtoSuperClass implements CobiGenGenerator {

  @Override
  public void generate(CobiGenModel model, Appendable code) throws IOException {

    Class<?> type = CobiGenVariableDefinitions.JAVA_TYPE.getValue(model);

    Class<?> superclass = type.getSuperclass();
    String superclassName = superclass.getSimpleName();
    // ApplicationPersistenceEntity">AbstractEto<#else>${pojo.extendedType.name?replace("Entity","Eto")}
    if (superclassName.endsWith("Entity") && !superclassName.equals("ApplicationPersistenceEntity")) {
      code.append(superclassName.replace("Entity", "Eto"));
    } else {
      code.append("AbstractEto");
    }
  }

}

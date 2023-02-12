package com.devonfw.cobigen.api.template.generator;

import java.io.IOException;
import java.lang.reflect.Field;

import com.devonfw.cobigen.api.model.CobiGenModel;
import com.devonfw.cobigen.api.model.CobiGenVariableDefinitions;
import com.devonfw.cobigen.api.template.out.QualifiedName;

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
    String parentEto = "AbstractEto";
    if (superclassName.endsWith("Entity") && !superclassName.equals("ApplicationPersistenceEntity")) {
      parentEto = superclassName.substring(0, superclassName.length() - 6) + "Eto";
      String pkg = superclass.getPackageName().replace(".dataaccess", ".common");
      CobiGenVariableDefinitions.OUT.getValue(model).addImport(QualifiedName.of(pkg, parentEto, '.'));
    }
    code.append(parentEto);
  }

}

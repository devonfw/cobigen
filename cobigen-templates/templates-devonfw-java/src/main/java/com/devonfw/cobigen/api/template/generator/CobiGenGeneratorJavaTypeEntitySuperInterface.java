package com.devonfw.cobigen.api.template.generator;

import java.io.IOException;

import com.devonfw.cobigen.api.model.CobiGenModel;
import com.devonfw.cobigen.api.model.CobiGenVariableDefinitions;
import com.devonfw.cobigen.api.template.out.QualifiedName;

import x_rootpackage_x.general.common.x_scope_x.ApplicationEntity;
import x_rootpackage_x.general.dataaccess.x_scope_x.ApplicationPersistenceEntity;

/**
 * Implementation of {@link CobiGenGenerator} to generate super-interface name and import of generated entity interface.
 *
 */
public class CobiGenGeneratorJavaTypeEntitySuperInterface implements CobiGenGenerator {

  @Override
  public void generate(CobiGenModel model, Appendable code) throws IOException {

    Class<?> type = CobiGenVariableDefinitions.JAVA_TYPE.getValue(model);

    Class<?> superclass = type.getSuperclass();
    String superclassName = superclass.getSimpleName();
    String parentEntityInterface = ApplicationEntity.class.getSimpleName();
    if (superclassName.endsWith("Entity")
        && !superclassName.equals(ApplicationPersistenceEntity.class.getSimpleName())) {
      parentEntityInterface = superclassName.substring(0, superclassName.length() - 6);
      String pkg = superclass.getPackageName().replace(".dataaccess", ".common");
      CobiGenVariableDefinitions.OUT.getValue(model).addImport(QualifiedName.of(pkg, parentEntityInterface, '.'));
    }
    code.append(parentEntityInterface);
  }

}

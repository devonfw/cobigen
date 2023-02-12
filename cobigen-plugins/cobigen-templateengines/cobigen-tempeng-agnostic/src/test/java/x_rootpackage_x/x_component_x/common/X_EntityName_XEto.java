package x_rootpackage_x.x_component_x.common;

import com.devonfw.cobigen.api.annotation.CobiGenDynamicType;
import com.devonfw.cobigen.api.template.generator.CobiGenGeneratorJavaTypeEtoSuperClass;
import com.devonfw.cobigen.api.template.generator.CobiGenGeneratorJavaTypeFields;
import com.devonfw.cobigen.api.template.generator.CobiGenGeneratorJavaTypeGetters;
import com.devonfw.cobigen.api.template.generator.CobiGenGeneratorJavaTypeSetters;

import x_rootpackage_x.general.common.AbstractEto;

/**
 * Implementation of {@link X_EntityName_X} as {@link AbstractEto ETO}.
 */
public class X_EntityName_XEto extends @CobiGenDynamicType(CobiGenGeneratorJavaTypeEtoSuperClass.class) AbstractEto implements X_EntityName_X {

  private CobiGenGeneratorJavaTypeFields field;

  /**
   * The constructor.
   */
  public X_EntityName_XEto() {

    super();
  }

  private CobiGenGeneratorJavaTypeGetters getter;

  private CobiGenGeneratorJavaTypeSetters setter;
}

package x_rootpackage_x.x_component_x.common.x_scope_x.x_detail_x;

import com.devonfw.cobigen.api.annotation.CobiGenDynamicType;
import com.devonfw.cobigen.api.annotation.CobiGenProperties;
import com.devonfw.cobigen.api.annotation.CobiGenProperty;
import com.devonfw.cobigen.api.annotation.CobiGenTemplate;
import com.devonfw.cobigen.api.template.CobiGenJavaIncrements;
import com.devonfw.cobigen.api.template.CobiGenJavaProperties;
import com.devonfw.cobigen.api.template.generator.CobiGenGeneratorJavaTypeEtoProperties;
import com.devonfw.cobigen.api.template.generator.CobiGenGeneratorJavaTypeEtoSuperClass;

import x_rootpackage_x.general.common.x_scope_x.AbstractEto;

/**
 * Implementation of {@link X_EntityName_X} as {@link AbstractEto ETO}.
 */
@CobiGenTemplate(value = CobiGenJavaIncrements.ENTITY)
@CobiGenProperties({
@CobiGenProperty(key = CobiGenJavaProperties.KEY_SCOPE, value = CobiGenJavaProperties.VALUE_SCOPE_API),
@CobiGenProperty(key = CobiGenJavaProperties.KEY_MODULE, value = CobiGenJavaProperties.VALUE_MODULE_API) })
public class X_EntityName_XEto extends @CobiGenDynamicType(CobiGenGeneratorJavaTypeEtoSuperClass.class) AbstractEto implements X_EntityName_X {

  private CobiGenGeneratorJavaTypeEtoProperties field;

  /**
   * The constructor.
   */
  public X_EntityName_XEto() {

    super();
  }

}

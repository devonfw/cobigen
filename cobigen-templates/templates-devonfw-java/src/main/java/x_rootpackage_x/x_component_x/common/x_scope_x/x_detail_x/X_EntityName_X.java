package x_rootpackage_x.x_component_x.common.x_scope_x.x_detail_x;

import com.devonfw.cobigen.api.annotation.CobiGenDynamicType;
import com.devonfw.cobigen.api.annotation.CobiGenProperties;
import com.devonfw.cobigen.api.annotation.CobiGenProperty;
import com.devonfw.cobigen.api.annotation.CobiGenTemplate;
import com.devonfw.cobigen.api.template.CobiGenJavaIncrements;
import com.devonfw.cobigen.api.template.CobiGenJavaProperties;
import com.devonfw.cobigen.api.template.generator.CobiGenGeneratorJavaTypeEntitySuperInterface;
import com.devonfw.cobigen.api.template.generator.CobiGenGeneratorJavaTypeEtoProperties;

import x_rootpackage_x.general.common.x_scope_x.ApplicationEntity;

/**
 * {@link ApplicationEntity} for {@link X_EntityName_X}.
 */
@CobiGenTemplate(value = CobiGenJavaIncrements.ENTITY)
@CobiGenProperties({
@CobiGenProperty(key = CobiGenJavaProperties.KEY_SCOPE, value = CobiGenJavaProperties.VALUE_SCOPE_API),
@CobiGenProperty(key = CobiGenJavaProperties.KEY_MODULE, value = CobiGenJavaProperties.VALUE_MODULE_API) })
public interface X_EntityName_X
    extends @CobiGenDynamicType(CobiGenGeneratorJavaTypeEntitySuperInterface.class) ApplicationEntity {

  CobiGenGeneratorJavaTypeEtoProperties GENERATOR = null;

}

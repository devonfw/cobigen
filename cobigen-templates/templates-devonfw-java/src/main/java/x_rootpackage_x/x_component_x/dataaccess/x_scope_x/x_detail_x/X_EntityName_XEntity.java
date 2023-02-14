package x_rootpackage_x.x_component_x.dataaccess.x_scope_x.x_detail_x;

import com.devonfw.cobigen.api.annotation.CobiGenProperties;
import com.devonfw.cobigen.api.annotation.CobiGenProperty;
import com.devonfw.cobigen.api.annotation.CobiGenTemplate;
import com.devonfw.cobigen.api.template.CobiGenJavaIncrements;
import com.devonfw.cobigen.api.template.CobiGenJavaProperties;

import x_rootpackage_x.general.dataaccess.x_scope_x.ApplicationPersistenceEntity;
import x_rootpackage_x.x_component_x.common.x_scope_x.x_detail_x.X_EntityName_X;

/**
 * Implementation of {@link X_EntityName_X} as {@link ApplicationPersistenceEntity}.
 */
@CobiGenTemplate(value = CobiGenJavaIncrements.ENTITY)
@CobiGenProperties({
@CobiGenProperty(key = CobiGenJavaProperties.KEY_SCOPE, value = CobiGenJavaProperties.VALUE_SCOPE_API),
@CobiGenProperty(key = CobiGenJavaProperties.KEY_MODULE, value = CobiGenJavaProperties.VALUE_MODULE_CORE) })
public class X_EntityName_XEntity extends ApplicationPersistenceEntity implements X_EntityName_X {

  // TODO

}

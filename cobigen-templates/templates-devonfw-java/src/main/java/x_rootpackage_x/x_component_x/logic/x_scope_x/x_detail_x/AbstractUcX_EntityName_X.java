package x_rootpackage_x.x_component_x.logic.x_scope_x.x_detail_x;

import javax.inject.Inject;

import com.devonfw.cobigen.api.annotation.CobiGenProperties;
import com.devonfw.cobigen.api.annotation.CobiGenProperty;
import com.devonfw.cobigen.api.annotation.CobiGenTemplate;
import com.devonfw.cobigen.api.template.CobiGenJavaIncrements;
import com.devonfw.cobigen.api.template.CobiGenJavaProperties;

import x_rootpackage_x.x_component_x.common.x_scope_x.x_detail_x.X_EntityName_X;
import x_rootpackage_x.x_component_x.dataaccess.x_scope_x.x_detail_x.X_EntityName_XRepository;

/**
 * Abstract base class for all use-cases related to {@link X_EntityName_X}.
 */
@CobiGenTemplate(value = CobiGenJavaIncrements.LOGIC)
@CobiGenProperties({
@CobiGenProperty(key = CobiGenJavaProperties.KEY_SCOPE, value = CobiGenJavaProperties.VALUE_SCOPE_BASE),
@CobiGenProperty(key = CobiGenJavaProperties.KEY_MODULE, value = CobiGenJavaProperties.VALUE_MODULE_CORE) })
public abstract class AbstractUcX_EntityName_X {

  @Inject
  private X_EntityName_XMapper beanMapper;

  @Inject
  private X_EntityName_XRepository repository;

  /**
   * @return the {@link X_EntityName_XRepository}.
   */
  public X_EntityName_XRepository getRepository() {

    return this.repository;
  }

  /**
   * @return the {@link X_EntityName_XMapper} to map from {@link x_rootpackage_x.general.common.x_scope_x.AbstractEto
   *         ETO} to {@link x_rootpackage_x.general.dataaccess.x_scope_x.ApplicationPersistenceEntity entity} and vice
   *         versa.
   */
  public X_EntityName_XMapper getBeanMapper() {

    return this.beanMapper;
  }

}

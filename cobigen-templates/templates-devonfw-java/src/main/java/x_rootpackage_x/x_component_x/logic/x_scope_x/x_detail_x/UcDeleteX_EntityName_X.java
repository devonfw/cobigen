package x_rootpackage_x.x_component_x.logic.x_scope_x.x_detail_x;

import javax.annotation.security.RolesAllowed;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.annotation.CobiGenProperties;
import com.devonfw.cobigen.api.annotation.CobiGenProperty;
import com.devonfw.cobigen.api.annotation.CobiGenTemplate;
import com.devonfw.cobigen.api.template.CobiGenJavaIncrements;
import com.devonfw.cobigen.api.template.CobiGenJavaProperties;

import x_rootpackage_x.general.common.x_scope_x.security.ApplicationAccessControlConfig;
import x_rootpackage_x.x_component_x.common.x_scope_x.x_detail_x.X_EntityName_X;
import x_rootpackage_x.x_component_x.common.x_scope_x.x_detail_x.X_EntityName_XEto;

/**
 * Use-case to delete instances of {@link X_EntityName_X}.
 */
@Named
@Transactional
@CobiGenTemplate(value = CobiGenJavaIncrements.LOGIC)
@CobiGenProperties({
@CobiGenProperty(key = CobiGenJavaProperties.KEY_SCOPE, value = CobiGenJavaProperties.VALUE_SCOPE_IMPL),
@CobiGenProperty(key = CobiGenJavaProperties.KEY_MODULE, value = CobiGenJavaProperties.VALUE_MODULE_CORE) })
public class UcDeleteX_EntityName_X extends AbstractUcX_EntityName_X {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(UcDeleteX_EntityName_X.class);

  /**
   * @param id the {@link X_EntityName_XEto#getId() primary key} of the {@link X_EntityName_XEto} to delete.
   * @return {@code false} if the {@code id} was {@code null} and deletion was omitted, {@code true} otherwise.
   */
  @RolesAllowed(ApplicationAccessControlConfig.PERMISSION_DELETE_X_ENTITY_NAME_X)
  public boolean deleteX_EntityName_X(Long id) {

    if (id == null) {
      LOG.info("ID of X_EntityName_X is null - omitting deletion.");
      return false;
    }
    LOG.info("Deleting X_EntityName_X with ID {}.", id);
    getRepository().deleteById(id);
    return true;
  }

  /**
   * @param eto the {@link X_EntityName_XEto} to delete.
   * @return {@code false} if the object was {@code null} and deletion was omitted, {@code true} otherwise.
   */
  @RolesAllowed(ApplicationAccessControlConfig.PERMISSION_DELETE_X_ENTITY_NAME_X)
  public boolean deleteX_EntityName_X(X_EntityName_XEto eto) {

    if (eto == null) {
      LOG.info("X_EntityName_X is null - omitting deletion.");
      return false;
    }
    Long id = eto.getId();
    return deleteX_EntityName_X(id);
  }
}

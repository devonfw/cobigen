package x_rootpackage_x.x_component_x.logic.x_scope_x.x_detail_x;

import java.util.Optional;

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
import x_rootpackage_x.x_component_x.dataaccess.x_scope_x.x_detail_x.X_EntityName_XEntity;

/**
 * Use-case to find instances of {@link X_EntityName_X}.
 */
@Named
@Transactional
@CobiGenTemplate(value = CobiGenJavaIncrements.LOGIC)
@CobiGenProperties({
@CobiGenProperty(key = CobiGenJavaProperties.KEY_SCOPE, value = CobiGenJavaProperties.VALUE_SCOPE_IMPL),
@CobiGenProperty(key = CobiGenJavaProperties.KEY_MODULE, value = CobiGenJavaProperties.VALUE_MODULE_CORE) })
public class UcFindX_EntityName_X extends AbstractUcX_EntityName_X {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(UcFindX_EntityName_X.class);

  /**
   * @param id the {@link X_EntityName_XEntity#getId() primary key} of the requested {@link X_EntityName_XEto}.
   * @return the {@link X_EntityName_XEto} or {@code null} if no such ETO exists.
   */
  @RolesAllowed(ApplicationAccessControlConfig.PERMISSION_FIND_X_ENTITY_NAME_X)
  public X_EntityName_XEto findX_EntityName_X(Long id) {

    LOG.debug("Get X_EntityName_X with id {} from database.", id);
    if (id == null) {
      return null;
    }
    Optional<X_EntityName_XEntity> entity = getRepository().findById(id);
    if (entity.isPresent()) {
      return getBeanMapper().toEto(entity.get());
    } else {
      return null;
    }
  }

}

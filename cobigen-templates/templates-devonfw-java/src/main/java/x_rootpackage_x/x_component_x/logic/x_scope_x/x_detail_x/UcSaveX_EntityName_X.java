package x_rootpackage_x.x_component_x.logic.x_scope_x.x_detail_x;

import java.util.Objects;

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
 * Use-case to save (insert or update) instances of {@link X_EntityName_X}.
 */
@Named
@Transactional
@CobiGenTemplate(value = CobiGenJavaIncrements.LOGIC)
@CobiGenProperties({
@CobiGenProperty(key = CobiGenJavaProperties.KEY_SCOPE, value = CobiGenJavaProperties.VALUE_SCOPE_IMPL),
@CobiGenProperty(key = CobiGenJavaProperties.KEY_MODULE, value = CobiGenJavaProperties.VALUE_MODULE_CORE) })
public class UcSaveX_EntityName_X extends AbstractUcX_EntityName_X {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(UcSaveX_EntityName_X.class);

  /**
   * @param eto the {@link X_EntityName_XEto} to save.
   * @return the {@link X_EntityName_XEntity#getId() primary key} of the saved {@link X_EntityName_XEto}.
   */
  @RolesAllowed(ApplicationAccessControlConfig.PERMISSION_SAVE_X_ENTITY_NAME_X)
  public Long saveX_EntityName_X(X_EntityName_XEto eto) {

    Objects.requireNonNull(eto);
    LOG.debug("Saving X_EntityName_X with id {} to database.", eto.getId());
    X_EntityName_XEntity entity = getBeanMapper().toEntity(eto);
    entity = getRepository().save(entity);
    return entity.getId();
  }

}
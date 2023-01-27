package x_rootpackage_x.x_component_x.logic;

import java.util.Optional;

import javax.inject.Named;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x_rootpackage_x.x_component_x.common.X_EntityName_X;
import x_rootpackage_x.x_component_x.common.X_EntityName_XEto;
import x_rootpackage_x.x_component_x.dataaccess.X_EntityName_XEntity;

/**
 * Use-case to find instances of {@link X_EntityName_X}.
 */
@Named
@Transactional
public class UcFindX_EntityName_X extends AbstractUcX_EntityName_X {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(UcFindX_EntityName_X.class);

  /**
   * @param id the {@link X_EntityName_XEntity#getId() primary key} of the requested {@link X_EntityName_XEto}.
   * @return the {@link X_EntityName_XEto} or {@code null} if no such ETO exists.
   */
  public X_EntityName_XEto findX_EntityName_X(long id) {

    LOG.debug("Get X_EntityName_X with id {} from database.", id);
    Optional<X_EntityName_XEntity> entity = getRepository().findById(id);
    if (entity.isPresent()) {
      return getBeanMapper().toEto(entity.get());
    } else {
      return null;
    }
  }

}

package x_rootpackage_x.x_component_x.logic;

import javax.inject.Inject;

import x_rootpackage_x.x_component_x.common.X_EntityName_X;
import x_rootpackage_x.x_component_x.dataaccess.X_EntityName_XRepository;

/**
 * Abstract base class for all use-cases related to {@link X_EntityName_X}.
 */
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
   * @return the {@link X_EntityName_XMapper} to map from {@link x_rootpackage_x.general.common.AbstractEto ETO} to
   *         {@link x_rootpackage_x.general.dataaccess.ApplicationPersistenceEntity entity} and vice versa.
   */
  public X_EntityName_XMapper getBeanMapper() {

    return this.beanMapper;
  }

}

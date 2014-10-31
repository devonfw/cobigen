package ${variables.rootPackage}.general.persistence.base.dao;

import ${variables.rootPackage}.general.persistence.api.dao.ApplicationDao;
import ${variables.rootPackage}.general.persistence.base.RestaurantPersistenceEntity;

import io.oasp.module.jpa.persistence.base.AbstractDao;

import net.sf.mmm.util.entity.api.PersistenceEntity;

import org.springframework.stereotype.Repository;

/**
 * This is the abstract base implementation of {@link ApplicationDao}.
 *
 * @param <ENTITY> is the {@link #getEntityClass() type} of the managed entity.
 */
@Repository
public abstract class ApplicationDaoImpl<ENTITY extends PersistenceEntity<Long>> extends AbstractDao<ENTITY> implements
    ApplicationDao<ENTITY> {

  /**
   * The constructor.
   */
  public ApplicationDaoImpl() {

    super();
  }

}

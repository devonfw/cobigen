package ${variables.rootPackage}.general.persistence.base.dao;

import ${variables.rootPackage}.general.persistence.api.dao.AbstractDao;
import ${variables.rootPackage}.general.persistence.base.RestaurantPersistenceEntity;

import net.sf.mmm.persistence.base.jpa.AbstractJpaGenericDao;
import net.sf.mmm.util.entity.api.PersistenceEntity;

/**
 * This is the abstract base implementation of {@link AbstractDao}.
 * 
 * @author hohwille
 * 
 * @param <ID> is the type of the {@link RestaurantPersistenceEntity#getId() primary key} of the managed {@link RestaurantPersistenceEntity
 *        entity}.
 * @param <ENTITY> is the {@link #getEntityClass() type} of the managed entity.
 */
public abstract class AbstractDaoImpl<ID, ENTITY extends PersistenceEntity<ID>> extends
    AbstractJpaGenericDao<ID, ENTITY> implements AbstractDao<ENTITY, ID> {

  /**
   * The constructor.
   */
  public AbstractDaoImpl() {

    super();
  }

}

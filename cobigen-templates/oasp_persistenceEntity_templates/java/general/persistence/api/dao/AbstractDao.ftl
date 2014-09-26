package ${variables.rootPackage}.general.persistence.api.dao;

import net.sf.mmm.persistence.api.GenericDao;
import net.sf.mmm.util.entity.api.PersistenceEntity;

/**
 * Interface for {@link GenericDao Data Access Object} in this project.
 * 
 * @param <ID> is the type of the {@link PersistenceEntity#getId() primary key}
 *        of the managed
 *        {@link io.oasp.gastronomy.restaurant.general.persistence.base.RestaurantPersistenceEntity
 *        entity}.
 * @param <ENTITY> is the {@link #getEntityClass() type} of the managed entity.
 */
public interface AbstractDao<ENTITY extends PersistenceEntity<ID>, ID> extends GenericDao<ID, ENTITY> {

}

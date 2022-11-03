package ${variables.rootPackage}.general.dataaccess.api.dao;

import com.devonfw.module.jpa.dataaccess.api.Dao;

import net.sf.mmm.util.entity.api.PersistenceEntity;

/**
 * Interface for all {@link Dao DAOs} (Data Access Object) of this application.
 *
 * @param <ENTITY> is the type of the managed entity.
 */
public interface ApplicationDao<ENTITY extends PersistenceEntity<Long>> extends Dao<ENTITY> {

}
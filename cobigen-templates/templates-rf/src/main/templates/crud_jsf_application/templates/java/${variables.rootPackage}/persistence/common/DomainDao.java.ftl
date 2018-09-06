package ${variables.rootPackage}.persistence.common;

import java.io.Serializable;

/**
 * Interface for {@link AbstractDomainDao}.
 */
public interface DomainDao<T, ID extends Serializable> {

    /**
     * Stores an entity to the database.
     *
     * @param entity
     *            entity to save
     */
    public void save(T entity);

    /**
     * Deletes an entity from the database.
     *
     * @param entity
     *            entity to delete
     */
    public void delete(T entity);

    /**
     * Fetches an entity from the database by its id.
     *
     * @param id
     *            identifier of the searched entity
     * @return T searched entity
     */
    public T searchById(ID id);

}

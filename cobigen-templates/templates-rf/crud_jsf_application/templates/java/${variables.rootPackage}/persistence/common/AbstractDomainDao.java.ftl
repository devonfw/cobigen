package ${variables.rootPackage}.persistence.common;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Required;

import de.bund.bva.pliscommon.persistence.dao.AbstractDao;

/**
 * This is the abstract base implementation for DAOs in this project.
 */
public abstract class AbstractDomainDao<T, ID extends Serializable> extends AbstractDao<T, ID> {

    /**
     * The constructor.
     */
    public AbstractDomainDao() {
        super();
    }

    @Override
    @Required
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }

    public void save(T object) {
        super.speichere(object);
    }

    public void delete(T object) {
        super.loesche(object);
    }

    public T searchById(ID id) {
        return super.sucheMitId(id);
    }

    /**
     * This method is not supported. Use "{@link #delete(object)}" instead. An UnsupportedOperationException
     * will be thrown.
     *
     * @throws UnsupportedOperationException
     *             This exception will be thrown, if this method is called.
     */
    @Override
    @Deprecated
    public void loesche(T entitaet) {
        throw new UnsupportedOperationException("The method " + getClass().getName()
            + ".'loesche()' is unsupported.");
    }

    /**
     * This method is not supported. Use "{@link #searchById(id)}" instead. An UnsupportedOperationException
     * will be thrown.
     *
     * @throws UnsupportedOperationException
     *             This exception will be thrown, if this method is called.
     */
    @Override
    @Deprecated
    public T sucheMitId(ID id) {
        throw new UnsupportedOperationException("The method " + getClass().getName()
            + ".'sucheMitId()' is unsupported.");
    }

    /**
     * This method is not supported. Use "{@link #save()}" instead. An UnsupportedOperationException will be
     * thrown.
     *
     * @throws UnsupportedOperationException
     *             This exception will be thrown, if this method is called.
     */
    @Override
    @Deprecated
    public void speichere(T entitaet) {
        throw new UnsupportedOperationException("The method " + getClass().getName()
            + ".'speichere()' is unsupported.");
    }

}

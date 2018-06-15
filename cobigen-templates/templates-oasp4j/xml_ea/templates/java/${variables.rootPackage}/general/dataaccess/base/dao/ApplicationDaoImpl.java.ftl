<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
package ${variables.rootPackage}.general.dataaccess.base.dao;

import ${variables.rootPackage}.general.dataaccess.api.dao.ApplicationDao;

import io.oasp.module.jpa.dataaccess.base.AbstractDao;

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


</#compress>
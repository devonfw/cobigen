package ${variables.rootPackage}.general.dataaccess.base.dao;

import io.oasp.module.jpa.dataaccess.api.MutablePersistenceEntity;
import io.oasp.module.jpa.dataaccess.api.RevisionedMasterDataDao;

import java.util.List;

public abstract class ApplicationMasterDataDaoImpl<ENTITY extends MutablePersistenceEntity<Long>> extends
    ApplicationDaoImpl<ENTITY> implements RevisionedMasterDataDao<ENTITY> {

  /**
   * The constructor.
   */
  public ApplicationMasterDataDaoImpl() {

    super();
  }

  @Override
  public List<ENTITY> findAll() {

    return super.findAll();
  }

}
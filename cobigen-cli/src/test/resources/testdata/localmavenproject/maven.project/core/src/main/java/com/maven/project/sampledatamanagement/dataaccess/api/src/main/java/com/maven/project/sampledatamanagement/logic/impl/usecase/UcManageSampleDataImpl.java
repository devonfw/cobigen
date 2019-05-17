package com.maven.project.sampledatamanagement.logic.impl.usecase;

import com.maven.project.sampledatamanagement.logic.api.to.SampleDataEto;
import com.maven.project.sampledatamanagement.logic.api.usecase.UcManageSampleData;
import com.maven.project.sampledatamanagement.logic.base.usecase.AbstractSampleDataUc;
import com.maven.project.sampledatamanagement.dataaccess.api.SampleDataEntity;

import java.util.Objects;

import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case implementation for modifying and deleting SampleDatas
 */
@Named
@Validated
@Transactional
public class UcManageSampleDataImpl extends AbstractSampleDataUc implements UcManageSampleData {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(UcManageSampleDataImpl.class);

  @Override
  public boolean deleteSampleData(long sampleDataId) {

    SampleDataEntity sampleData = getSampleDataRepository().find(sampleDataId);
    getSampleDataRepository().delete(sampleData);
    LOG.debug("The sampleData with id '{}' has been deleted.", sampleDataId);
    return true;
  }

  @Override
  public SampleDataEto saveSampleData(SampleDataEto sampleData) {

   Objects.requireNonNull(sampleData, "sampleData");

	 SampleDataEntity sampleDataEntity = getBeanMapper().map(sampleData, SampleDataEntity.class);

   //initialize, validate sampleDataEntity here if necessary
   SampleDataEntity resultEntity = getSampleDataRepository().save(sampleDataEntity);
   LOG.debug("SampleData with id '{}' has been created.",resultEntity.getId());
   return getBeanMapper().map(resultEntity, SampleDataEto.class);
  }
}

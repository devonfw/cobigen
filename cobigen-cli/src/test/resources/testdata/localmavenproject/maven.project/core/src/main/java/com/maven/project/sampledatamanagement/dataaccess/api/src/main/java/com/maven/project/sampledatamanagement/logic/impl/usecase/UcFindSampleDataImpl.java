package com.maven.project.sampledatamanagement.logic.impl.usecase;

import com.maven.project.sampledatamanagement.logic.api.to.SampleDataEto;
import com.maven.project.sampledatamanagement.logic.api.usecase.UcFindSampleData;
import com.maven.project.sampledatamanagement.logic.base.usecase.AbstractSampleDataUc;
import com.maven.project.sampledatamanagement.dataaccess.api.SampleDataEntity;
import com.maven.project.sampledatamanagement.logic.api.to.SampleDataSearchCriteriaTo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import javax.inject.Named;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case implementation for searching, filtering and getting SampleDatas
 */
@Named
@Validated
@Transactional
public class UcFindSampleDataImpl extends AbstractSampleDataUc implements UcFindSampleData {

	/**
	 * Logger instance.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(UcFindSampleDataImpl.class);

	@Override
	public SampleDataEto findSampleData(long id) {
      LOG.debug("Get SampleData with id {} from database.", id);
      Optional<SampleDataEntity> foundEntity = getSampleDataRepository().findById(id);
      if (foundEntity.isPresent())
        return getBeanMapper().map(foundEntity.get(), SampleDataEto.class);
      else
        return null;
    }

	@Override
	public Page<SampleDataEto> findSampleDatas(SampleDataSearchCriteriaTo criteria) {
      Page<SampleDataEntity> sampledatas = getSampleDataRepository().findByCriteria(criteria);
    return mapPaginatedEntityList(sampledatas, SampleDataEto.class);
  }

	@Override
	public SampleDataCto findSampleDataCto(long id) {
      LOG.debug("Get SampleDataCto with id {} from database.", id);
      SampleDataEntity entity = getSampleDataRepository().find(id);
      SampleDataCto cto = new SampleDataCto();
      cto.setSampleData(getBeanMapper().map(entity, SampleDataEto.class));
   
      return cto;
    }

	@Override
	public Page<SampleDataCto> findSampleDataCtos(SampleDataSearchCriteriaTo criteria) {
  
      Page<SampleDataEntity> sampledatas = getSampleDataRepository().findByCriteria(criteria);
      List<SampleDataCto> ctos = new ArrayList<>();
      for (SampleDataEntity entity : sampledatas.getContent()) {
        SampleDataCto cto = new SampleDataCto();
        cto.setSampleData(getBeanMapper().map(entity, SampleDataEto.class));
        ctos.add(cto);
      }
      Pageable pagResultTo = PageRequest.of(criteria.getPageable().getPageNumber(), criteria.getPageable().getPageSize());
      
      return new PageImpl<>(ctos, pagResultTo, sampledatas.getTotalElements());
    }

}

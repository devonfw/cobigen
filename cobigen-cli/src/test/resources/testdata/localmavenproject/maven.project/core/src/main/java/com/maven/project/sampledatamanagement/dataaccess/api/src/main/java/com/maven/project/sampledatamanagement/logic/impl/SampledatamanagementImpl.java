package com.maven.project.sampledatamanagement.logic.impl;

import com.maven.project.general.logic.base.AbstractComponentFacade;
import com.maven.project.sampledatamanagement.logic.api.Sampledatamanagement;
import com.maven.project.sampledatamanagement.logic.api.to.SampleDataEto;
import com.maven.project.sampledatamanagement.logic.api.usecase.UcFindSampleData;
import com.maven.project.sampledatamanagement.logic.api.usecase.UcManageSampleData;
import com.maven.project.sampledatamanagement.logic.api.to.SampleDataSearchCriteriaTo;
import org.springframework.data.domain.Page;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Implementation of component interface of sampledatamanagement
 */
@Named
public class SampledatamanagementImpl extends AbstractComponentFacade implements Sampledatamanagement {

	@Inject
	private UcFindSampleData ucFindSampleData;

	@Inject
	private UcManageSampleData ucManageSampleData;

	@Override
	public SampleDataEto findSampleData(long id) {

      return this.ucFindSampleData.findSampleData(id);
    }

	@Override
	public Page<SampleDataEto> findSampleDatas(SampleDataSearchCriteriaTo criteria) {
      return this.ucFindSampleData.findSampleDatas(criteria);
    }

	@Override
	public SampleDataEto saveSampleData(SampleDataEto sampledata) {

      return this.ucManageSampleData.saveSampleData(sampledata);
    }

	@Override
	public boolean deleteSampleData(long id) {

      return this.ucManageSampleData.deleteSampleData(id);
    }

	@Override
	public SampleDataCto findSampleDataCto(long id) {
    
      return ucFindSampleData.findSampleDataCto(id);
    }

	@Override
	public Page<SampleDataCto> findSampleDataCtos(SampleDataSearchCriteriaTo criteria) {
    
      return ucFindSampleData.findSampleDataCtos(criteria);
    }

}

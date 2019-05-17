package com.maven.project.sampledatamanagement.service.impl.rest;

import com.maven.project.sampledatamanagement.common.api.SampleData;
import com.maven.project.sampledatamanagement.logic.api.Sampledatamanagement;
import com.maven.project.sampledatamanagement.logic.api.to.SampleDataEto;
import com.maven.project.sampledatamanagement.logic.api.to.SampleDataSearchCriteriaTo;
import com.maven.project.sampledatamanagement.service.api.rest.SampledatamanagementRestService;
import org.springframework.data.domain.Page;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

/**
 * The service implementation for REST calls in order to execute the logic of component {@link Sampledatamanagement}.
 */
@Named("SampledatamanagementRestService")
public class SampledatamanagementRestServiceImpl implements SampledatamanagementRestService {

	@Inject
	private Sampledatamanagement sampledatamanagement;

	@Override
	public SampleDataEto getSampleData(long id) {
    return this.sampledatamanagement.findSampleData(id);
  }

	@Override
	public SampleDataEto saveSampleData(SampleDataEto sampledata) {
      return this.sampledatamanagement.saveSampleData(sampledata);
  }

	@Override
	public void deleteSampleData(long id) {
    this.sampledatamanagement.deleteSampleData(id);
  }

	@Override
	public Page<SampleDataEto> findSampleDatas(SampleDataSearchCriteriaTo searchCriteriaTo) {
    return this.sampledatamanagement.findSampleDatas(searchCriteriaTo);
  }

	@Override
	public SampleDataCto getSampleDataCto(long id) {
    return this.sampledatamanagement.findSampleDataCto(id);
  }

	@Override
	public Page<SampleDataCto> findSampleDataCtos(SampleDataSearchCriteriaTo searchCriteriaTo) {
    return this.sampledatamanagement.findSampleDataCtos(searchCriteriaTo);
  }

}

package com.maven.project.sampledatamanagement.logic.api.usecase;

import com.maven.project.sampledatamanagement.logic.api.to.SampleDataEto;
import com.maven.project.sampledatamanagement.logic.api.to.SampleDataSearchCriteriaTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UcFindSampleData {

  /**
   * Returns a SampleData by its id 'id'.
   *
   * @param id The id 'id' of the SampleData.
   * @return The {@link SampleDataEto} with id 'id'
   */
  SampleDataEto findSampleData(long id);


  /**
   * Returns a paginated list of SampleDatas matching the search criteria.
   *
   * @param criteria the {@link SampleDataSearchCriteriaTo}.
   * @return the {@link List} of matching {@link SampleDataEto}s.
   */
  Page<SampleDataEto> findSampleDatas(SampleDataSearchCriteriaTo criteria);

}

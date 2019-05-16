package com.maven.project.sampledatamanagement.logic.base.usecase;

import com.maven.project.general.logic.base.AbstractUc;
import com.maven.project.sampledatamanagement.dataaccess.api.repo.SampleDataRepository;

import javax.inject.Inject;

/**
 * Abstract use case for SampleDatas, which provides access to the commonly necessary data access objects.
 */
public class AbstractSampleDataUc extends AbstractUc {

	  /** @see #getSampleDataRepository() */
	  @Inject
    private SampleDataRepository sampleDataRepository;

    /**
     * Returns the field 'sampleDataRepository'.
     * @return the {@link SampleDataRepository} instance.
     */
    public SampleDataRepository getSampleDataRepository() {

      return this.sampleDataRepository;
    }

}

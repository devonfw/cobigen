package com.maven.project.sampledatamanagement.logic.api.to;

import com.devonfw.module.basic.common.api.to.AbstractCto;
import com.maven.project.sampledatamanagement.common.api.SampleData;

import java.util.List;
import java.util.Set;

/**
 * Composite transport object of SampleData
 */
public class SampleDataCto extends AbstractCto {

	private static final long serialVersionUID = 1L;

	private SampleDataEto sampleData;


	public SampleDataEto getSampleData() {
		return sampleData;
	}

	public void setSampleData(SampleDataEto sampleData) {
		this.sampleData = sampleData;
	}


}

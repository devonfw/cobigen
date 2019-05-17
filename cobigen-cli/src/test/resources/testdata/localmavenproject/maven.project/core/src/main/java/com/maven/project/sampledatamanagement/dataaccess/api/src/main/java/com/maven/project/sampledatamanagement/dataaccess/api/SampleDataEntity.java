package com.maven.project.sampledatamanagement.dataaccess.api;

import com.maven.project.sampledatamanagement.common.api.SampleData;
import com.maven.project.general.dataaccess.api.ApplicationPersistenceEntity;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Data access object for SampleData entities
 */
@Entity
@javax.persistence.Table(name = "SampleData")
public class SampleDataEntity extends ApplicationPersistenceEntity implements SampleData {

  private static final long serialVersionUID = 1L;


}

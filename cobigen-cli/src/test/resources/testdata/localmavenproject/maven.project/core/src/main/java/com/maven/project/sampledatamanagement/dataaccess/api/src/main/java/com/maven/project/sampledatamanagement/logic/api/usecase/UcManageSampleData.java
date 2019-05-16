package com.maven.project.sampledatamanagement.logic.api.usecase;

import com.maven.project.sampledatamanagement.logic.api.to.SampleDataEto;

/**
 * Interface of UcManageSampleData to centralize documentation and signatures of methods.
 */
public interface UcManageSampleData {

  /**
   * Deletes a sampleData from the database by its id 'sampleDataId'.
   *
   * @param sampleDataId Id of the sampleData to delete
   * @return boolean <code>true</code> if the sampleData can be deleted, <code>false</code> otherwise
   */
  boolean deleteSampleData(long sampleDataId);

  /**
   * Saves a sampleData and store it in the database.
   *
   * @param sampleData the {@link SampleDataEto} to create.
   * @return the new {@link SampleDataEto} that has been saved with ID and version.
   */
  SampleDataEto saveSampleData(SampleDataEto sampleData);

}

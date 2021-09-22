package com.devonfw.cobigen.eclipse.common;

import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Abstract long running background job providing exception handling facilities.
 */
public abstract class AbstractCobiGenJob implements IRunnableWithProgress {

  /**
   * Exception occurred during processing
   */
  protected RuntimeException occurredException;

  /**
   * Returns the field 'occurredException'
   *
   * @return value of occurredException
   */
  public RuntimeException getOccurredException() {

    return this.occurredException;
  }

  /**
   * States whether an exception occurred during processing.
   *
   * @return <code>true</code> if an exception occurred, <code>false</code> otherwise.
   */
  public boolean isExceptionOccurred() {

    return this.occurredException != null;
  }
}

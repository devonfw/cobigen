package com.devonfw.cobigen.api.extension;

import java.io.File;

import com.devonfw.cobigen.api.annotation.ExceptionFacade;
import com.devonfw.cobigen.api.exception.MergeException;

/**
 * This interface should be inherited to declare a new component to handle document merges. An {@link Merger} can be
 * registered via an {@link TriggerInterpreter} implementation
 */
@ExceptionFacade
public interface Merger {

  /**
   * Returns the type, this merger should handle
   *
   * @return the type (not null)
   */
  public String getType();

  /**
   * Merges the patch into the base file
   *
   * @param base target {@link File} to be merged into
   * @param patch {@link String} patch, which should be applied to the base file
   * @param targetCharset target char set of the file to be read and write
   * @return Merged source code (not null)
   * @throws MergeException if an exception occurs while merging the contents
   */
  public String merge(File base, String patch, String targetCharset) throws MergeException;

}

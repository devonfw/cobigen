package com.devonfw.cobigen.xmlplugin.merger.delegates;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.extension.Merger;
import com.github.maybeec.lexeme.LeXeMerger;
import com.github.maybeec.lexeme.common.exception.XMLMergeException;

/**
 * Provides a XmlLawMerger instance with the {@link Merger} interface
 */
public class XmlMergerDelegate implements Merger {

  /** Merger type of this instance. */

  private MergeType mergeType = MergeType.PATCHOVERWRITE;

  /** {@link LeXeMerger} instance to be used. */
  private LeXeMerger merger;

  /**
   * Path to merge schema files
   */
  private Path mergeSchemaPath;

  /**
   * Required to pass validation state to new LeXeMerger if path to root template was changed
   */
  private boolean validationEnabled = false;

  private static final Logger LOG = LoggerFactory.getLogger(XmlMergerDelegate.class);

  /**
   *
   * @param mergeSchemaLocation String of a path to the folder containing the merge schemas to be used
   * @param mergeType the way how conflicts will be handled
   * @param validate use validator
   * @author sholzer (Aug 27, 2015)
   */
  public XmlMergerDelegate(String mergeSchemaLocation, MergeType mergeType, Boolean validate) {

    this.mergeType = mergeType;

    this.merger = new LeXeMerger(mergeSchemaLocation);
    setValidation(validate);
    this.validationEnabled = validate;
  }

  /**
   *
   * @param mergeSchemaLocation Path to the folder containing the merge schemas to be used
   * @param mergeType the way how conflicts will be handled
   * @param validate use validator
   * @author sholzer (Aug 27, 2015)
   */
  public XmlMergerDelegate(Path mergeSchemaLocation, MergeType mergeType, Boolean validate) {

    this.mergeType = mergeType;

    this.merger = new LeXeMerger(mergeSchemaLocation);
    setValidation(validate);
    this.validationEnabled = validate;
  }

  /**
   * Updates path of merge schema location with new template root path
   *
   * @param path Path to resolve with MERGE_SCHEMA_RESOURCE_FOLDER path
   */
  public void updateMergeSchemaPath(Path path) {

    if (path != null) {
      Path newMergeSchemaPath = path.resolve(ConfigurationConstants.MERGE_SCHEMA_RESOURCE_FOLDER);
      if (Files.exists(newMergeSchemaPath)) {
        this.mergeSchemaPath = newMergeSchemaPath;
        this.merger = new LeXeMerger(this.mergeSchemaPath);
        setValidation(this.validationEnabled);
      } else {
        this.mergeSchemaPath = null;
      }
    } else {
      throw new IllegalArgumentException("rootPath path cannot be null.");
    }
  }

  @Override
  public String getType() {

    return this.mergeType.value;
  }

  @Override
  public String merge(File base, String patch, String targetCharset) throws MergeException {

    try {
      return this.merger.mergeInString(base, patch, targetCharset, this.mergeType.type);
    } catch (XMLMergeException e) {
      throw new MergeException(base, "Error during merge processing by LeXeMe.", e);
    }
  }

  /**
   * Sets the validation flag
   *
   * @param validation true if a validation is desired. false otherwise. Default is false
   * @author sholzer (Sep 1, 2015)
   */
  public void setValidation(boolean validation) {

    this.merger.setValidation(validation);
  }

}

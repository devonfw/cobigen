package com.devonfw.cobigen.api.externalprocess;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.externalprocess.to.MergeTo;

/**
 * Default external server proxy for Merger
 */
public class ExternalServerMergerProxy implements Merger {

  /** The external process for the plugin */
  protected ExternalProcess externalProcess;

  /** Whether the patch overrides base code fragments */
  protected boolean patchOverrides;

  /**
   * Create new proxy which automatically communicates with the external process by JSON communication
   *
   * @param externalProcess of the plugin
   * @param patchOverrides whether the patch overrides base code fragments
   */
  public ExternalServerMergerProxy(ExternalProcess externalProcess, boolean patchOverrides) {

    this.externalProcess = externalProcess;
    this.patchOverrides = patchOverrides;
  }

  @Override
  public String getType() {

    throw new CobiGenRuntimeException("This method should be implemented in Java for performance reasons!");
  }

  @Override
  public String merge(File base, String patch, String targetCharset) throws MergeException {

    String baseFileContents;
    try {
      baseFileContents = new String(Files.readAllBytes(base.toPath()), Charset.forName(targetCharset));
    } catch (IOException e) {
      throw new MergeException(base, "Could not read base file!", e);
    }

    MergeTo mergeTo = new MergeTo(baseFileContents, patch, this.patchOverrides);

    return this.externalProcess.postJsonRequest("merge", mergeTo);
  }
}

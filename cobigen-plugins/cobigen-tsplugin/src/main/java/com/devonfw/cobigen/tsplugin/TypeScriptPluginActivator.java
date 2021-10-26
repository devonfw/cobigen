package com.devonfw.cobigen.tsplugin;

import java.util.List;

import com.devonfw.cobigen.api.annotation.Activation;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.api.externalprocess.ExternalProcess;
import com.devonfw.cobigen.tsplugin.config.constant.MavenMetadata;
import com.devonfw.cobigen.tsplugin.inputreader.TypeScriptInputReader;
import com.devonfw.cobigen.tsplugin.merger.TypeScriptMerger;
import com.google.common.collect.Lists;

/**
 * TypeScript Plug-in Activator to be registered in the PluginRegistry of CobiGen.
 */
@Activation(byFileExtension = { TypeScriptInputReader.VALID_EXTENSION }, byMergeStrategy = {
TypeScriptPluginActivator.TSMERGE, TypeScriptPluginActivator.TSMERGE_OVERRIDE })
public class TypeScriptPluginActivator implements GeneratorPluginActivator {

  /** Download URL for the external server executable */
  private static final String EXTERNAL_SERVER_DOWNLOAD_URL;

  /** Current Operating System, the code is exectued on */
  private static final String OS = System.getProperty("os.name").toLowerCase();

  /** Merger type for typescript files (prefer patch) */
  public static final String TSMERGE_OVERRIDE = "tsmerge_override";

  /** Merger type for typescript files (prefer base) */
  public static final String TSMERGE = "tsmerge";

  /** Defines the trigger type */
  private static final String TRIGGER_TYPE = "typescript";

  /** The singleton external process to communicate with the ts-merger nestserver */
  private ExternalProcess externalProcess = new ExternalProcess(EXTERNAL_SERVER_DOWNLOAD_URL, MavenMetadata.ARTIFACT_ID,
      MavenMetadata.SERVER_VERSION, "/processmanagement/" + MavenMetadata.ARTIFACT_ID + "/");

  static {
    if (OS.indexOf("win") >= 0) {
      EXTERNAL_SERVER_DOWNLOAD_URL = MavenMetadata.DOWNLOAD_URL_WIN;
    } else if (OS.indexOf("mac") >= 0) {
      EXTERNAL_SERVER_DOWNLOAD_URL = MavenMetadata.DOWNLOAD_URL_MACOS;
    } else {
      EXTERNAL_SERVER_DOWNLOAD_URL = MavenMetadata.DOWNLOAD_URL_LINUX;
    }
  }

  @Override
  public List<Merger> bindMerger() {

    List<Merger> merger = Lists.newLinkedList();
    merger.add(new TypeScriptMerger(this.externalProcess, TSMERGE, false));
    merger.add(new TypeScriptMerger(this.externalProcess, TSMERGE_OVERRIDE, true));
    return merger;
  }

  @Override
  public List<TriggerInterpreter> bindTriggerInterpreter() {

    return Lists.<TriggerInterpreter> newArrayList(new TypeScriptTriggerInterpreter(this.externalProcess, TRIGGER_TYPE));
  }

}

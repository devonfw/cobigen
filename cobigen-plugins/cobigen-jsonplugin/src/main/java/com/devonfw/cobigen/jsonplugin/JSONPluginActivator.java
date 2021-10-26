package com.devonfw.cobigen.jsonplugin;

import java.util.List;

import com.devonfw.cobigen.api.annotation.Activation;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.jsonplugin.merger.JSONMerger;
import com.google.common.collect.Lists;

/**
 * JSON Plug-in Activator to be registered in the PluginRegistry of CobiGen.
 */
@Activation(byMergeStrategy = { JSONPluginActivator.JSONMERGE, JSONPluginActivator.JSONMERGE_OVERRIDE })
public class JSONPluginActivator implements GeneratorPluginActivator {

  /** JSON merge (prefer patch) */
  static final String JSONMERGE_OVERRIDE = "jsonmerge_override";

  /** JSON merge (prefer base) */
  static final String JSONMERGE = "jsonmerge";

  @Override
  public List<Merger> bindMerger() {

    List<Merger> merger = Lists.newLinkedList();
    merger.add(new JSONMerger(JSONMERGE, false));
    merger.add(new JSONMerger(JSONMERGE_OVERRIDE, true));
    return merger;
  }

  @Override
  public List<TriggerInterpreter> bindTriggerInterpreter() {

    return null;
  }

}

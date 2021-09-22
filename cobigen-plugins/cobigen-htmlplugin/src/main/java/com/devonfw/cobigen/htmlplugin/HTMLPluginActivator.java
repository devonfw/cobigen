package com.devonfw.cobigen.htmlplugin;

import java.util.List;

import com.devonfw.cobigen.api.annotation.Activation;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.htmlplugin.merger.AngularMerger;
import com.google.common.collect.Lists;

/**
 * JSON Plug-in Activator to be registered in the PluginRegistry of CobiGen.
 */
@Activation(byMergeStrategy = { HTMLPluginActivator.NG_MERGE, HTMLPluginActivator.NG_MERGE_OVERRIDE })
public class HTMLPluginActivator implements GeneratorPluginActivator {

  /** Merge Strategy for NG Templates (patch first) */
  static final String NG_MERGE_OVERRIDE = "html-ng*_override";

  /** Merge Strategy for NG Templates (base first) */
  static final String NG_MERGE = "html-ng*";

  @Override
  public List<Merger> bindMerger() {

    List<Merger> merger = Lists.newLinkedList();
    merger.add(new AngularMerger(NG_MERGE, false));
    merger.add(new AngularMerger(NG_MERGE_OVERRIDE, true));
    return merger;
  }

  @Override
  public List<TriggerInterpreter> bindTriggerInterpreter() {

    return null;
  }

}

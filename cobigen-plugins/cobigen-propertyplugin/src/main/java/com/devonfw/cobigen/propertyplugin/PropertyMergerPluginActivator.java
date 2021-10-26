package com.devonfw.cobigen.propertyplugin;

import java.util.List;

import com.devonfw.cobigen.api.annotation.Activation;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.google.common.collect.Lists;

/**
 * Plug-in activator to be registered in CobiGen's PluginRegistry
 */
@Activation(byMergeStrategy = { PropertyMergerPluginActivator.PROPERTYMERGE,
PropertyMergerPluginActivator.PROPERTYMERGE_OVERRIDE })
public class PropertyMergerPluginActivator implements GeneratorPluginActivator {

  /** Property File Merge Strategy (prefer patch) */
  static final String PROPERTYMERGE_OVERRIDE = "propertymerge_override";

  /** Property File Merge Strategy (prefer base) */
  static final String PROPERTYMERGE = "propertymerge";

  @Override
  public List<Merger> bindMerger() {

    List<Merger> merger = Lists.newLinkedList();
    merger.add(new PropertyMerger(PROPERTYMERGE, false));
    merger.add(new PropertyMerger(PROPERTYMERGE_OVERRIDE, true));
    return merger;
  }

  @Override
  public List<TriggerInterpreter> bindTriggerInterpreter() {

    return null;
  }

}

package com.devonfw.cobigen.xmlplugin;

import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.annotation.Activation;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.xmlplugin.merger.delegates.MergeType;
import com.devonfw.cobigen.xmlplugin.merger.delegates.XmlMergerDelegate;
import com.google.common.collect.Lists;

/**
 * Plug-in activator to be detected by CobiGen's service loader lookup.
 */
@Activation(byMergeStrategy = { "xmlmerge_override", "xmlmerge", "xmlmerge_attachTexts",
"xmlmerge_override_attachTexts", "xmlmerge_override_validate", "xmlmerge_validate", "xmlmerge_attachTexts_validate",
"xmlmerge_override_attachTexts_validate" }, byFileExtension = { "xml", "xmi" })
public class XmlPluginActivator implements GeneratorPluginActivator {

  /**
   * defining the default location of the merge schemas
   */
  static private String defaultMergeSchemaLocation = "src/main/resources/mergeSchemas";

  /** Static Logger instance */
  private static final Logger LOG = LoggerFactory.getLogger(XmlPluginActivator.class);

  /**
   * List of mergers to update
   */
  private static List<Merger> mergerList = Lists.newLinkedList();

  @Override
  public List<Merger> bindMerger() {

    List<Merger> merger = Lists.newLinkedList();
    merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.BASEOVERWRITE, false));
    merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.BASEATTACHOROVERWRITE, false));
    merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.PATCHOVERWRITE, false));
    merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.PATCHATTACHOROVERWRITE, false));
    // mergers with validation enabled
    merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.BASEOVERWRITEVALIDATE, true));
    merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.BASEATTACHOROVERWRITEVALIDATE, true));
    merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.PATCHOVERWRITEVALIDATE, true));
    merger.add(new XmlMergerDelegate(defaultMergeSchemaLocation, MergeType.PATCHATTACHOROVERWRITEVALIDATE, true));
    mergerList = merger;
    return merger;
  }

  @Override
  public List<TriggerInterpreter> bindTriggerInterpreter() {

    return Lists.<TriggerInterpreter> newArrayList(new XmlTriggerInterpreter("xml"));
  }

  @Override
  public void setProjectRoot(Path path) {

    LOG.debug("updated project root path {}", path);
    for (Merger merger : mergerList) {
      if (merger instanceof XmlMergerDelegate) {
        ((XmlMergerDelegate) merger).updateMergeSchemaPath(path);
      }
    }
  }

}

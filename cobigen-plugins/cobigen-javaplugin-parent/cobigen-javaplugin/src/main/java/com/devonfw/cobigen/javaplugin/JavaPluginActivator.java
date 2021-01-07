package com.devonfw.cobigen.javaplugin;

import java.util.List;

import com.devonfw.cobigen.api.annotation.Activation;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.javaplugin.inputreader.JavaInputReader;
import com.devonfw.cobigen.javaplugin.merger.JavaMerger;
import com.google.common.collect.Lists;

/** Plug-in activator to be registered to the PluginRegistry of CobiGen by any client. */
@Activation(byFileExtension = { JavaInputReader.VALID_EXTENSION },
    byMergeStrategy = { JavaPluginActivator.JAVAMERGE, JavaPluginActivator.JAVAMERGE_OVERRIDE }, byFolder = true)
public class JavaPluginActivator implements GeneratorPluginActivator {

    /** Merge Strategy name for simple java merging (prefer patch) */
    static final String JAVAMERGE_OVERRIDE = "javamerge_override";

    /** Merge Strategy name for simple java merging (prefer base) */
    static final String JAVAMERGE = "javamerge";

    @Override
    public List<Merger> bindMerger() {
        List<Merger> merger = Lists.newLinkedList();
        merger.add(new JavaMerger(JAVAMERGE, false));
        merger.add(new JavaMerger(JAVAMERGE_OVERRIDE, true));
        return merger;
    }

    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return Lists.<TriggerInterpreter> newArrayList(new JavaTriggerInterpreter("java"));
    }

}

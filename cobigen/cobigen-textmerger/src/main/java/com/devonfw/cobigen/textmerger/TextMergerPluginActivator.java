package com.devonfw.cobigen.textmerger;

import java.util.List;

import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.google.common.collect.Lists;

/**
 * This Plugin Activator registers a merger, which only appends the patch text to the original existing file
 * @author mbrunnli (06.04.2014)
 */
public class TextMergerPluginActivator implements GeneratorPluginActivator {

    @Override
    public List<Merger> bindMerger() {
        List<Merger> merger = Lists.newLinkedList();
        merger.add(new TextAppender("textmerge_append", false));
        merger.add(new TextAppender("textmerge_appendWithNewLine", true));
        merger.add(new TextAppender("textmerge_override", false));
        return merger;
    }

    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return null;
    }

}

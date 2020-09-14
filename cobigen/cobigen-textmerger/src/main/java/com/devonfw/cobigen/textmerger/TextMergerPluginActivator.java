package com.devonfw.cobigen.textmerger;

import java.util.List;

import com.devonfw.cobigen.api.annotation.Activation;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.google.common.collect.Lists;

/**
 * This plug-in activator registers a merger, which only appends the patch text to the original existing file
 */
@Activation(byMergeStrategy = { TextMergerPluginActivator.TEXTMERGE_APPEND,
    TextMergerPluginActivator.TEXTMERGE_APPEND_WITH_NEW_LINE, TextMergerPluginActivator.TEXTMERGE_OVERRIDE })
public class TextMergerPluginActivator implements GeneratorPluginActivator {

    /** Merge strategy to append text parts (prefer patch) */
    static final String TEXTMERGE_OVERRIDE = "textmerge_override";

    /** Merge strategy to append text parts while adding a new line before */
    static final String TEXTMERGE_APPEND_WITH_NEW_LINE = "textmerge_appendWithNewLine";

    /** Merge strategy to append text parts (prefer base) */
    static final String TEXTMERGE_APPEND = "textmerge_append";

    @Override
    public List<Merger> bindMerger() {
        List<Merger> merger = Lists.newLinkedList();
        merger.add(new TextAppender(TEXTMERGE_APPEND, false));
        merger.add(new TextAppender(TEXTMERGE_APPEND_WITH_NEW_LINE, true));
        merger.add(new TextAppender(TEXTMERGE_OVERRIDE, false));
        return merger;
    }

    @Override
    public List<TriggerInterpreter> bindTriggerInterpreter() {
        return null;
    }

}

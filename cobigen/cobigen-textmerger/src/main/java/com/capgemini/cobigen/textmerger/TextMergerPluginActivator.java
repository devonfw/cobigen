package com.capgemini.cobigen.textmerger;

import java.util.List;

import com.capgemini.cobigen.extension.IGeneratorPluginActivator;
import com.capgemini.cobigen.extension.IMerger;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.google.common.collect.Lists;

/**
 * This Plugin Activator registers a merger, which only appends the patch text to the original existing file
 * @author mbrunnli (06.04.2014)
 */
public class TextMergerPluginActivator implements IGeneratorPluginActivator {

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public List<IMerger> bindMerger() {
        List<IMerger> merger = Lists.newLinkedList();
        merger.add(new TextAppender("textmerge_append", false));
        merger.add(new TextAppender("textmerge_appendWithNewLine", true));
        return merger;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (08.04.2014)
     */
    @Override
    public List<ITriggerInterpreter> bindTriggerInterpreter() {
        return null;
    }

}

package com.devonfw.cobigen.impl.extension;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.TextTemplateEngine;

/** Service lookup implementation. Will be called once on loading CobiGen implementation. */
public class ServiceLookup {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ServiceLookup.class);

    /**
     * Detects plug-ins as well as template engines on startup by service loader mechanism.
     */
    public static void detectServices() {

        // lookup plug-ins
        Iterator<GeneratorPluginActivator> pluginIterator =
            ServiceLoader.load(GeneratorPluginActivator.class).iterator();
        if (pluginIterator.hasNext()) {
            LOG.info("Loading plug-ins");
        } else {
            LOG.error("No plug-ins found!");
        }
        while (pluginIterator.hasNext()) {
            GeneratorPluginActivator loadedPlugin = pluginIterator.next();
            LOG.debug(" * {} found", loadedPlugin.getClass().getName());
            PluginRegistry.loadPlugin(loadedPlugin.getClass());
        }

        // lookup template engines
        Iterator<TextTemplateEngine> tempEngineIterator = ServiceLoader.load(TextTemplateEngine.class).iterator();
        if (tempEngineIterator.hasNext()) {
            LOG.info("Loading template engines");
        } else {
            LOG.error("No template engines found!");
        }
        while (tempEngineIterator.hasNext()) {
            TextTemplateEngine loadedPlugin = tempEngineIterator.next();
            LOG.debug(" * {} found", loadedPlugin.getClass().getName());
            TemplateEngineRegistry.register(loadedPlugin.getClass());
        }
    }
}

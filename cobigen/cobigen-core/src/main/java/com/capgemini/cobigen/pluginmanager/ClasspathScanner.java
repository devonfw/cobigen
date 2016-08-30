package com.capgemini.cobigen.pluginmanager;

import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import com.capgemini.cobigen.api.PluginRegistry;
import com.capgemini.cobigen.api.extension.GeneratorPluginActivator;
import com.capgemini.cobigen.api.extension.InputReader;
import com.capgemini.cobigen.api.extension.Merger;

/**
 * The ClasspathScanner scans the current classpath for sub classes of {@link GeneratorPluginActivator}. All
 * plugins will be called to bind their defined {@link Merger}s and {@link InputReader}s
 */
public class ClasspathScanner {

    /**
     * Scans the classpath for {@link GeneratorPluginActivator}s and registers {@link Merger} and
     * {@link InputReader}
     *
     * @author mbrunnli (06.04.2014)
     */
    public static void scanClasspathAndRegisterPlugins() {

        Reflections reflections =
            new Reflections(new ConfigurationBuilder().addScanners(new SubTypesScanner()));
        Set<Class<? extends GeneratorPluginActivator>> plugins =
            reflections.getSubTypesOf(GeneratorPluginActivator.class);

        for (Class<? extends GeneratorPluginActivator> plugin : plugins) {
            PluginRegistry.loadPlugin(plugin);
        }

    }
}

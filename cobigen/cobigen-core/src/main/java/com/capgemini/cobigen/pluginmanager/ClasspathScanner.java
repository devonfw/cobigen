/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.pluginmanager;

import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import com.capgemini.cobigen.extension.IGeneratorPluginActivator;
import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.extension.IMerger;

/**
 * The ClasspathScanner scans the current classpath for sub classes of {@link IGeneratorPluginActivator}. All plugins
 * will be called to bind their defined {@link IMerger}s and {@link IInputReader}s
 * 
 * @author mbrunnli (06.04.2014)
 */
public class ClasspathScanner {

    /**
     * Scans the classpath for {@link IGeneratorPluginActivator}s and registers {@link IMerger} and {@link IInputReader}
     * 
     * @author mbrunnli (06.04.2014)
     */
    public static void scanClasspathAndRegisterPlugins() {

        Reflections reflections = new Reflections(new ConfigurationBuilder().addScanners(new SubTypesScanner()));
        Set<Class<? extends IGeneratorPluginActivator>> plugins =
                reflections.getSubTypesOf(IGeneratorPluginActivator.class);

        for (Class<? extends IGeneratorPluginActivator> plugin : plugins) {
            PluginRegistry.loadPlugin(plugin);
        }

    }
}

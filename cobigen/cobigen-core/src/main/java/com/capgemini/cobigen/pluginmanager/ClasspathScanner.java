/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.pluginmanager;

import java.util.List;

import net.sf.corn.cps.CPScanner;
import net.sf.corn.cps.ClassFilter;

import com.capgemini.cobigen.extension.IGeneratorPluginActivator;
import com.capgemini.cobigen.extension.IInputReader;
import com.capgemini.cobigen.extension.IMerger;

/**
 * The ClasspathScanner scans the current classpath for sub classes of {@link IGeneratorPluginActivator}. All
 * plugins will be called to bind their defined {@link IMerger}s and {@link IInputReader}s
 * @author mbrunnli (06.04.2014)
 */
public class ClasspathScanner {

    /**
     * Scans the classpath for {@link IGeneratorPluginActivator}s and registers {@link IMerger} and
     * {@link IInputReader}
     * @author mbrunnli (06.04.2014)
     */
    @SuppressWarnings("unchecked")
    public static void scanClasspathAndRegisterPlugins() {
        List<Class<?>> classes =
            CPScanner.scanClasses(new ClassFilter().interfaceClass(IGeneratorPluginActivator.class));

        for (Class<?> clazz : classes) {
            PluginRegistry.loadPlugin((Class<IGeneratorPluginActivator>) clazz);
        }
    }
}

/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.pluginmanager;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.extension.IGeneratorPluginActivator;
import com.capgemini.cobigen.extension.IMerger;
import com.capgemini.cobigen.extension.ITriggerInterpreter;
import com.google.common.collect.Maps;

/**
 * The {@link PluginRegistry} manages registrations of {@link IMerger}s and {@link ITriggerInterpreter}s
 * 
 * @author mbrunnli (06.04.2014)
 */
public class PluginRegistry {

    /**
     * Currently registered {@link IMerger}s mapped by their type
     */
    private static Map<String, IMerger> registeredMerger = Collections.synchronizedMap(Maps
        .<String, IMerger> newHashMap());

    /**
     * Currently registered {@link ITriggerInterpreter}s mapped by their type
     */
    private static Map<String, ITriggerInterpreter> registeredTriggerInterpreter = Collections
        .synchronizedMap(Maps.<String, ITriggerInterpreter> newHashMap());

    /**
     * Assigning logger to PluginRegistry
     */
    private static final Logger LOG = LoggerFactory.getLogger(PluginRegistry.class);

    /***
     * Loads the given plug-in and registers all {@link IMerger}s and {@link ITriggerInterpreter}s bound by
     * the given plug-in
     * 
     * @param generatorPlugin
     *            plug-in to be loaded
     * @param <T>
     *            Type of the plug-in interface
     * @author mbrunnli (07.04.2014)
     */
    public static <T extends IGeneratorPluginActivator> void loadPlugin(Class<T> generatorPlugin) {

        try {
            Object plugin = generatorPlugin.newInstance();
            LOG.info("Register GeneratorPluginActivator '{}'", generatorPlugin.getClass().getCanonicalName());
            if (plugin instanceof IGeneratorPluginActivator) {
                // Collect IMerger
                if (((IGeneratorPluginActivator) plugin).bindMerger() != null) {
                    for (IMerger merger : ((IGeneratorPluginActivator) plugin).bindMerger()) {
                        PluginRegistry.registerMerger(merger);
                    }
                }
                // Collect ITriggerInterpreter
                if (((IGeneratorPluginActivator) plugin).bindTriggerInterpreter() != null) {
                    for (ITriggerInterpreter triggerInterpreter : ((IGeneratorPluginActivator) plugin)
                        .bindTriggerInterpreter()) {
                        PluginRegistry.registerTriggerInterpreter(triggerInterpreter);
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error("Could not intantiate Generator Plug-in '{}'", generatorPlugin.getClass()
                .getCanonicalName(), e);
        }
    }

    /**
     * Registers the given {@link IMerger}
     * 
     * @param merger
     *            to be registered
     * @author mbrunnli (07.04.2014)
     */
    public static void registerMerger(IMerger merger) {

        if (merger == null || StringUtils.isEmpty(merger.getType()))
            throw new IllegalArgumentException(
                "You cannot register a new Merger with merger==null or type==null or empty!");
        registeredMerger.put(merger.getType(), merger);
        LOG.info("Merger for type '{}' registered ({})", merger.getType(), merger.getClass()
            .getCanonicalName());
    }

    /**
     * Reigsters the given {@link ITriggerInterpreter}
     * 
     * @param triggerInterpreter
     *            to be registered
     * @author mbrunnli (07.04.2014)
     */
    public static void registerTriggerInterpreter(ITriggerInterpreter triggerInterpreter) {

        if (triggerInterpreter == null || StringUtils.isEmpty(triggerInterpreter.getType()))
            throw new IllegalArgumentException(
                "You cannot register a new TriggerInterpreter with triggerInterpreter==null or type==null or empty!");
        registeredTriggerInterpreter.put(triggerInterpreter.getType(), triggerInterpreter);
        LOG.info("TriggerInterpreter for type '{}' registered ({})", triggerInterpreter.getType(),
            triggerInterpreter.getClass().getCanonicalName());
    }

    /**
     * Returns the {@link IMerger} for the given mergerType
     * 
     * @param mergerType
     *            the {@link IMerger} should be able to interpret
     * @return the {@link IMerger} for the given mergerType or <code>null</code> if there is no
     *         {@link IMerger} for this mergerType
     * @author mbrunnli (07.04.2014)
     */
    public static IMerger getMerger(String mergerType) {

        if (mergerType == null) return null;
        return registeredMerger.get(mergerType);
    }

    /**
     * Returns the {@link ITriggerInterpreter} for the given triggerType
     * 
     * @param triggerType
     *            the {@link ITriggerInterpreter} should be able to interpret
     * @return the {@link ITriggerInterpreter} for the given triggerType of <code>null</code> if there is no
     * @author mbrunnli (08.04.2014)
     */
    public static ITriggerInterpreter getTriggerInterpreter(String triggerType) {

        if (triggerType == null) return null;
        return registeredTriggerInterpreter.get(triggerType);
    }

}

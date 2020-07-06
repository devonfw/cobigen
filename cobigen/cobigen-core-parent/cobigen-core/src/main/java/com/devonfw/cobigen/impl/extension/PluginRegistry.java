package com.devonfw.cobigen.impl.extension;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.NotYetSupportedException;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.impl.aop.ProxyFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The {@link PluginRegistry} manages registrations of {@link Merger}s and {@link TriggerInterpreter}s
 */
public class PluginRegistry {

    /**
     * Currently registered {@link Merger}s mapped by their type
     */
    private static Map<String, Merger> registeredMerger =
        Collections.synchronizedMap(Maps.<String, Merger> newHashMap());

    /**
     * Currently registered {@link TriggerInterpreter}s mapped by their type
     */
    private static Map<String, TriggerInterpreter> registeredTriggerInterpreter =
        Collections.synchronizedMap(Maps.<String, TriggerInterpreter> newHashMap());

    /**
     * List of registered plugins
     */
    private static List<Object> pluginsList = Lists.newLinkedList();

    /**
     * Assigning logger to PluginRegistry
     */
    private static final Logger LOG = LoggerFactory.getLogger(PluginRegistry.class);

    /***
     * Loads the given plug-in and registers all {@link Merger}s and {@link TriggerInterpreter}s bound by the
     * given plug-in
     *
     * @param generatorPlugin
     *            plug-in to be loaded
     * @param <T>
     *            Type of the plug-in interface
     */
    public static <T extends GeneratorPluginActivator> void loadPlugin(Class<T> generatorPlugin) {

        try {
            Object plugin = generatorPlugin.newInstance();
            LOG.info("Register CobiGen Plug-in '{}'.", generatorPlugin.getCanonicalName());
            if (plugin instanceof GeneratorPluginActivator) {
                // Collect IMerger
                if (((GeneratorPluginActivator) plugin).bindMerger() != null) {
                    for (Merger merger : ((GeneratorPluginActivator) plugin).bindMerger()) {
                        PluginRegistry.registerMerger(merger);
                    }
                    // adds merger plugins to notifyable list
                    pluginsList.add(plugin);
                }
                // Collect ITriggerInterpreter
                if (((GeneratorPluginActivator) plugin).bindTriggerInterpreter() != null) {
                    for (TriggerInterpreter triggerInterpreter : ((GeneratorPluginActivator) plugin)
                        .bindTriggerInterpreter()) {
                        PluginRegistry.registerTriggerInterpreter(triggerInterpreter);
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CobiGenRuntimeException(
                "Could not intantiate CobiGen Plug-in '" + generatorPlugin.getCanonicalName() + "'.", e);
        }
    }

    /**
     * Registers the given {@link Merger}
     *
     * @param merger
     *            to be registered
     */
    public static void registerMerger(Merger merger) {

        if (merger == null || StringUtils.isEmpty(merger.getType())) {
            throw new IllegalArgumentException(
                "You cannot register a new Merger with merger==null or type==null or empty!");
        }
        registeredMerger.put(merger.getType(), merger);
        LOG.debug("Merger for type '{}' registered ({}).", merger.getType(), merger.getClass().getCanonicalName());
    }

    /**
     * Registers the given {@link TriggerInterpreter}
     *
     * @param triggerInterpreter
     *            to be registered
     */
    public static void registerTriggerInterpreter(TriggerInterpreter triggerInterpreter) {

        if (triggerInterpreter == null || StringUtils.isEmpty(triggerInterpreter.getType())) {
            throw new IllegalArgumentException(
                "You cannot register a new TriggerInterpreter with triggerInterpreter==null or type==null or empty!");
        }
        registeredTriggerInterpreter.put(triggerInterpreter.getType(), triggerInterpreter);
        LOG.debug("TriggerInterpreter for type '{}' registered ({}).", triggerInterpreter.getType(),
            triggerInterpreter.getClass().getCanonicalName());
    }

    /**
     * Returns the {@link Merger} for the given mergerType
     *
     * @param mergerType
     *            the {@link Merger} should be able to interpret
     * @return the {@link Merger} for the given mergerType or <code>null</code> if there is no {@link Merger}
     *         for this mergerType
     */
    public static Merger getMerger(String mergerType) {

        if (mergerType == null) {
            return null;
        }
        Merger merger = registeredMerger.get(mergerType);
        if (merger != null) {
            merger = ProxyFactory.getProxy(merger);
        }
        return merger;
    }

    /**
     * Returns the {@link TriggerInterpreter} for the given triggerType
     *
     * @param triggerType
     *            the {@link TriggerInterpreter} should be able to interpret
     * @return the {@link TriggerInterpreter} for the given triggerType of <code>null</code> if there is no
     */
    public static TriggerInterpreter getTriggerInterpreter(String triggerType) {

        if (triggerType == null) {
            return null;
        }
        TriggerInterpreter triggerInterpreter = registeredTriggerInterpreter.get(triggerType);
        if (triggerInterpreter != null) {
            triggerInterpreter = ProxyFactory.getProxy(triggerInterpreter);
        }
        return triggerInterpreter;
    }

    /**
     * Returns a {@link Map} of all {@link TriggerInterpreter} keys.
     *
     * @return all {@link TriggerInterpreter} keys as a set of strings.
     */
    public static Set<String> getTriggerInterpreterKeySet() {
        return new HashSet<>(registeredTriggerInterpreter.keySet());
    }

    /**
     * Notifies plugins about the new template root path
     *
     * @param configFolder
     *            Path to update on registered plugins
     */
    public static void notifyPlugins(Path configFolder) {

        for (Object plugin : pluginsList) {
            if (plugin instanceof GeneratorPluginActivator) {
                try {
                    ((GeneratorPluginActivator) plugin).setProjectRoot(configFolder);
                } catch (NotYetSupportedException e) {
                    LOG.debug("setProjectRoot() method is not implemented in this plugin yet!", e);
                }
            }
        }
    }

}

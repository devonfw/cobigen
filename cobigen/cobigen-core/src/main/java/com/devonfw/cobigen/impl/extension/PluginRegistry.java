package com.devonfw.cobigen.impl.extension;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.annotation.Activation;
import com.devonfw.cobigen.api.annotation.ReaderPriority;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.extension.Priority;
import com.devonfw.cobigen.api.extension.TriggerInterpreter;
import com.devonfw.cobigen.impl.aop.ProxyFactory;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.primitives.SignedBytes;

/**
 * The {@link PluginRegistry} manages registrations of {@link Merger}s and {@link TriggerInterpreter}s
 */
public class PluginRegistry {

  /**
   * Currently registered {@link Merger}s mapped by their merge strategy
   */
  private static Map<String, Merger> registeredMerger = Maps.<String, Merger> newHashMap();

  /** Currently registered {@link TriggerInterpreter}s mapped by their type */
  private static Map<String, TriggerInterpreter> registeredTriggerInterpreter = Maps
      .<String, TriggerInterpreter> newHashMap();

  /** Currently registered {@link TriggerInterpreter}s mapped by their supporting file extensions */
  private static Multimap<String, TriggerInterpreter> registeredTriggerInterpreterByFileExtension = HashMultimap
      .<String, TriggerInterpreter> create();

  /** Key-Placeholder for a path representing a folder */
  private static final String FOLDER = "$";

  /** List of registered plugins */
  private static Map<Class<? extends GeneratorPluginActivator>, GeneratorPluginActivator> loadedPlugins = new HashMap<>();

  /** Assigning logger to PluginRegistry */
  private static final Logger LOG = LoggerFactory.getLogger(PluginRegistry.class);

  /***
   * Loads the given plug-in and registers all {@link Merger}s and {@link TriggerInterpreter}s bound by the given
   * plug-in
   *
   * @param generatorPlugin plug-in to be loaded
   * @param <T> Type of the plug-in interface
   * @return the instantiated {@link GeneratorPluginActivator}
   */
  private static <T extends GeneratorPluginActivator> GeneratorPluginActivator loadPlugin(Class<T> generatorPlugin) {

    try {
      Object plugin = generatorPlugin.newInstance();
      LOG.info("Register CobiGen Plug-in '{}'.", generatorPlugin.getCanonicalName());
      if (plugin instanceof GeneratorPluginActivator) {
        // Collect Mergers
        GeneratorPluginActivator activator = (GeneratorPluginActivator) plugin;
        if (activator.bindMerger() != null) {
          for (Merger merger : activator.bindMerger()) {
            registerMerger(merger);
          }
          // adds merger plugins to notifyable list
        }
        // Collect TriggerInterpreters
        if (activator.bindTriggerInterpreter() != null) {
          for (TriggerInterpreter triggerInterpreter : activator.bindTriggerInterpreter()) {
            registerTriggerInterpreter(triggerInterpreter, activator);
          }
        }
        loadedPlugins.put(activator.getClass(), activator);
        return activator;
      } else {
        LOG.warn("Instantiated plugin of class {}, which is not subclass of {}", plugin.getClass().getCanonicalName(),
            GeneratorPluginActivator.class.getCanonicalName());
        return null;
      }
    } catch (InstantiationException | IllegalAccessException e) {
      throw new CobiGenRuntimeException(
          "Could not intantiate CobiGen Plug-in '" + generatorPlugin.getCanonicalName() + "'.", e);
    }
  }

  /**
   * Registers the given {@link Merger}
   *
   * @param merger to be registered
   */
  private static void registerMerger(Merger merger) {

    if (merger == null || StringUtils.isEmpty(merger.getType())) {
      throw new IllegalArgumentException("You cannot register a new Merger with merger==null or type==null or empty!");
    }
    registeredMerger.put(merger.getType(), merger);
    LOG.debug("Merger for type '{}' registered ({}).", merger.getType(), merger.getClass().getCanonicalName());
  }

  /**
   * Registers the given {@link TriggerInterpreter}
   *
   * @param triggerInterpreter to be registered
   * @param plugin the plugin the trigger interpreter is located in
   */
  public static void registerTriggerInterpreter(TriggerInterpreter triggerInterpreter,
      GeneratorPluginActivator plugin) {

    if (triggerInterpreter == null || StringUtils.isEmpty(triggerInterpreter.getType())) {
      throw new IllegalArgumentException(
          "You cannot register a new TriggerInterpreter with triggerInterpreter==null or type==null or empty!");
    }
    registeredTriggerInterpreter.put(triggerInterpreter.getType(), triggerInterpreter);
    Activation annotation = plugin.getClass().getAnnotation(Activation.class);
    if (annotation != null) {
      for (String ext : annotation.byFileExtension()) {
        registeredTriggerInterpreterByFileExtension.put(ext, triggerInterpreter);
      }
      if (annotation.byFolder()) {
        registeredTriggerInterpreterByFileExtension.put(FOLDER, triggerInterpreter);
      }
    }
    LOG.debug("TriggerInterpreter for type '{}' registered ({}).", triggerInterpreter.getType(),
        triggerInterpreter.getClass().getCanonicalName());
  }

  /**
   * Returns the {@link Merger} for the given merge strategy
   *
   * @param mergeStrategy the {@link Merger} should be able to interpret
   * @return the {@link Merger} for the given mergerType or <code>null</code> if there is no {@link Merger} for this
   *         mergerType
   */
  public static Merger getMerger(String mergeStrategy) {

    if (mergeStrategy == null) {
      return null;
    }

    Merger merger = registeredMerger.get(mergeStrategy);
    if (merger == null) {
      LOG.debug("Trying to find merger for type '{}' in {} registered plugins.", mergeStrategy,
          ClassServiceLoader.getGeneratorPluginActivatorClasses().size());
      for (Class<? extends GeneratorPluginActivator> activatorClass : ClassServiceLoader
          .getGeneratorPluginActivatorClasses()) {
        LOG.debug("Checking found plug-in activator '{}'", activatorClass);
        if (activatorClass.isAnnotationPresent(Activation.class)) {
          Activation activation = activatorClass.getAnnotation(Activation.class);
          String[] byMergeStrategy = activation.byMergeStrategy();
          if (LOG.isDebugEnabled()) {
            LOG.debug("Plug-in will be activated by merge strategies '{}'.",
                Arrays.stream(byMergeStrategy).collect(Collectors.joining(",")));
          }
          Arrays.sort(byMergeStrategy);
          if (Arrays.binarySearch(byMergeStrategy, mergeStrategy) >= 0) {
            loadPlugin(activatorClass);
            break;
          } else {
            LOG.debug("Merge strategy not found. Skipping.");
          }
        } else {
          LOG.debug("Activator annotation not present. Skipping.");
        }
      }
      merger = registeredMerger.get(mergeStrategy);
    }
    if (merger != null) {
      merger = ProxyFactory.getProxy(merger);
    }
    return merger;
  }

  /**
   * Returns the {@link TriggerInterpreter} for the given triggerType
   *
   * @param triggerType the {@link TriggerInterpreter} should be able to interpret
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
   * @param inputPath the path of the input to be read to just return the valid {@link TriggerInterpreter}s in order
   *        sorted by {@link Priority}
   *
   * @return all {@link TriggerInterpreter} keys as a set of strings.
   */
  public static List<TriggerInterpreter> getTriggerInterpreters(Path inputPath) {

    String extension;
    if (inputPath.toFile().isFile()) {
      extension = FilenameUtils.getExtension(inputPath.getFileName().toString());
      LOG.debug("Trying to find trigger interpreter by file extension '{}'", extension);
      for (Class<? extends GeneratorPluginActivator> activatorClass : ClassServiceLoader
          .getGeneratorPluginActivatorClasses()) {
        LOG.debug("Checking found plug-in activator '{}'", activatorClass);
        if (activatorClass.isAnnotationPresent(Activation.class)) {
          Activation activation = activatorClass.getAnnotation(Activation.class);
          String[] byFileExtension = activation.byFileExtension();
          if (LOG.isDebugEnabled()) {
            LOG.debug("Plug-in will be activated by file extensions '{}'.",
                Arrays.stream(byFileExtension).collect(Collectors.joining(",")));
          }
          Arrays.sort(byFileExtension);
          if (Arrays.binarySearch(byFileExtension, extension) >= 0 && !loadedPlugins.containsKey(activatorClass)) {
            loadPlugin(activatorClass);
          } else {
            LOG.debug("File extension not found. Skipping.");
          }
        } else {
          LOG.debug("Activator annotation not present. Skipping.");
        }
      }
    } else { // directory
      extension = FOLDER;
      LOG.debug("Trying to find trigger interpreter by for folder inputs");
      for (Class<? extends GeneratorPluginActivator> activatorClass : ClassServiceLoader
          .getGeneratorPluginActivatorClasses()) {
        LOG.debug("Checking found plug-in activator '{}'", activatorClass);
        if (activatorClass.isAnnotationPresent(Activation.class)) {
          Activation activation = activatorClass.getAnnotation(Activation.class);
          if (activation.byFolder() && !loadedPlugins.containsKey(activatorClass)) {
            loadPlugin(activatorClass);
          }
        } else {
          LOG.debug("Activator annotation not present. Skipping.");
        }
      }
    }

    List<TriggerInterpreter> sortedPlugins = registeredTriggerInterpreterByFileExtension.get(extension).stream()
        .sorted((a, b) -> {
          Priority priorityA = getPriority(a.getClass());
          Priority priorityB = getPriority(b.getClass());
          return SignedBytes.compare(priorityA.getRank(), priorityB.getRank());
        }).collect(Collectors.toList());

    return sortedPlugins;
  }

  /**
   * Extracts the {@link ReaderPriority} of a trigger interpreter
   *
   * @param clazz class to get the {@link ReaderPriority} annotation from (commonly the TriggerInterpreter classes)
   * @return the priority of the input reader
   */
  private static Priority getPriority(Class<? extends TriggerInterpreter> clazz) {

    Priority priority;
    if (clazz.getClass().isAnnotationPresent(ReaderPriority.class)) {
      ReaderPriority[] annotation = clazz.getClass().getAnnotationsByType(ReaderPriority.class);
      priority = annotation[0].value();
    } else {
      try {
        priority = (Priority) ReaderPriority.class.getMethod("value").getDefaultValue();
      } catch (NoSuchMethodException | SecurityException e) {
        LOG.error(
            "Could not find value() method of ReaderPriority. This should be an invalid case. Setting priority to hardcoded LOW to proceed. Please anyhow report a bug please.");
        priority = Priority.LOW;
      }
    }
    return priority;
  }

  /**
   * Notifies plug-ins about the new template root path
   *
   * @param configFolder Path to update on registered plug-ins
   */
  public static void notifyPlugins(Path configFolder) {

    for (Object plugin : loadedPlugins.values()) {
      ((GeneratorPluginActivator) plugin).setProjectRoot(configFolder);
    }
  }

}

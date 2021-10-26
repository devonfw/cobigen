package com.devonfw.cobigen.eclipse.common.tools;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Util functions for {@link Map}s
 */
public final class MapUtils {

  /**
   * Recursive add of a new value to a nested {@link Set} within a {@link Map}
   *
   * @param <K> the key type of the map
   * @param <V> The value type of the internal {@link Set}
   * @param result reference to the result {@link Map}
   * @param value {@link Set} value to be added
   * @param key {@link Map} key to add the value to
   */
  public static <K, V> void deepMapAdd(Map<K, Set<V>> result, K key, V value) {

    if (!result.containsKey(key)) {
      result.put(key, Sets.<V> newHashSet());
    }
    result.get(key).add(value);
  }

  /**
   * Recursive add for a {@link Set} value within a {@link Map}
   *
   * @param <K> the key type of the map
   * @param <V> The value type of the internal {@link Set}
   * @param result reference to the result {@link Map}
   * @param toAdd reference to the {@link Map}, which entries should be added recursively
   */
  public static <K, V> void deepMapAddAll(Map<K, Set<V>> result, Map<K, Set<V>> toAdd) {

    for (Entry<K, Set<V>> entry : toAdd.entrySet()) {
      if (!result.containsKey(entry.getKey())) {
        result.put(entry.getKey(), new HashSet<>(entry.getValue()));
      } else {
        result.get(entry.getKey()).addAll(entry.getValue());
      }
    }
  }
}

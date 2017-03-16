package com.capgemini.cobigen.eclipse.common.tools;

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
     * @param <T>
     *            The value type of the internal {@link Set}
     * @param result
     *            reference to the result {@link Map}
     * @param value
     *            {@link Set} value to be added
     * @param key
     *            {@link Map} key to add the value to
     */
    public static <T> void deepMapAdd(Map<String, Set<T>> result, String key, T value) {
        if (!result.containsKey(key)) {
            result.put(key, Sets.<T> newHashSet());
        }
        result.get(key).add(value);
    }

    /**
     * Recursive add for a {@link Set} value within a {@link Map}
     * @param <T>
     *            The value type of the internal {@link Set}
     * @param result
     *            reference to the result {@link Map}
     * @param toAdd
     *            reference to the {@link Map}, which entries should be added recursively
     */
    public static <T> void deepMapAddAll(Map<String, Set<T>> result, Map<String, Set<T>> toAdd) {
        for (Entry<String, Set<T>> entry : toAdd.entrySet()) {
            if (!result.containsKey(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            } else {
                result.get(entry.getKey()).addAll(entry.getValue());
            }
        }
    }
}

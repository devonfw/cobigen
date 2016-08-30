package com.capgemini.cobigen.impl.config.constant;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Version steps of the templates configuration.
 * @author mbrunnli (Jun 22, 2015)
 */
public enum ContextConfigurationVersion {

    /**
     * Initial release.
     */
    v1_0(1f),

    /**
     * ChangeLog:
     * <ul>
     * <li>new recursiveInputRetrieval attribute added for containerMatcher (#81)</li>
     * <li>new accumulationType attribute added for matchers (#93)</li>
     * </ul>
     */
    v2_0(2f),

    /**
     * ChangeLog:
     * <ul>
     * <li>target namespace changed</li>
     * </ul>
     */
    v2_1(2.1f);

    /** Comparable float representation of the version number. */
    private float floatRepresentation;

    /**
     * The constructor.
     * @param floatRepresentation
     *            comparable float representation of the version number.
     * @author mbrunnli (May 17, 2016)
     */
    private ContextConfigurationVersion(float floatRepresentation) {
        this.floatRepresentation = floatRepresentation;
    }

    /**
     * @return the comparable float representation value of the version.
     * @author mbrunnli (May 17, 2016)
     */
    public float getFloatRepresentation() {
        return floatRepresentation;
    }

    /**
     * Get latest context configuration floatRepresentation supported by this CobiGen release.
     * @return latest context configuration floatRepresentation supported by this CobiGen release.
     * @author mbrunnli (Jun 24, 2015)
     */
    public static ContextConfigurationVersion getLatest() {
        return values()[values().length - 1];
    }

    /**
     * Returns the sorted float representations of the enum's values.
     * @return a sorted List
     * @author mbrunnli (May 17, 2016)
     */
    public static List<Float> valuesSorted() {
        List<Float> floatVersions = Lists.newArrayListWithExpectedSize(values().length);
        for (ContextConfigurationVersion v : values()) {
            floatVersions.add(v.getFloatRepresentation());
        }
        Collections.sort(floatVersions);
        return floatVersions;
    }

    @Override
    public String toString() {
        return name().replace("_", ".");
    };
}

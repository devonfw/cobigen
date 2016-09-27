package com.capgemini.cobigen.impl.config.constant;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Version steps of the templates configuration.
 * @author mbrunnli (Jun 22, 2015)
 */
public enum TemplatesConfigurationVersion {

    /**
     * Initial release.
     */
    v1_0(1f),

    /**
     * ChangeLog:
     * <ul>
     * <li>new nodes template-scan and templateExtension added (#55)</li>
     * </ul>
     */
    v1_2(1.2f),

    /**
     * ChangeLog:
     * <ul>
     * <li>new templateScanRef node added as child for increment to reference templateScans (#118)</li>
     * <li>attribute id for templates now optional + new attribute 'name' introduced (#104)</li>
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
    private TemplatesConfigurationVersion(float floatRepresentation) {
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
     * Get latest context configuration version supported by this CobiGen release.
     * @return latest context configuration version supported by this CobiGen release.
     * @author mbrunnli (Jun 24, 2015)
     */
    public static TemplatesConfigurationVersion getLatest() {
        return values()[values().length - 1];
    }

    /**
     * Returns the sorted float representations of the enum's values.
     * @return a sorted List
     * @author mbrunnli (May 17, 2016)
     */
    public static List<Float> valuesSorted() {
        List<Float> floatVersions = Lists.newArrayListWithExpectedSize(values().length);
        for (TemplatesConfigurationVersion v : values()) {
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

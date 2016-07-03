package com.capgemini.cobigen.config.constant;

/**
 * Version steps of the templates configuration.
 * @author mbrunnli (Jun 22, 2015)
 */
public enum TemplatesConfigurationVersion {

    /**
     * Initial release.
     */
    v1_0,

    /**
     * ChangeLog:
     * <ul>
     * <li>new nodes template-scan and templateExtension added (#55)</li>
     * </ul>
     */
    v1_2,

    /**
     * ChangeLog:
     * <ul>
     * <li>new templateScanRef node added as child for increment to reference templateScans (#118)</li>
     * <li>attribute id for templates now optional + new attribute 'name' introduced (#104)</li>
     * <li>target namespace changed</li>
     * </ul>
     */
    v2_1;

    /**
     * {@inheritDoc}
     * @author mbrunnli (Jun 22, 2015)
     */
    @Override
    public String toString() {
        return name().replace("_", ".");
    };

    /**
     * Get latest context configuration version supported by this CobiGen release.
     * @return latest context configuration version supported by this CobiGen release.
     * @author mbrunnli (Jun 24, 2015)
     */
    public static TemplatesConfigurationVersion getLatest() {
        return values()[values().length - 1];
    }
}

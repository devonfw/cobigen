package com.capgemini.cobigen.config.upgrade.version;

/**
 * Version steps of the templates configuration.
 * @author mbrunnli (Jun 22, 2015)
 */
public enum ContextConfigurationVersion {

    /**
     * Initial release.
     */
    v1_0,

    /**
     * ChangeLog:
     * <ul>
     * <li>new recursiveInputRetrieval attribute added for containerMatcher (#81)</li>
     * <li>new accumulationType attribute added for matchers (#93)</li>
     * </ul>
     */
    v2_0,

    /**
     * ChangeLog:
     * <ul>
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
}

package com.capgemini.cobigen.api.constants;

import java.util.Properties;

/**
 * External accessible configuration constants.
 */
public class ConfigurationConstants {

    /** Merge strategy to override complete file rather than merging at all */
    public static final String MERGE_STRATEGY_OVERRIDE = "override";

    /** Context configuration file name */
    public static final String CONTEXT_CONFIG_FILENAME = "context.xml";

    /** Templates configuration file name */
    public static final String TEMPLATES_CONFIG_FILENAME = "templates.xml";

    /** Filename of the {@link Properties} used to customize cobigen properties and template relocation. */
    public static final String COBIGEN_PROPERTIES = "cobigen.properties";
}

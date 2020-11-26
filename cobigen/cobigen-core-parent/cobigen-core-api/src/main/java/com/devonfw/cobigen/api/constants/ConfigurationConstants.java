package com.devonfw.cobigen.api.constants;

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

    /** Name of the CobiGen home folder, placed on the users home directory */
    public static final String COBIGEN_HOME_FOLDER = ".cobigen";

    /** Name of the templates folder */
    public static final String TEMPLATES_FOLDER = "templates";

    /** Resource folder containing templates */
    public static final String TEMPLATE_RESOURCE_FOLDER = "src/main/templates";

    /** Resource folder containing merge schemas */
    public static final String MERGE_SCHEMA_RESOURCE_FOLDER = "src/main/resources/mergeSchemas";

    /** Delimiter splitting the template folder and value of references in templates.xml files */
    public static final String REFERENCE_DELIMITER = "::";
}

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
    
    /** Name of the extracted templates project */
    public static final String COBIGEN_TEMPLATES = "CobiGen_Templates";
    
    /** Name of the environment variable pointing to cobigen configuration file */
    public static final String COBIGEN_CONFIG_DIR = "COBIGEN_CONFIG_DIR";
    
    /** Name of cobigen configuration file */
    public static final String COBIGEN_CONFIG_FILE = ".cobigen";
    
    /**Name of configuration key for location of templates. It could be Cobigen_Templates folder, an artifact or a maven dependency*/
    public static final String COBIGEN_CONFIG_TEMPLATES_LOCATION_KEY = "cobigen.templates.templates_location";
    
    /** Resource folder containing templates */
    public static final String TEMPLATE_RESOURCE_FOLDER = "src/main/templates";

    /** Resource folder containing merge schemas */
    public static final String MERGE_SCHEMA_RESOURCE_FOLDER = "src/main/resources/mergeSchemas";

    /** Delimiter splitting the template folder and value of references in templates.xml files */
    public static final String REFERENCE_DELIMITER = "::";
}

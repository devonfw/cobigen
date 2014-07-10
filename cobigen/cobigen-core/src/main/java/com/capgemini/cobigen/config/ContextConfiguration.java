/*******************************************************************************
 * Copyright � Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.capgemini.cobigen.config.entity.Trigger;
import com.capgemini.cobigen.config.reader.ContextConfigurationReader;
import com.capgemini.cobigen.exceptions.InvalidConfigurationException;
import com.capgemini.cobigen.util.SystemUtil;

/**
 * The {@link ContextConfiguration} is a configuration data wrapper for all information about templates and
 * the target destination for the generated data.
 * @author mbrunnli (04.02.2013)
 */
public class ContextConfiguration {

    /**
     * The enumeration equivalent to the enumeration of the configuration xml variableType
     * @author mbrunnli (04.02.2013)
     */
    public enum ContextSetting {
        /**
         * The root path of the generator project, top folder of context.xml and all context template folders
         */
        GeneratorProjectRootPath,
        /**
         * The root path of the target folder all relative target paths start with
         */
        GenerationTargetRootPath,
    }

    /**
     * The current settings
     */
    private Map<ContextSetting, String> contextSettings;

    /**
     * All available {@link Trigger}s
     */
    private Map<String, Trigger> triggers;

    /**
     * context.xml file
     */
    private File contextXmlFile;

    /**
     * Creates a new {@link ContextConfiguration} with the contents initially loaded from the context.xml
     * @param rootConfigFolder
     *            the root folder containing the context.xml and all templates, configurations etc.
     * @throws InvalidConfigurationException
     *             thrown if the {@link File} is not valid with respect to the context.xsd
     * @throws IOException
     *             thrown if the {@link File} could not be accessed
     * @author mbrunnli (04.02.2013)
     */
    public ContextConfiguration(File rootConfigFolder) throws IOException, InvalidConfigurationException {
        initializeSettings();
        set(ContextSetting.GeneratorProjectRootPath, rootConfigFolder.getAbsolutePath());
        contextXmlFile =
            new File(rootConfigFolder.getAbsolutePath() + SystemUtil.FILE_SEPARATOR + "context.xml");

        readConfiguration();
    }

    /**
     * Reads the configuration from {@link #contextXmlFile}
     * @throws InvalidConfigurationException
     *             thrown if the {@link File} is not valid with respect to the context.xsd
     * @author mbrunnli (10.04.2013)
     */
    private void readConfiguration() throws InvalidConfigurationException {
        ContextConfigurationReader reader = new ContextConfigurationReader(contextXmlFile);
        triggers = reader.loadTriggers();
    }

    /**
     * Reloads the configuration from source. This function might be called if the configuration file has
     * changed in a running system
     * @throws IOException
     *             if the file could not be accessed
     * @throws InvalidConfigurationException
     *             thrown if the {@link File} is not valid with respect to the context.xsd
     * @author mbrunnli (10.04.2013)
     */
    public void reloadConfigurationFromFile() throws IOException, InvalidConfigurationException {
        readConfiguration();
    }

    /**
     * Initializes all settings with the empty string
     * 
     * @author mbrunnli (05.04.2013)
     */
    private void initializeSettings() {
        contextSettings = new HashMap<ContextSetting, String>();
        for (ContextSetting cv : ContextSetting.values()) {
            contextSettings.put(cv, "");
        }
    }

    /**
     * Returns the value for the given variable
     * @param variable
     *            {@link ContextSetting}
     * @return the value for the given variable
     * @author mbrunnli (06.02.2013)
     */
    public String get(ContextSetting variable) {
        return contextSettings.get(variable);
    }

    /**
     * Temporarily set the {@link ContextSetting} to the given variable
     * @param variable
     *            {@link ContextSetting}
     * @param value
     *            which should be stored temporarily
     * @author mbrunnli (06.02.2013)
     */
    public void set(ContextSetting variable, String value) {
        contextSettings.put(variable, value);
    }

    /**
     * Returns all registered {@link Trigger}s
     * @return all registered {@link Trigger}s
     * @author mbrunnli (09.04.2014)
     */
    public List<Trigger> getTriggers() {
        return new LinkedList<Trigger>(triggers.values());
    }

    /**
     * Returns the {@link Trigger} with the given id
     * @param id
     *            of the {@link Trigger} to be searched
     * @return the {@link Trigger} with the given id or <code>null</code> if there is no
     * @author mbrunnli (09.04.2014)
     */
    public Trigger getTrigger(String id) {
        return triggers.get(id);
    }

}

package com.devonfw.cobigen.impl.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.google.common.collect.Maps;

/**
 * Cached in-memory CobiGen configuration.
 */
public class ConfigurationHolder {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationHolder.class);

    /** Cached templates configurations. Configuration File URI -> Trigger ID -> configuration instance */
    private Map<Path, Map<String, TemplatesConfiguration>> templatesConfigurations = Maps.newHashMap();

    /** Cached context configuration */
    private ContextConfiguration contextConfiguration;

    /** Root path of the configuration */
    private Path configurationPath;

    /**
     * Map of the external triggers to use. We need this variable to properly organize the different external
     * templatesConfiguration
     */
    private Map<String, Trigger> externalTriggers = new HashMap();

    /**
     * {@link TemplatesConfiguration} to be used for storing the external increments
     */
    private TemplatesConfiguration externalTemplatesConfig;

    /**
     * Creates a new {@link ConfigurationHolder} which serves as a cache for CobiGen's external configuration.
     * @param configurationPath
     *            root path of the configuration
     */
    public ConfigurationHolder(Path configurationPath) {
        this.configurationPath = configurationPath;
    }

    /**
     * Reads the {@link TemplatesConfiguration} from cache or from file if not present in cache.
     * @param trigger
     *            to get matcher declarations from
     * @return the {@link TemplatesConfiguration}
     * @throws InvalidConfigurationException
     *             if the configuration is not valid
     */
    public TemplatesConfiguration readTemplatesConfiguration(Trigger trigger) {

        Path templateFolder = Paths.get(trigger.getTemplateFolder());
        if (!templatesConfigurations.containsKey(templateFolder)) {
            templatesConfigurations.put(templateFolder, Maps.<String, TemplatesConfiguration> newHashMap());

            TemplatesConfiguration config = new TemplatesConfiguration(configurationPath, trigger, this);
            templatesConfigurations.get(templateFolder).put(trigger.getId(), config);
        }

        return templatesConfigurations.get(templateFolder).get(trigger.getId());
    }

    /**
     * Reads the {@link TemplatesConfiguration} from cache or from file if not present in cache, just storing
     * the external increment we want.
     * @param trigger
     *            to get matcher declarations from
     * @param incrementToSearch
     *            name of the increment to search
     * @return the {@link TemplatesConfiguration}
     * @throws InvalidConfigurationException
     *             if the configuration is not valid
     */
    public TemplatesConfiguration readTemplatesConfiguration(Trigger trigger, String incrementToSearch) {

        Path templateFolder = Paths.get(trigger.getTemplateFolder());
        templatesConfigurations.put(templateFolder, Maps.<String, TemplatesConfiguration> newHashMap());

        Path externalIncrementPath = configurationPath.normalize();

        // As we read one increment each time, we want to properly store everything in the same map so that
        // they don't get overwritten. Therefore, we first check whether the trigger has been yet loaded or
        // not
        if (externalTriggers.containsKey(trigger.getId())) {
            externalTemplatesConfig.loadSpecificIncrement(incrementToSearch);
        } else {
            externalTemplatesConfig =
                new TemplatesConfiguration(externalIncrementPath, trigger, this, incrementToSearch);
            externalTriggers.put(trigger.getId(), trigger);
        }
        templatesConfigurations.get(templateFolder).put(trigger.getId(), externalTemplatesConfig);

        return templatesConfigurations.get(templateFolder).get(trigger.getId());
    }

    /**
     * Reads the {@link ContextConfiguration} from cache or from file if not present in cache.
     * @return the {@link ContextConfiguration}
     * @throws InvalidConfigurationException
     *             if the configuration is not valid
     */
    public ContextConfiguration readContextConfiguration() {
        if (contextConfiguration == null) {
            contextConfiguration = new ContextConfiguration(configurationPath);
        }
        return contextConfiguration;
    }

    /**
     * We use this method just for retrieving all the {@link TemplatesConfiguration}.
     * @return Map containing all the {@link TemplatesConfiguration}
     */
    public Map<Path, Map<String, TemplatesConfiguration>> getTemplatesConfigurations() {
        return templatesConfigurations;
    }

    /**
     * Used for getting the external trigger id
     * @return the external trigger id
     */
    public Map<String, Trigger> getExternalTriggers() {
        return externalTriggers;
    }

}

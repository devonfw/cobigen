package com.capgemini.cobigen.impl.config;

import java.nio.file.Path;
import java.util.Map;

import com.capgemini.cobigen.api.constants.ConfigurationConstants;
import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.impl.config.entity.Trigger;
import com.google.common.collect.Maps;

/**
 * Cached in-memory CobiGen configuration.
 */
public class ConfigurationHolder {

    /** Cached templates configurations. Configuration File URI -> Trigger ID -> configuration instance */
    private Map<String, Map<String, TemplatesConfiguration>> templatesConfigurations = Maps.newHashMap();

    /** Cached context configuration */
    private ContextConfiguration contextConfiguration;

    /** Root path of the configuration */
    private Path configurationPath;

    /**
     * Creates a new {@link ConfigurationHolder} which serves as a cache for CobiGen's external configuration.
     * @param configurationPath
     *            root path of the configuration
     */
    public ConfigurationHolder(Path configurationPath) {
        this.configurationPath = configurationPath;
    }

    /**
     * Removes all instances of the {@link TemplatesConfiguration} given by the provided {@link Path} from the
     * cache.
     * @param path
     *            {@link Path} of the {@link ConfigurationConstants#TEMPLATES_CONFIG_FILENAME templates
     *            configuration file}.
     */
    public void invalidateTemplatesConfiguration(Path path) {
        templatesConfigurations.remove(path.toUri().toString());
    }

    /**
     * Removes the {@link ContextConfiguration} from the cache.
     */
    public void invalidateContextConfiguration() {
        contextConfiguration = null;
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

        if (!templatesConfigurations.containsKey(trigger.getTemplateFolder())) {
            templatesConfigurations.put(trigger.getTemplateFolder(),
                Maps.<String, TemplatesConfiguration> newHashMap());

            TemplatesConfiguration config = new TemplatesConfiguration(configurationPath, trigger);
            templatesConfigurations.get(trigger.getTemplateFolder()).put(trigger.getId(), config);
        }

        return templatesConfigurations.get(trigger.getTemplateFolder()).get(trigger.getId());
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
}

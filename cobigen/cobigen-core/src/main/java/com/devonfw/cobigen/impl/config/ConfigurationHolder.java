package com.devonfw.cobigen.impl.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.google.common.collect.Maps;

/**
 * Cached in-memory CobiGen configuration.
 */
public class ConfigurationHolder {

    /** Cached templates configurations. Configuration File URI -> Trigger ID -> configuration instance */
    private Map<Path, Map<String, TemplatesConfiguration>> templatesConfigurations = Maps.newHashMap();

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
        // updates the root template path and informs all of its observers
        PluginRegistry.notifyPlugins(configurationPath);
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

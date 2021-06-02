package com.devonfw.cobigen.impl.config;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.config.entity.Trigger;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.util.FileSystemUtil;
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

    /** The OS filesystem path of the configuration */
    private URI configurationLocation;

    /**
     * Creates a new {@link ConfigurationHolder} which serves as a cache for CobiGen's external configuration.
     * @param configurationPath
     *            root path of the configuration (can be / for internal nio ZIP fileystems)
     * @param configurationLocation
     *            the OS Filesystem path of the configuration location.
     */
    public ConfigurationHolder(Path configurationPath, URI configurationLocation) {
        this.configurationPath = configurationPath;
        this.configurationLocation = configurationLocation;
        // updates the root template path and informs all of its observers
        PluginRegistry.notifyPlugins(configurationPath);
    }

    /**
     * @return <code>true</code> if the configuration is based in a JAR file
     */
    public boolean isJarConfig() {
        return FileSystemUtil.isZipFile(configurationLocation);
    }

    /**
     * @return the path of the configuration based on the OS filesystem. It could be a .jar file or a maven
     *         project root folder
     */
    public URI getConfigurationLocation() {
        return configurationLocation;
    }

    /**
     * @return the path within the configuration. Might be a different file system than OS in case of a .jar
     *         configuration
     */
    public Path getConfigurationPath() {
        return configurationPath;
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

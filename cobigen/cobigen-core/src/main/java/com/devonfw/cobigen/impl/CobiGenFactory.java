package com.devonfw.cobigen.impl;

import java.net.URI;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Path;
import java.util.Objects;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.HealthCheck;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.ConfigurationUtil;
import com.devonfw.cobigen.impl.aop.BeanFactory;
import com.devonfw.cobigen.impl.aop.ProxyFactory;
import com.devonfw.cobigen.impl.config.ConfigurationHolder;
import com.devonfw.cobigen.impl.config.ContextConfiguration;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.healthcheck.HealthCheckImpl;
import com.devonfw.cobigen.impl.util.ExtractTemplatesUtil;
import com.devonfw.cobigen.impl.util.FileSystemUtil;

/**
 * CobiGen's Factory to create new instances of {@link CobiGen}.
 */
public class CobiGenFactory {

    /**
     * Creates a new {@link CobiGen} with a given {@link ContextConfiguration}.
     *
     * @param configFileOrFolder
     *            the root folder containing the context.xml and all templates, configurations etc.
     * @return a new instance of {@link CobiGen}
     * @throws InvalidConfigurationException
     *             if the context configuration could not be read properly.
     */
    public static CobiGen create(URI configFileOrFolder) throws InvalidConfigurationException {
        Objects.requireNonNull(configFileOrFolder, "The URI pointing to the configuration could not be null.");

        Path configFolder = FileSystemUtil.createFileSystemDependentPath(configFileOrFolder);

        ConfigurationHolder configurationHolder = new ConfigurationHolder(configFolder);
        BeanFactory beanFactory = new BeanFactory();
        beanFactory.addManuallyInitializedBean(configurationHolder);
        CobiGen createBean = beanFactory.createBean(CobiGen.class);
        // Notifies all plugins of new template root path
        PluginRegistry.notifyPlugins(configFolder);
        return createBean;
    }

    /**
     * Creates a new {@link CobiGen}
     *
     * @return a new instance of {@link CobiGen}
     * @throws InvalidConfigurationException
     *             if the context configuration could not be read properly.
     */
    public static CobiGen create() throws InvalidConfigurationException {
        URI configFileOrFolder = ConfigurationUtil.findTemplatesLocation();
        if (configFileOrFolder == null) {
            throw new InvalidConfigurationException(
                "No valid templates can be found. Please configure your cobigen configuration file properly or place the templates in cobigen home directory. Creating CobiGen instance aborted.");
        }
        return create(configFileOrFolder);
    }

    /**
     * Extracts templates project to the given path
     * @return path to have the templates extracted
     * @throws DirectoryNotEmptyException
     *             if the directory is not empty
     */
    public static Path extractTemplates() throws DirectoryNotEmptyException {
        Path templatesLocationUri = ConfigurationUtil.getTemplatesFolderPath();
        ExtractTemplatesUtil.extractTemplates(templatesLocationUri.resolve(ConfigurationConstants.COBIGEN_TEMPLATES),
            false);
        return templatesLocationUri;
    }

    /**
     * Creates a new {@link HealthCheck}.
     * @return a new {@link HealthCheck} instance
     */
    public static HealthCheck createHealthCheck() {
        return ProxyFactory.getProxy(new HealthCheckImpl());
    }

}

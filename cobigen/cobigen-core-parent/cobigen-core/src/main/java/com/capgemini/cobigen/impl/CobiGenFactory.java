package com.capgemini.cobigen.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.exception.InvalidConfigurationException;
import com.capgemini.cobigen.impl.annotation.ProxyFactory;
import com.capgemini.cobigen.impl.config.ConfigurationHolder;
import com.capgemini.cobigen.impl.config.ContextConfiguration;
import com.capgemini.cobigen.impl.config.nio.ConfigurationChangedListener;
import com.capgemini.cobigen.impl.util.FileSystemUtil;

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
     * @throws IOException
     *             if the {@link URI} points to a file or folder, which could not be read.
     * @throws InvalidConfigurationException
     *             if the context configuration could not be read properly.
     */
    public static CobiGen create(URI configFileOrFolder) throws InvalidConfigurationException, IOException {
        Objects.requireNonNull(configFileOrFolder, "The URI pointing to the configuration could not be null.");

        Path configFolder = FileSystemUtil.createFileSystemDependentPath(configFileOrFolder);

        ConfigurationHolder configurationHolder = new ConfigurationHolder(configFolder);
        if (!FileSystemUtil.isZipFile(configFileOrFolder)) {
            new ConfigurationChangedListener(configFolder, configurationHolder);
        }

        return ProxyFactory.getProxy(new CobiGenImpl(configurationHolder));
    }

}

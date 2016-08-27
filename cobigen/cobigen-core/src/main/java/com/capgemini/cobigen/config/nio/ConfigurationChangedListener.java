package com.capgemini.cobigen.config.nio;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Objects;

import com.capgemini.cobigen.CobiGen;
import com.capgemini.cobigen.config.ConfigurationHolder;
import com.capgemini.cobigen.config.constant.ConfigurationConstants;
import com.google.common.collect.Lists;

/**
 * ConfigurationHolder file tracker for file changes. Mandatory to be started to invalidate the
 * {@link ConfigurationHolder} cache correctly.
 */
public class ConfigurationChangedListener implements Runnable {

    /** {@link WatchService} to poll for configuration changes. */
    private WatchService watcher;

    /** The configuration of CobiGen */
    private ConfigurationHolder configurationHolder;

    /**
     * Creates a new {@link ConfigurationChangedListener} to watch the configuration.
     * @param configurationPath
     *            {@link Path} of the configuration.
     * @param configurationHolder
     *            The {@link ConfigurationHolder} of {@link CobiGen}.
     * @throws IOException
     *             if I/O errors occurred.
     */
    public ConfigurationChangedListener(Path configurationPath, ConfigurationHolder configurationHolder)
        throws IOException {
        Objects.requireNonNull(configurationPath, "Configuration path must not be null.");
        Objects.requireNonNull(configurationHolder, "ConfigurationHolder must not be null.");
        this.configurationHolder = configurationHolder;

        WatchService watchService = configurationPath.getFileSystem().newWatchService();
        configurationPath.register(watchService, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);
    }

    /**
     * Start a new thread watching for changes of the configuration.
     */
    void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        for (;;) {

            // wait for key to be signaled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            List<Path> changedTemplatesConfigurations = Lists.newArrayList();
            boolean contextConfigurationChanged = false;

            for (WatchEvent<?> event : key.pollEvents()) {
                // The relative file path from the watched root folder is the context of the event.
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path relativeFilePath = ev.context();

                // Collect changes
                if (relativeFilePath.getFileName().equals(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME)) {
                    changedTemplatesConfigurations.add(relativeFilePath);
                } else if (relativeFilePath.getFileName()
                    .equals(ConfigurationConstants.CONTEXT_CONFIG_FILENAME)) {
                    contextConfigurationChanged = true;
                }
            }

            if (contextConfigurationChanged) {
                configurationHolder.invalidateContextConfiguration();
            }
            for (Path path : changedTemplatesConfigurations) {
                configurationHolder.invalidateTemplatesConfiguration(path);
            }

            // Reset the key -- this step is critical if you want to receive
            // further watch events. If the key is no longer valid, the directory
            // is inaccessible so exit the loop.
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }
}

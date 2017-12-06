package com.capgemini.cobigen.impl.config.nio;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capgemini.cobigen.api.CobiGen;
import com.capgemini.cobigen.api.constants.ConfigurationConstants;
import com.capgemini.cobigen.impl.config.ConfigurationHolder;
import com.google.common.collect.Lists;

/**
 * ConfigurationHolder file tracker for file changes. Mandatory to be started to invalidate the
 * {@link ConfigurationHolder} cache correctly.
 */
public class ConfigurationChangedListener implements Runnable {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationChangedListener.class);

    /** {@link WatchService} to poll for configuration changes. */
    private WatchService watcher;

    /** The configuration of CobiGen */
    private ConfigurationHolder configurationHolder;

    /** Watch Keys tracking files */
    private final Map<WatchKey, Path> keys = new HashMap<>();

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

        watcher = FileSystems.getDefault().newWatchService();

        LOG.debug("Scanning {} ...", configurationPath);
        registerAll(configurationPath);
        LOG.debug("Done.");
    }

    /**
     * Start a new thread watching for changes of the configuration.
     */
    public void start() {
        new Thread(this).start();
    }

    /**
     * Register the given directory with the WatchService
     * @param dir
     *            directory to register
     * @throws IOException
     *             if files could not be read
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (LOG.isDebugEnabled()) {
            Path prev = keys.get(key);
            if (prev == null) {
                LOG.debug("register: {}", dir);
            } else {
                if (!dir.equals(prev)) {
                    LOG.debug("update: {} -> {}", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the WatchService.
     * @param root
     *            the root
     * @throws IOException
     *             if files could not be read
     */
    private void registerAll(final Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
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

            Path dir = keys.get(key);
            if (dir == null) {
                LOG.error(
                    "WatchKey not recognized!! This is most probably a bug and might lead to incorrect change detection within the configuration folder.");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                Kind<?> kind = event.kind();

                // Context for directory entry event is the file name of entry
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path relativeFilePath = ev.context();
                Path child = dir.resolve(relativeFilePath);

                LOG.debug("{}: {}", event.kind().name(), child);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (kind == ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        LOG.warn("Could not read directory {} to register file change listener.", child, x);
                    }
                }

                List<Path> changedTemplatesConfigurations = Lists.newArrayList();
                boolean contextConfigurationChanged = false;

                // Collect changes
                if (relativeFilePath.getFileName().toString()
                    .equals(ConfigurationConstants.TEMPLATES_CONFIG_FILENAME)) {
                    changedTemplatesConfigurations.add(child);
                } else if (relativeFilePath.getFileName().toString()
                    .equals(ConfigurationConstants.CONTEXT_CONFIG_FILENAME)) {
                    contextConfigurationChanged = true;
                }

                if (contextConfigurationChanged) {
                    configurationHolder.invalidateContextConfiguration();
                }
                for (Path path : changedTemplatesConfigurations) {
                    configurationHolder.invalidateTemplatesConfiguration(path);
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }
}

package com.devonfw.cobigen.api.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;

/**
 * Utilities related to the cobigen configurations including:
 *
 * 1. templates location
 */
public class CobiGenPaths {

    /** Logger instance */
    private static final Logger LOG = LoggerFactory.getLogger(CobiGenPaths.class);

    /**
     * Returns the CobiGen home directory, or creates a new one if it does not exist
     * @return {@link Path} of the CobiGen home directory
     */
    public static Path getCobiGenHomePath() {

        String envValue = System.getenv(ConfigurationConstants.CONFIG_ENV_HOME);
        Path cobiGenPath;
        if (!StringUtils.isEmpty(envValue)) {
            LOG.info("Custom configuration folder configured in environment variable {}={}",
                ConfigurationConstants.CONFIG_ENV_HOME, envValue);
            cobiGenPath = Paths.get(envValue);
        } else {
            cobiGenPath = ConfigurationConstants.DEFAULT_HOME;
        }

        // We first check whether we already have a directory
        if (Files.exists(cobiGenPath)) {
            return cobiGenPath;
        }

        try {
            Files.createDirectories(cobiGenPath);
        } catch (IOException e) {
            throw new CobiGenRuntimeException("Unable to create cobigen home directory at " + cobiGenPath);
        }
        return cobiGenPath;
    }

    /**
     * Returns the templates home directory (which is located inside CobiGen home folder), or creates a new
     * one if it does not exist
     * @return {@link Path} of the templates home directory
     */
    public static Path getTemplatesFolderPath() {
        return getTemplatesFolderPath(getCobiGenHomePath());
    }

    /**
     * Returns the templates home directory (which is located inside CobiGen home folder), or creates a new
     * one if it does not exist
     * @param home
     *            cobigen configuration home directory
     * @return {@link Path} of the templates home directory
     */
    public static Path getTemplatesFolderPath(Path home) {

        Path templatesPath = home.resolve(ConfigurationConstants.TEMPLATES_FOLDER);

        // We first check whether we already have a directory
        if (Files.exists(templatesPath)) {
            return templatesPath;
        }

        try {
            Files.createDirectories(templatesPath);
        } catch (IOException e) {
            throw new CobiGenRuntimeException("Unable to create path " + templatesPath);
        }
        return templatesPath;
    }

    /**
     * Returns the directory where the external processes are located, or creates a new one if it was not
     * present
     * @param processPath
     *            name of the process
     * @return {@link Path} of the external processes home directory
     */
    public static Path getExternalProcessesPath(String processPath) {

        Path home = getCobiGenHomePath();

        // We first check whether we already have a directory
        Path externalServersPath = home.resolve(processPath);
        if (Files.exists(externalServersPath)) {
            return externalServersPath;
        }

        if (new File(externalServersPath.toUri()).mkdir()) {
            return externalServersPath;
        } else {
            // Folder has not been created
            return null;
        }

    }

}

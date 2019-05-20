package com.devonfw.cobigen.api.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;

/**
 * Utilities for handling CobiGen home directory
 */
public class CobiGenPathUtil {

    /**
     * Returns the CobiGen home directory, or creates a new one if it was not present
     * @return {@link Path} of the CobiGen home directory
     */
    public static Path getCobiGenFolderPath() {

        String path = getUsersHomeDir() + File.separator + ConfigurationConstants.COBIGEN_HOME_FOLDER;
        Path cobiGenPath = Paths.get(path);

        // We first check whether we already have a directory
        if (Files.exists(cobiGenPath)) {
            return cobiGenPath;
        }

        if (new File(path).mkdir()) {
            return cobiGenPath;
        } else {
            // Folder has not been created
            return null;
        }

    }

    /**
     * Returns the directory where the external processes are located, or creates a new one if it was not
     * present
     * @return {@link Path} of the external processes home directory
     */
    public static Path getExternalProcessesPath(String processPath) {

        Path home = getCobiGenFolderPath();

        // We first check whether we already have a directory
        if (Files.exists(home.resolve(processPath))) {
            return home.resolve(processPath);
        }

        if (new File(home.resolve(processPath).toUri()).mkdir()) {
            return home.resolve(processPath);
        } else {
            // Folder has not been created
            return null;
        }

    }

    /**
     * Returns the current users home directory OS independent
     * @return string path of current users home directory
     */
    private static String getUsersHomeDir() {
        String users_home = System.getProperty("user.home");
        return users_home.replace("\\", "/"); // to support all platforms.
    }

}

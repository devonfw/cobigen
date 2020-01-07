package com.devonfw.cobigen.api.constants;

import java.nio.file.Path;

import com.devonfw.cobigen.api.util.CobiGenPathUtil;

/**
 * Constants related to the External Process plug-in like port to be used
 */
public final class ExternalProcessConstants {

    /**
     * Following SonarLint rules, we need to create a private constructor
     */
    private ExternalProcessConstants() {

        throw new IllegalStateException("Utility class, should not be instantiated");
    }

    /**
     * Host name of the connection between CobiGen and the external process plug-in, by default localhost
     */
    public static final String HOST_NAME = "localhost";

    /**
     * Port to be used for connecting with the external process
     */
    public static final int PORT = 5000;

    /**
     * Connection timeout in milliseconds, so that requests don't get frozen
     */
    public static final int CONNECTION_TIMEOUT = 10000;

    /**
     * The number of times we should retry the request
     */
    public static final int NUMBER_OF_RETRIES = 3;

    /**
     * Folder name where the different external processes are stored
     */
    public static final Path EXTERNAL_PROCESS_FOLDER = CobiGenPathUtil.getExternalProcessesPath("externalservers");

    /**
     * Name of the service the external process should implement for testing that the connection is ready
     */
    public static final String IS_CONNECTION_READY = "isConnectionReady";
}

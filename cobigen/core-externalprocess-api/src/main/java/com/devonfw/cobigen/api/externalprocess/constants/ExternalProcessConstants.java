package com.devonfw.cobigen.api.externalprocess.constants;

import java.nio.file.Path;

import com.devonfw.cobigen.api.util.CobiGenPaths;

/**
 * Constants related to the External Process plug-in like port to be used
 */
public final class ExternalProcessConstants {

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
    public static final Path EXTERNAL_PROCESS_FOLDER = CobiGenPaths.getExternalProcessesPath("externalservers");

    /**
     * Name of the service the external process should implement for testing that the connection is ready
     */
    public static final String IS_CONNECTION_READY = "isConnectionReady";
}

package com.devonfw.cobigen.todoplugin.merger;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;
import com.devonfw.cobigen.impl.externalprocess.ExternalProcessHandler;

/**
 * Tests general functionalities of the server
 */
public class PortBlockedTest {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(PortBlockedTest.class);

    /**
     * Initializing connection with server on port 80 as it is always blocked, so let's try to check what
     * happens.
     */
    private static ExternalProcessHandler request =
        ExternalProcessHandler.getExternalProcessHandler(PortBlockedTest.class, ExternalProcessConstants.HOST_NAME, 80);

    /**
     * Starts the server and initializes the connection to it
     */
    @BeforeClass
    public static void initializeServer() {
        assertEquals(true, request.startServer());
        assertEquals(true, request.initializeConnection());
    }

    /**
     * Tries to connect to a port that is always blocked (80) and then tries again with other port numbers
     */
    @Test
    public void checkPortIsBlocked() {

        // Port 80 is always blocked, so let's try to check what happens.
        try {
            request.startServer();
            assertEquals(true, request.initializeConnection());
        } finally {
            request.terminateProcessConnection();
        }
    }

}

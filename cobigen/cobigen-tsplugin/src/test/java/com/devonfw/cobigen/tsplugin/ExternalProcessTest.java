package com.devonfw.cobigen.tsplugin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;
import com.devonfw.cobigen.impl.externalprocess.ExternalProcessHandler;
import com.devonfw.cobigen.tsplugin.merger.constants.Constants;

/**
 * Tests general functionalities of the server
 */
public class ExternalProcessTest {

    /**
     * Tries to connect to a port that is always blocked (80) and then tries again with other port numbers
     */
    @Test
    public void checkPortIsBlocked() {

        // Port 80 is always blocked, so let's try to check what happens.
        ExternalProcessHandler request =
            ExternalProcessHandler.getExternalProcessHandler(ExternalProcessConstants.HOST_NAME, 80);

        try {

            request.executingExe(Constants.EXE_NAME, this.getClass());

            assertEquals(true, request.initializeConnection());
        } finally {
            request.terminateProcessConnection();
        }
    }

}

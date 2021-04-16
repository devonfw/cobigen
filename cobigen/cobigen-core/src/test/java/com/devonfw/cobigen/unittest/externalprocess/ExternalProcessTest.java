package com.devonfw.cobigen.unittest.externalprocess;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;
import com.devonfw.cobigen.impl.externalprocess.ExternalProcessHandler;

/**
 * Tests the execution of a external process
 */
public class ExternalProcessTest {

    /**
     * Checks whether we are able to start and terminate a new external process
     */
    @Test
    public void startExternalProcess() {
        ExternalProcessHandler request = ExternalProcessHandler.getExternalProcessHandler("/DummyExe",
            ExternalProcessConstants.HOST_NAME, ExternalProcessConstants.PORT);

        assertEquals(true, request.startServer());
        assertEquals(true, request.terminateProcessConnection());
    }
}

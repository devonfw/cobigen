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
     * Root path to all resources used in this test case
     */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/ExternalProcess/";

    /**
     * Checks whether we are able to start and terminate a new external process
     */
    @Test
    public void startExternalProcess() {
        ExternalProcessHandler request = ExternalProcessHandler
            .getExternalProcessHandler(ExternalProcessConstants.HOST_NAME, ExternalProcessConstants.PORT);

        assertEquals(true, request.executingExe(testFileRootPath + "\\DummyExe.exe"));
        assertEquals(true, request.terminateProcessConnection());
    }
}

package com.devonfw.cobigen.tsplugin;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;
import com.devonfw.cobigen.impl.externalprocess.ExternalProcessHandler;
import com.devonfw.cobigen.tsplugin.merger.constants.Constants;

/**
 * Tests general functionalities of the server
 */
public class ExternalProcessTest {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ExternalProcessTest.class);

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

            String s;
            Process p;
            try {
                p = Runtime.getRuntime().exec("ps -aux | less");
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((s = br.readLine()) != null) {
                    LOG.info("line: " + s);
                }
                p.waitFor();
                LOG.info(("exit: " + p.exitValue()));

                p.destroy();
            } catch (Exception e) {
            }

            assertEquals(true, request.initializeConnection());
        } finally {
            request.terminateProcessConnection();
        }
    }

}

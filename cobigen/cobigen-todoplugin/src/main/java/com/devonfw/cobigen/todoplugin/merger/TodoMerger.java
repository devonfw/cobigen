package com.devonfw.cobigen.todoplugin.merger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;
import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.to.MergeTo;
import com.devonfw.cobigen.impl.exceptions.ConnectionExceptionHandler;
import com.devonfw.cobigen.impl.externalprocess.ExternalProcessHandler;

/**
 * The {@link TodoMerger} merges a patch and the base file. There will be no merging on statement level.
 */
public class TodoMerger implements Merger {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(TodoMerger.class);

    /**
     * Instance that handles all the operations performed to the external server, like initializing the
     * connection and sending new requests
     */
    private ExternalProcessHandler request = ExternalProcessHandler.getExternalProcessHandler(this.getClass(),
        ExternalProcessConstants.HOST_NAME, ExternalProcessConstants.PORT);

    /**
     * Exception handler related to connectivity to the server
     */
    private ConnectionExceptionHandler connectionExc = new ConnectionExceptionHandler();

    /** OS specific line separator */
    private static final String LINE_SEP = System.getProperty("line.separator");

    /** Merger Type to be registered */
    private String type;

    /** The conflict resolving mode */
    private boolean patchOverrides;

    /**
     * Creates a new {@link TodoMerger}
     *
     * @param type
     *            merger type
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     */
    public TodoMerger(String type, boolean patchOverrides) {
        this.type = type;
        this.patchOverrides = patchOverrides;
        try {
            // We first check if the server is already running
            request.startConnection();
            if (request.isNotConnected()) {
                startServerConnection();
            }
        } catch (IOException e) {
            // If it is not currently running, we need to execute it
            LOG.info("Server is not currently running. Let's initialize it");
            startServerConnection();
        }
    }

    /**
     * Deploys the server and tries to initialize a new connection between CobiGen and the server
     */
    private void startServerConnection() {
        request.startServer();
        request.initializeConnection();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String merge(File base, String patch, String targetCharset) throws MergeException {
        String baseFileContents;
        if (request.isNotConnected()) {
            startServerConnection();
        }
        try {
            baseFileContents = new String(Files.readAllBytes(base.toPath()), Charset.forName(targetCharset));
        } catch (IOException e) {
            throw new MergeException(base, "Could not read base file!", e);
        }

        MergeTo mergeTo = new MergeTo(baseFileContents, patch, patchOverrides);

        HttpURLConnection conn = request.getConnection("POST", "Content-Type", "application/json", "merge");

        StringBuffer body = new StringBuffer();

        if (request.sendRequest(mergeTo, conn, targetCharset)) {

            try (InputStreamReader isr = new InputStreamReader(conn.getInputStream(), targetCharset);
                BufferedReader br = new BufferedReader(isr);) {

                LOG.info("Receiving output from Server....");
                Stream<String> s = br.lines();
                s.parallel().forEachOrdered((String line) -> {
                    body.append(line);
                    body.append(LINE_SEP);
                });

                return body.toString();
            } catch (Exception e) {

                connectionExc.handle(e);
            }
        } else {
            throw new MergeException(base, "Execution of the Todo merger raised an internal error."
                + " Check your file syntax and if error occurs again, please report it on tools-cobigen GitHub.");
        }
        // Merge was not successful
        return baseFileContents;
    }

}

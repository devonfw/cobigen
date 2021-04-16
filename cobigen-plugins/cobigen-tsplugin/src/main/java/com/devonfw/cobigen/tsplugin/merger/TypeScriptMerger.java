package com.devonfw.cobigen.tsplugin.merger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;
import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.to.InputFileTo;
import com.devonfw.cobigen.api.to.MergeTo;
import com.devonfw.cobigen.impl.exceptions.ConnectionExceptionHandler;
import com.devonfw.cobigen.impl.externalprocess.ExternalProcessHandler;
import com.devonfw.cobigen.tsplugin.merger.constants.Constants;

/**
 * The {@link TypeScriptMerger} merges a patch and the base file. There will be no merging on statement level.
 */
public class TypeScriptMerger implements Merger {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(TypeScriptMerger.class);

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

    /** Charset that will be used when sending strings to the server */
    private String charset = "UTF-8";

    /** Used for not checking multiple times whether the server is deployed or not */
    private Boolean serverIsNotDeployed = true;

    /**
     * Creates a new {@link TypeScriptMerger}
     *
     * @param type
     *            merger type
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     */
    public TypeScriptMerger(String type, boolean patchOverrides) {
        this.type = type;
        this.patchOverrides = patchOverrides;
        try {
            // We first check if the server is already running
            request.startConnection();
            if (request.isNotConnected()) {
                if (startServerConnection()) {
                    // Server is deployed
                    serverIsNotDeployed = false;
                }
            } else {
                // Server is deployed
                serverIsNotDeployed = false;
            }
        } catch (IOException e) {
            // If it is not currently running, we need to execute it
            LOG.info("Server is not currently running. Let's initialize it");
            if (startServerConnection()) {
                // Server is deployed
                serverIsNotDeployed = false;
            }
        }
    }

    /**
     * Deploys the server and tries to initialize a new connection between CobiGen and the server
     * @return true only if the server was executed and deployed successfully
     */
    private Boolean startServerConnection() {
        if (request.startServer()) {
            return request.initializeConnection();
        } else {
            return false;
        }
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String merge(File base, String patch, String targetCharset) throws MergeException {
        String baseFileContents;
        if (serverIsNotDeployed) {
            LOG.error("We have not been able to send requests to the external server. "
                + "Most probably there is an error on the executable file. "
                + "Try to manually remove folder .cobigen/externalservers found at your user root folder");
            return null;
        }
        try {
            baseFileContents = new String(Files.readAllBytes(base.toPath()), Charset.forName(targetCharset));
        } catch (IOException e) {
            throw new MergeException(base, "Could not read base file!", e);
        }

        MergeTo mergeTo = new MergeTo(baseFileContents, patch, patchOverrides);

        HttpURLConnection conn = request.getConnection("POST", "Content-Type", "application/json", "merge");

        StringBuffer importsAndExports = new StringBuffer();
        StringBuffer body = new StringBuffer();

        if (request.sendRequest(mergeTo, conn, targetCharset)) {

            try (InputStreamReader isr = new InputStreamReader(conn.getInputStream(), targetCharset);
                BufferedReader br = new BufferedReader(isr);) {

                LOG.info("Receiving output from Server....");
                Stream<String> s = br.lines();
                s.parallel().forEachOrdered((String line) -> {
                    if (line.startsWith("import ") || isExportStatement(line)) {
                        importsAndExports.append(line);
                        importsAndExports.append(LINE_SEP);
                    } else {
                        body.append(line);
                        body.append(LINE_SEP);
                    }
                });

                return runBeautifierExcludingImports(importsAndExports.toString(), body.toString(), targetCharset);
            } catch (Exception e) {

                connectionExc.handle(e);
            }
        } else {
            throw new MergeException(base, "Execution of the TypeScript merger raised an internal error."
                + " Check your file syntax and if error occurs again, please report it on cobigen GitHub.");
        }
        // Merge was not successful
        return baseFileContents;
    }

    /**
     * Reads the output.ts temporary file to get the merged contents
     * @param importsAndExports
     *            The part of the code where imports and exports are declared
     * @param body
     *            the rest of the body of the source file
     * @param targetCharset
     *            target char set of the file to be read and write
     * @return merged contents already beautified
     */
    private String runBeautifierExcludingImports(String importsAndExports, String body, String targetCharset) {

        InputFileTo fileTo = new InputFileTo("", body, charset);
        HttpURLConnection conn = request.getConnection("POST", "Content-Type", "application/json", "beautify");

        StringBuffer bodyBuffer = new StringBuffer();

        request.sendRequest(fileTo, conn, targetCharset);

        try (InputStreamReader isr = new InputStreamReader(conn.getInputStream(), targetCharset);
            BufferedReader br = new BufferedReader(isr);) {

            LOG.info("Receiving output from Server....");
            Stream<String> s = br.lines();
            s.parallel().forEachOrdered((String line) -> {
                bodyBuffer.append(line);
                bodyBuffer.append(LINE_SEP);
            });
        } catch (Exception e) {

            connectionExc.handle(e);
        }

        return importsAndExports + LINE_SEP + LINE_SEP + bodyBuffer.toString();
    }

    /**
     * Check whether this line is an export statement, taking into account that "export class" is not an
     * export statement.
     * @param line
     *            line to check whether it is an export
     * @return true if it is a real export
     */
    private boolean isExportStatement(String line) {
        if (line.startsWith("export ")) {
            Pattern pattern = Pattern.compile(Constants.EXPORT_REGEX);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find() == false) {
                return false;
            }
            String exportType = matcher.group(1).toLowerCase();

            if (Constants.NOT_EXPORT_TYPES.get(exportType) == null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}

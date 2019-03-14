package com.devonfw.cobigen.tsplugin.merger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.script.ScriptEngine;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;
import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.impl.exceptions.ConnectionExceptionHandler;
import com.devonfw.cobigen.impl.externalprocess.ExternalProcessHandler;
import com.devonfw.cobigen.tsplugin.merger.constants.Constants;
import com.devonfw.cobigen.tsplugin.merger.transferobjects.FileTO;
import com.devonfw.cobigen.tsplugin.merger.transferobjects.MergeTO;

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
    ExternalProcessHandler request = ExternalProcessHandler
        .getExternalProcessHandler(ExternalProcessConstants.HOST_NAME, ExternalProcessConstants.PORT);

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

    /** Cached script engines to not evaluate dependent scripts again and again */
    private Map<String, ScriptEngine> scriptEngines = new HashMap<>(2);

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
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String merge(File base, String patch, String targetCharset) throws MergeException {
        String baseFileContents;
        try {
            baseFileContents = new String(Files.readAllBytes(base.toPath()), Charset.forName(targetCharset));
        } catch (IOException e) {
            throw new MergeException(base, "Could not read base file!", e);
        }

        MergeTO mergeTO = new MergeTO(baseFileContents, patch, patchOverrides);

        HttpURLConnection conn = request.getConnection("POST", "Content-Type", "application/json", "merge");

        StringBuffer importsAndExports = new StringBuffer();
        StringBuffer body = new StringBuffer();
        try (OutputStream os = conn.getOutputStream(); OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");) {

            sendRequest(mergeTO, conn, os, osw);

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

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

        } catch (IllegalStateException e) {

            LOG.error("Closing connection on InputReader.", e);
            request.terminateProcessConnection();
        } catch (Exception e) {
            connectionExc.setConnectExceptionMessage("Connection to server failed, attempt number " + 0 + ".");
            connectionExc.setIOExceptionMessage("IO exception when merging");

            connectionExc.handle(e);
        }
        return runBeautifierExcludingImports(importsAndExports.toString(), body.toString());
    }

    /**
     * Sends a transfer object to the server, by using http connection
     * @param dataTO
     *            Data to transfer, it should be a serializable class
     * @param conn
     *            {@link HttpURLConnection} to the server, containing also the URL
     * @param os
     *            {@link OutputStream} that will be opened for sending data
     * @param osw
     *            {@link OutputStreamWriter} used for writing the data to be sent
     * @throws IOException
     *             when connection to the server failed
     * @throws JsonGenerationException
     *             When generating the JSON from the transfer object failed
     * @throws JsonMappingException
     *             When mapping the JSON from the transfer object failed
     */
    private void sendRequest(Object dataTO, HttpURLConnection conn, OutputStream os, OutputStreamWriter osw)
        throws IOException, JsonGenerationException, JsonMappingException {
        ObjectWriter objWriter;
        objWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonMergerTO = objWriter.writeValueAsString(dataTO);

        // We need to escape new lines because otherwise our JSON gets corrupted
        jsonMergerTO = jsonMergerTO.replace("\\n", "\\\\n");

        osw.write(jsonMergerTO);
        osw.flush();
        os.close();
        conn.connect();

        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
    }

    /**
     * Reads the output.ts temporary file to get the merged contents
     * @param importsAndExports
     *            The part of the code where imports and exports are declared
     * @param body
     *            the rest of the body of the source file
     * @return merged contents already beautified
     */
    private String runBeautifierExcludingImports(String importsAndExports, String body) {

        FileTO fileTO = new FileTO(body);
        HttpURLConnection conn = request.getConnection("POST", "Content-Type", "application/json", "beautify");

        StringBuffer bodyBuffer = new StringBuffer();
        try (OutputStream os = conn.getOutputStream(); OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");) {

            sendRequest(fileTO, conn, os, osw);

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            LOG.info("Receiving output from Server....");
            Stream<String> s = br.lines();
            s.parallel().forEachOrdered((String line) -> {
                bodyBuffer.append(line);
                bodyBuffer.append(LINE_SEP);
            });

        } catch (IllegalStateException e) {

            LOG.error("Closing connection on InputReader.", e);
            request.terminateProcessConnection();
        } catch (Exception e) {
            connectionExc.setConnectExceptionMessage("Connection to server failed, attempt number " + 0 + ".");
            connectionExc.setIOExceptionMessage("IO exception when merging");

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

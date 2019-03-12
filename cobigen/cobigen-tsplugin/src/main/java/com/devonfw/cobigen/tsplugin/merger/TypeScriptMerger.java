package com.devonfw.cobigen.tsplugin.merger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.ExternalProcessConstants;
import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.extension.Merger;
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
    ExternalProcessHandler request = ExternalProcessHandler
        .getExternalProcessHandler(ExternalProcessConstants.HOST_NAME, ExternalProcessConstants.PORT);

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
        // Used for sending serialized objects
        ObjectWriter objWriter;

        StringBuffer importsAndExports = new StringBuffer();
        StringBuffer body = new StringBuffer();
        try (OutputStream os = conn.getOutputStream(); OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");) {

            objWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String jsonMergerTO = objWriter.writeValueAsString(mergeTO);

            // We need to escape new lines because otherwise our JSON gets corrupted
            jsonMergerTO = jsonMergerTO.replace("\\n", "\\\\n");

            osw.write(jsonMergerTO);
            osw.flush();
            os.close();
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output = "";

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

        } catch (ConnectException e) {

            LOG.error("Connection to server failed, attempt number " + 0 + ".", e);
        } catch (IOException e) {

            LOG.error("IO exception when merging", e);
        } catch (IllegalStateException e) {

            LOG.error("Closing connection on InputReader.", e);
            request.terminateProcessConnection();
        }
        return runBeautifierExcludingImports(base, importsAndExports.toString(), body.toString());
    }

    /**
     * Executes the call specified by {@code executable} parameter on the script given by {@code scriptName}
     * with the javascript engine Nashorn.
     * @param base
     *            the existent base file just for error reporting
     * @param scriptName
     *            name of the script to be executed. Should exist in the root of the build path
     * @param executable
     *            {@link ScriptExecutable} running the script call itself
     * @return return value of the script casted to {@link String}
     */
    private String executeJS(File base, ScriptExecutable executable, String scriptName) {

        if (!scriptEngines.containsKey(scriptName)) {

            ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName(Constants.ENGINE_JS);

            Compilable jsCompilable = (Compilable) jsEngine;
            CompiledScript jsScript;
            try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/" + scriptName))) {
                jsScript = jsCompilable.compile(reader);
            } catch (ScriptException e) {
                throw new MergeException(base, "Could not compile " + scriptName
                    + " script on initialization. This is most properly a bug. Please report on GitHub.", e);
            } catch (IOException e) {
                throw new MergeException(base, "Could not read " + scriptName
                    + " script on initialization. This is most properly a bug. Please report on Github.", e);
            }

            ScriptContext scriptCtxt = jsEngine.getContext();
            Bindings engineScope = scriptCtxt.getBindings(ScriptContext.ENGINE_SCOPE);
            try {
                jsEngine.eval("global = {}"); // simulate global object
                jsScript.eval(engineScope);
            } catch (ScriptException e) {
                throw new MergeException(base, "Could not evaluate " + scriptName
                    + " script on initialization. This is most properly a bug. Please report on Github.", e);
            }

            scriptEngines.put(scriptName, jsEngine);
        }

        Invocable jsInvocable = (Invocable) scriptEngines.get(scriptName);
        try {
            return (String) executable.exec(jsInvocable);
        } catch (NoSuchMethodException e) {
            throw new MergeException(base,
                "Invalid API of " + scriptName + " script used. This is most properly a bug. Please report on Github.",
                e);
        } catch (ScriptException e) {
            throw new MergeException(base, "Execution of the script " + scriptName + " raised an error.", e);
        }
    }

    /**
     * Reads the output.ts temporary file to get the merged contents
     * @param base
     *            base file just for exception handling
     * @param mergedContents
     *            merged typescript code
     * @param string
     * @return merged contents already beautified
     */
    private String runBeautifierExcludingImports(File base, String importsAndExports, String body) {

        String formattedBody = "";
        // executeJS(base, invocable -> invocable.invokeMethod(((ScriptEngine) invocable).eval("global"),
        // "js_beautify", body.toString()), Constants.BEAUTIFY_JS);

        return importsAndExports + LINE_SEP + LINE_SEP + body;
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

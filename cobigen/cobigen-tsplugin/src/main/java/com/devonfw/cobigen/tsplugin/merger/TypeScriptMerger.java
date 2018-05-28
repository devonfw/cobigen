package com.devonfw.cobigen.tsplugin.merger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.tsplugin.merger.constants.Constants;

/**
 * The {@link TypeScriptMerger} merges a patch and the base file. There will be no merging on statement level.
 */
public class TypeScriptMerger implements Merger {

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

        String mergedContents =
            executeJS(base, invocable -> invocable.invokeFunction("merge", baseFileContents, patch, patchOverrides),
                Constants.TSMERGER_JS);

        return runBeautifierExcludingImports(base, mergedContents);
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

            ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("nashorn");

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
     * @return merged contents already beautified
     */
    private String runBeautifierExcludingImports(File base, String mergedContents) {
        StringBuilder imports = new StringBuilder();
        StringBuilder body = new StringBuilder();

        try (StringReader inR = new StringReader(mergedContents); BufferedReader br = new BufferedReader(inR)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("import ")) {
                    imports.append(line);
                    imports.append(LINE_SEP);
                } else {
                    body.append(line);
                }
            }
        } catch (IOException e) {
            throw new MergeException(base, "Could not process merged contents for formatting.", e);
        }

        String formattedBody =
            executeJS(base, invocable -> invocable.invokeMethod(((ScriptEngine) invocable).eval("global"),
                "js_beautify", body.toString()), Constants.BEAUTIFY_JS);

        return imports + LINE_SEP + LINE_SEP + formattedBody;
    }

}

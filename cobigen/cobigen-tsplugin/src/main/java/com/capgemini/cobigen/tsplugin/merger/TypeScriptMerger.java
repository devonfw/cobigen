package com.capgemini.cobigen.tsplugin.merger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.tsplugin.merger.constants.Constants;

/**
 * The {@link TypeScriptMerger} merges a patch and the base file. There will be no merging on statement level
 */
public class TypeScriptMerger implements Merger {

    /**
     * Merger Type to be registered
     */
    private String type;

    /**
     * The conflict resolving mode
     */
    private boolean patchOverrides;

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
        return tsMerger(patchOverrides, base, patch);

    }

    /**
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     * @param base
     *            the existent base file
     * @param patch
     *            the patch string
     * @return contents merged
     */
    private String tsMerger(boolean patchOverrides, File base, String patch) {
        Context cxBeautify = Context.enter();
        Scriptable scopeBeautify = cxBeautify.initStandardObjects();

        String mergedContents = "";
        String mergedImports = "";

        try (InputStream beautifierASStream = TypeScriptMerger.class.getResourceAsStream(Constants.BEAUTIFY_JS);
            InputStream mergerASStream = TypeScriptMerger.class.getResourceAsStream(Constants.TS_MERGER);
            Reader readerBeautifier = new InputStreamReader(beautifierASStream)) {
            cxBeautify.evaluateReader(scopeBeautify, readerBeautifier, "__beautify.js", 1, null);
            Path tmpDir = Files.createTempDirectory(Constants.COBIGEN_TS);
            Path filePath = tmpDir.resolve(Constants.TSMERGER_JS);
            Path filePatch = tmpDir.resolve(Constants.PATCH_TS);
            Files.copy(mergerASStream, filePath);
            Files.copy(IOUtils.toInputStream(patch, "UTF-8"), filePatch);

            List<String> commands = new LinkedList<>();

            if (SystemUtils.IS_OS_WINDOWS) {
                commands.add("cmd.exe");
                commands.add("/c");
            }

            commands.add("node");
            commands.add(filePath.toAbsolutePath().toString());
            if (patchOverrides) {
                commands.add("true");
            } else {
                commands.add("false");
            }

            commands.add(base.getAbsolutePath().toString());
            commands.add(filePatch.toAbsolutePath().toString());
            ProcessBuilder builder = new ProcessBuilder(commands);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            try (InputStreamReader rdr = new InputStreamReader(p.getInputStream());
                BufferedReader r = new BufferedReader(rdr)) {
                String line;
                while (true) {
                    line = r.readLine();
                    if (line == null) {
                        break;
                    }
                    if (line.startsWith("import ")) {
                        mergedImports = mergedImports.concat(line);
                        mergedImports = mergedImports.concat("\n\n");
                    } else {
                        mergedContents = mergedContents.concat("\n");
                        mergedContents = mergedContents.concat(line);
                    }
                }
            }
        } catch (IOException e) {
            throw new MergeException(new File(""), "Error reading jsBeautifier script");
        }

        scopeBeautify.put("jsCode", scopeBeautify, mergedContents);
        return mergedImports + (String) cxBeautify.evaluateString(scopeBeautify,
            "js_beautify(jsCode, {indent_size:" + 4 + "})", "inline", 1, null);

    }

}

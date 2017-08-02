package com.capgemini.cobigen.tsplugin.merger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
        return tsMerger(patchOverrides, base, patch, targetCharset);

    }

    /**
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     * @param base
     *            the existent base file
     * @param patch
     *            the patch string
     * @param targetCharset
     *            target char set of the file to be read and write
     * @return contents merged
     */
    private String tsMerger(boolean patchOverrides, File base, String patch, String targetCharset) {

        String mergedContents = "";
        String cmdError = "";

        try (InputStream mergerASStream = TypeScriptMerger.class.getResourceAsStream("/" + Constants.TSMERGER_JS)) {

            Path tmpDir = Files.createTempDirectory(Constants.COBIGEN_TS);
            Path filePath = tmpDir.resolve(Constants.TSMERGER_JS);
            Path filePatch = tmpDir.resolve(Constants.PATCH_TS);
            File outputFile = new File(tmpDir.toAbsolutePath().toString() + Constants.MERGED_CONTENTS);
            outputFile.createNewFile();
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
                commands.add("-f");
            }

            commands.add("-b");
            commands.add(base.getAbsolutePath().toString());
            commands.add("-p");
            commands.add(filePatch.toAbsolutePath().toString());
            commands.add("-o");
            commands.add(outputFile.getAbsolutePath().toString());
            commands.add("-e");
            commands.add(targetCharset);
            ProcessBuilder builder = new ProcessBuilder(commands);
            builder.redirectErrorStream(true);

            Process p = builder.start();

            try (InputStreamReader rdr = new InputStreamReader(p.getInputStream());
                BufferedReader r = new BufferedReader(rdr)) {
                String line;
                while (true) {
                    line = r.readLine();
                    if (line != null) {
                        cmdError = cmdError.concat(line);
                    } else {
                        break;
                    }
                }
                if (!cmdError.equals("")) {
                    throw new MergeException(base, cmdError);
                }
            }

            mergedContents = readMergedContentsFile(outputFile, targetCharset);

        } catch (IOException e) {
            throw new MergeException(base, "An error during merge process occurred!");
        }
        return mergedContents;

    }

    /**
     * Calls the jsBeautifier script to beautify the merged code
     *
     * @param mergedContents
     *            the merged coded
     * @return the merged code beautified
     */
    private String beautifier(String mergedContents) {
        Context cxBeautify = Context.enter();
        Scriptable scopeBeautify = cxBeautify.initStandardObjects();
        try (InputStream beautifierASStream = TypeScriptMerger.class.getResourceAsStream(Constants.BEAUTIFY_JS);
            Reader readerBeautifier = new InputStreamReader(beautifierASStream)) {
            cxBeautify.evaluateReader(scopeBeautify, readerBeautifier, "__beautify.js", 1, null);
        } catch (IOException e) {
            throw new MergeException(new File(""), "Error reading jsBeautifier script");
        }
        scopeBeautify.put("jsCode", scopeBeautify, mergedContents);
        String result = "";
        result = (String) cxBeautify.evaluateString(scopeBeautify, "js_beautify(jsCode, {indent_size:" + 4 + "})",
            "inline", 1, null);
        if (result.equals("")) {
            throw new MergeException(new File(""), "An error ocurred with beautify.js script!");
        }
        return result;
    }

    /**
     * Reads the output.ts temporary file to get the merged contents
     * @param output
     *            the output.ts file
     * @param targetCharset
     *            target char set of the file to be read and write
     * @return merged contents already beautified
     * @throws IOException
     *             if merged.ts file cannot be read
     */
    private String readMergedContentsFile(File output, String targetCharset) throws IOException {
        String imports = "";
        String mergedContents = "";

        try (FileInputStream finStrm = new FileInputStream(output);
            InputStreamReader inR = new InputStreamReader(finStrm, targetCharset);
            BufferedReader br = new BufferedReader(inR)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("import ")) {
                    imports = imports.concat(line);
                    imports = imports.concat("\n");
                } else {
                    mergedContents = mergedContents.concat(line);
                }
            }
        }
        return imports + "\n\n" + beautifier(mergedContents);
    }

}

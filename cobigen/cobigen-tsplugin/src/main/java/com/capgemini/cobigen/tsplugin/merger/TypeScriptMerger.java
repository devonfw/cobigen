package com.capgemini.cobigen.tsplugin.merger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.tsplugin.merger.constants.Constants;
import com.capgemini.cobigen.tsplugin.util.UnzipUtility;

/**
 *
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
        String result = "";
        try {
            result = tsMerger(patchOverrides, base, patch);
        } catch (IOException e) {
            throw new MergeException(base, e.getMessage());
        }

        return result;

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
     * @throws IOException
     *             if cannot find beautify.js
     */
    private String tsMerger(boolean patchOverrides, File base, String patch) throws IOException {
        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();
        String mergedContents = "";

        InputStream beautifierASStream = TypeScriptMerger.class.getResourceAsStream(Constants.BEAUTIFY_JS);
        InputStream zipFile = TypeScriptMerger.class.getResourceAsStream(Constants.TS_MERGER);

        try {
            Reader reader = new InputStreamReader(beautifierASStream);
            cx.evaluateReader(scope, reader, "__beautify.js", 1, null);
            reader.close();
        } catch (IOException e) {
            throw new MergeException(new File(""), "Error reading resoruce script");
        }

        File temp = new File("/tmp");
        temp.mkdir();
        File outPatch = new File("/tmp/temp_patch.ts");
        PrintWriter out = new PrintWriter("/tmp/temp_patch.ts");
        out.println(patch);
        out.close();

        if (Files.notExists(new File("/tmp/tsm").toPath(), LinkOption.NOFOLLOW_LINKS)) {
            UnzipUtility unzipper = new UnzipUtility();
            unzipper.unzip(zipFile, "/tmp/tsm");
        }

        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "node \\tmp\\tsm\\build\\index.js "
            + patchOverrides + " " + base.getAbsolutePath() + " " + outPatch.getAbsolutePath());

        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) {
                break;
            }
            mergedContents = mergedContents.concat(line);
        }
        scope.put("jsCode", scope, mergedContents);
        return (String) cx.evaluateString(scope, "js_beautify(jsCode, {indent_size:" + 4 + "})", "inline", 1, null);

    }

}

package com.capgemini.cobigen.tsplugin.merger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.tsplugin.merger.constants.Constants;

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
     * @param base
     * @param patch
     * @return
     * @throws IOException
     */
    private String tsMerger(boolean patchOverrides, File base, String patch) throws IOException {
        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();
        String mergedContents = "";

        InputStream beautifierASStream = TypeScriptMerger.class.getResourceAsStream(Constants.BEAUTIFY_JS);

        try {
            Reader reader1 = new InputStreamReader(beautifierASStream);
            cx.evaluateReader(scope, reader1, "__beautify.js", 1, null);
            reader1.close();
        } catch (IOException e) {
            throw new MergeException(new File(""), "Error reading resoruce script");
        }
        String index = "src/main/resources/tsm/";
        File indexFile = new File(index);
        String file = indexFile.getAbsolutePath();

        PrintWriter out = new PrintWriter("temp_patch.ts");
        out.println(patch);
        out.close();
        System.out.println("file created");
        new ProcessBuilder("cmd.exe", "/c", "more " + base.getAbsolutePath() + " > " + "temp_patch.ts").start();
        System.out.println(file);
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "node src\\main\\resources\\tsm\\build\\index.js "
            + patchOverrides + " " + base.getAbsolutePath() + " " + patch);
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

        // new ProcessBuilder("cmd.exe", "/c", "del " + file + "\\temp.ts").start();
        // new ProcessBuilder("cmd.exe", "/c", "del " + file + "\\temp_patch.ts").start();
        scope.put("jsCode", scope, mergedContents);
        return (String) cx.evaluateString(scope, "js_beautify(jsCode, {indent_size:" + 4 + "})", "inline", 1, null);

    }

}

package com.capgemini.cobigen.htmlplugin.merger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Document;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.htmlplugin.merger.ng2.Angular2Merger;

/**
 * The {@link HTMLMerger} merges a patch and the base file of the same HTML file.
 *
 */
public class HTMLMerger implements Merger {

    /**
     * Merger Type to be registered
     */
    private String type;

    /**
     * The conflict resolving mode
     */
    private boolean patchOverrides;

    /**
     * Creates a new {@link HTMLMerger}
     *
     * @param type
     *            merger type
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     */
    public HTMLMerger(String type, boolean patchOverrides) {

        this.type = type;
        this.patchOverrides = patchOverrides;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String merge(File base, String patch, String targetCharset) throws MergeException {

        Document fileDocBase;
        Document docPatch;
        String mergedContents = null;
        String htmlString;
        Parser parse = Parser.htmlParser();
        parse.settings(new ParseSettings(true, true));
        try (Reader reader = new FileReader(base)) {
            htmlString = IOUtils.toString(reader);
        } catch (IOException e) {
            throw new MergeException(base, "file could not be found, or read, or the charsetName is invalid");
        }

        fileDocBase = parse.parseInput(htmlString, base.toString());
        docPatch = parse.parseInput(patch, targetCharset);
        Angular2Merger ng2 = new Angular2Merger(fileDocBase, docPatch);
        mergedContents = ng2.merger(patchOverrides);

        return mergedContents;
    }
}

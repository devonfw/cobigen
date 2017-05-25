package com.capgemini.cobigen.htmlplugin.merger;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
        Angular2Merger ng2;
        String mergedContents = null;
        try {
            fileDocBase = Jsoup.parse(base, targetCharset);
            docPatch = Jsoup.parse(patch, targetCharset);
            ng2 = new Angular2Merger(fileDocBase, docPatch);
            mergedContents = ng2.merger(patchOverrides);
        } catch (IOException e) {
            throw new MergeException(base, "file could not be found, or read, or the charsetName is invalid");
        }

        return mergedContents;
    }
}

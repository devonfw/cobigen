package com.capgemini.cobigen.textmerger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.textmerger.util.MergeUtil;

/**
 * The {@link TextAppender} allows appending the patch to the base file
 * @author mbrunnli (03.06.2014)
 */
public class TextAppender implements Merger {

    /**
     * Type (or name) of the instance
     */
    private String type;

    /**
     * States whether the patch should be appended after adding a new line to the base file
     */
    private boolean withNewLineBeforehand;

    /**
     * Creates a new text appender which appends the patch to the base file. If {@link #withNewLineBeforehand}
     * is set, the patch will be appended by first adding a new line to the base file
     * @param type
     *            of the text appender instance
     * @param withNewLineBeforehand
     *            if <code>true</code> a new line will be inserted before each appended text if the appended
     *            text is not empty<br>
     *            <code>false</code>, otherwise.
     * @author mbrunnli (03.06.2014)
     */
    public TextAppender(String type, boolean withNewLineBeforehand) {
        this.type = type;
        this.withNewLineBeforehand = withNewLineBeforehand;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String merge(File base, String patch, String targetCharset) throws MergeException {
        String mergedString;
        try {
            mergedString = FileUtils.readFileToString(base, targetCharset);
        } catch (IOException e) {
            throw new MergeException(base, "Could not read base file.", e);
        }

        if (patch.contains("anchor:")) {
            Map<String, String> splitBase = MergeUtil.splitByAnchors(mergedString);
            Map<String, String> splitPatch = MergeUtil.splitByAnchors(mergedString);

            String header = MergeUtil.getAnchorRegexDocumentpart("header");
            if (MergeUtil.hasKeyMatchingRegularExpression(header, splitBase)) {
                MergeUtil.appendText(mergedString, header, splitBase, true);
            } else if (MergeUtil.hasKeyMatchingRegularExpression(header, splitPatch)) {
                MergeUtil.appendText(mergedString, header, splitPatch, true);
            }

            String docPart = "";
            // code to put keys and values into mergedstring
            for (String tmpAnchor : splitBase.keySet()) {
                // if it has a documentpart defined, create a regex for that, check if it exists in the patch
                // and append it according to the mergestrategy
                if (!tmpAnchor.matches(MergeUtil.getAnchorRegexDocumentpart("footer"))) {
                    tmpAnchor =
                        tmpAnchor.substring(tmpAnchor.indexOf(":") + 1, StringUtils.ordinalIndexOf(tmpAnchor, ":", 2));
                    docPart = MergeUtil.getAnchorRegexDocumentpart(tmpAnchor);
                    if (MergeUtil.hasKeyMatchingRegularExpression(docPart, splitBase)) {
                        if (MergeUtil.hasKeyMatchingRegularExpression(docPart, splitPatch)) {

                        } else {
                            mergedString = MergeUtil.appendText(mergedString, docPart, splitBase, false);
                        }
                    } else if (MergeUtil.hasKeyMatchingRegularExpression(docPart, splitPatch)) {
                        mergedString = MergeUtil.appendText(mergedString, docPart, splitPatch, false);
                    }
                }
            }

            String footer = MergeUtil.getAnchorRegexDocumentpart("footer");
            if (MergeUtil.hasKeyMatchingRegularExpression(footer, splitBase)) {
                MergeUtil.appendText(mergedString, footer, splitBase, false);
            } else if (MergeUtil.hasKeyMatchingRegularExpression(footer, splitPatch)) {
                MergeUtil.appendText(mergedString, footer, splitPatch, false);
            }
        } else {
            if (StringUtils.isNotEmpty(patch)) {
                if (type.equalsIgnoreCase("textmerge_replace")) {
                    mergedString = patch;
                    return mergedString;
                } else if (withNewLineBeforehand) {
                    mergedString += System.lineSeparator();
                }
                mergedString += patch;
            }
        }
        return mergedString;
    }

}

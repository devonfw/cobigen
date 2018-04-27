package com.capgemini.cobigen.textmerger;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

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
        return merge(mergedString, patch);
    }

    /**
     * Merges the patch into the base string
     * @param base
     *            target {@link String} to be merged into
     * @param patch
     *            {@link String} patch, which should be applied to the base file
     * @return Merged text (not null)
     */
    public String merge(String base, String patch) {
        String mergedString = "";
        if (MergeUtil.hasAnchors(patch)) {
            LinkedHashMap<String, String> splitBase = MergeUtil.splitByAnchors(base);
            LinkedHashMap<String, String> splitPatch = MergeUtil.splitByAnchors(patch);

            String footer = MergeUtil.getAnchorRegexDocumentpart("footer");
            String header = MergeUtil.getAnchorRegexDocumentpart("header");
            String toAppend = "";

            if (MergeUtil.hasKeyMatchingRegularExpression(header, splitBase)) {
                toAppend = MergeUtil.appendText(toAppend, header, splitBase, true, true);
                mergedString += toAppend;
                MergeUtil.removeKeyFromMaps(splitBase, splitPatch, header);
            } else if (MergeUtil.hasKeyMatchingRegularExpression(header, splitPatch)) {
                toAppend = MergeUtil.appendText(toAppend, header, splitPatch, true, true);
                mergedString += toAppend;
                MergeUtil.removeKeyFromMaps(splitBase, splitPatch, header);
            }

            String docPart = "";
            String mergeStrat = "";
            String tmpAnchor;
            LinkedHashSet<String> joinedKeySet = MergeUtil.joinKeySetsRetainOrder(splitBase, splitPatch);
            for (Iterator<String> iterator = joinedKeySet.iterator(); iterator.hasNext();) {
                tmpAnchor = iterator.next();
                toAppend = "";

                if (!tmpAnchor.matches(footer)) {
                    docPart = MergeUtil.getDocumentPart(tmpAnchor);
                    mergeStrat = MergeUtil.getMergeStrategy(tmpAnchor);
                    if (!MergeUtil.canBeSkipped(joinedKeySet, tmpAnchor)) {
                        if (MergeUtil.hasKeyMatchingRegularExpression(docPart, splitBase)) {
                            if (MergeUtil.hasKeyMatchingRegularExpression(docPart, splitPatch)) {

                                if (mergeStrat.matches(MergeUtil.getAnchorRegexMergestrategy("newline.+"))) {
                                    // TODO When adding new functionality, add an else if here that specifies
                                    // how text should be added when mergestrategy can start with newline and
                                    // doesn't just append.
                                    if (mergeStrat
                                        .equals(MergeUtil.getAnchorRegexMergestrategy("newlineappendbefore"))) {
                                        toAppend += MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                        toAppend += System.lineSeparator();
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                                        mergedString += toAppend;
                                        MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);
                                    } else if (mergeStrat
                                        .equals(MergeUtil.getAnchorRegexMergestrategy("newlineappendafter"))) {
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                        toAppend += System.lineSeparator();
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                                        mergedString += toAppend;
                                        MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);
                                    } else {
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                        toAppend += System.lineSeparator();
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                                        mergedString += toAppend;
                                        MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);
                                    }

                                } else if (mergeStrat.matches(MergeUtil.getAnchorRegexMergestrategy(".*newline"))) {
                                    // TODO When adding new functionality, add an else if here that specifies
                                    // how text should be added when mergestrategy can end in newline and
                                    // doesn't just append.
                                    if (mergeStrat.equals("appendbeforenewline")) {
                                        toAppend += System.lineSeparator();
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, true, true);
                                        mergedString += toAppend;
                                        MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);
                                    } else if (mergeStrat
                                        .equals(MergeUtil.getAnchorRegexMergestrategy("appendafternewline"))) {
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                                        toAppend += System.lineSeparator();
                                        mergedString += toAppend;
                                        MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);
                                    } else {
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                                        toAppend += System.lineSeparator();
                                        mergedString += toAppend;
                                        MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);
                                    }
                                    // TODO When adding new functionality, add an else if about what happens
                                    // when newline is irrelevant here
                                } else if (mergeStrat.equals(MergeUtil.getAnchorRegexMergestrategy("appendbefore"))) {
                                    toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                    toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, true, true);
                                    mergedString += toAppend;
                                    MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);
                                } else if (mergeStrat.equals(MergeUtil.getAnchorRegexMergestrategy("appendafter"))) {
                                    toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                    toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                                    mergedString += toAppend;
                                    MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);
                                } else if (mergeStrat.equals(MergeUtil.getAnchorRegexMergestrategy("replace"))) {
                                    toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                                    mergedString += toAppend;
                                    MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);

                                } else if (mergeStrat.equals(MergeUtil.getAnchorRegexMergestrategy("nomerge"))) {
                                    toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, true);
                                    mergedString += toAppend;
                                    MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);
                                } else {
                                    toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                    toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                                    mergedString += toAppend;
                                    MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);
                                }

                            } else {
                                // TODO When adding new functionality, add an else if about what happens
                                // when the anchor is only specified in the base, if it doesn't just append
                                toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, true);
                                mergedString += toAppend;
                                MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);
                            }
                            // TODO When adding new functionality, add an else if about what happens
                            // when the anchor is only specified in the patch, if it doesn't just append
                        } else if (MergeUtil.hasKeyMatchingRegularExpression(docPart, splitPatch)) {
                            if (mergeStrat.matches(MergeUtil.getAnchorRegexMergestrategy(".*newline"))) {
                                toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                                toAppend += System.lineSeparator();
                                mergedString += toAppend;
                                MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);
                            } else if (mergeStrat.matches(MergeUtil.getAnchorRegexMergestrategy("newline.+"))) {
                                toAppend += System.lineSeparator();
                                toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                                mergedString += toAppend;
                                MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);
                            }
                            toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                            mergedString += toAppend;
                            MergeUtil.removeKeyFromMaps(splitBase, splitPatch, docPart);

                        }
                    }
                } else {
                    if (MergeUtil.hasKeyMatchingRegularExpression(footer, splitBase)) {
                        toAppend = MergeUtil.appendText(toAppend, footer, splitBase, false, true);
                        mergedString += toAppend;
                    } else if (MergeUtil.hasKeyMatchingRegularExpression(footer, splitPatch)) {
                        toAppend = MergeUtil.appendText(toAppend, footer, splitPatch, false, true);
                        mergedString += toAppend;
                    }
                    break;
                }
            }
        } else {
            mergedString = base;
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
        return mergedString.trim();
    }
}

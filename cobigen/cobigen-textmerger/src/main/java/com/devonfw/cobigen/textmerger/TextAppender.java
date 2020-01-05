package com.devonfw.cobigen.textmerger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.devonfw.cobigen.api.exception.MergeException;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.textmerger.anchorextension.Anchor;
import com.devonfw.cobigen.textmerger.anchorextension.MergeStrategy;
import com.devonfw.cobigen.textmerger.anchorextension.MergeUtil;

/**
 * The {@link TextAppender} allows appending the patch to the base file
 * @author mbrunnli (03.06.2014)
 */
public class TextAppender implements Merger {

    /**
     * Type (or name) of the instance
     */
    private String type;

    private MergeStrategy defaultStrat;

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
        switch (type) {
        case "textmerge_append":
            defaultStrat = MergeStrategy.APPEND;
            break;
        case "textmerge_appendWithNewLine":
            defaultStrat = MergeStrategy.APPEND;
            break;
        case "textmerge_override":
            defaultStrat = MergeStrategy.OVERRIDE;
            break;
        }
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
        try {
            mergedString = merge(mergedString, patch);
        } catch (Exception e) {
            throw new MergeException(base, e.getMessage(), e);
        }
        return mergedString;
    }

    /**
     * Merges the patch into the base string
     * @param base
     *            target {@link String} to be merged into
     * @param patch
     *            {@link String} patch, which should be applied to the base file
     * @return Merged text (not null)
     * @throws Exception
     *             When there is some problem about anchors
     */
    public String merge(String base, String patch) throws Exception {
        String mergedString = "";
        if (MergeUtil.hasAnchors(patch)) {
            LinkedHashMap<Anchor, String> splitBase = MergeUtil.splitByAnchors(base, defaultStrat);
            LinkedHashMap<Anchor, String> splitPatch = MergeUtil.splitByAnchors(patch, defaultStrat);

            String footer = "footer";
            String header = "header";
            String toAppend = "";

            if (MergeUtil.hasKeyMatchingDocumentPart(header, splitBase)) {
                toAppend = MergeUtil.appendText(toAppend, header, splitBase, true, true);
                mergedString = MergeUtil.addTextAndDeleteCurrentAnchor(mergedString, toAppend, splitPatch, splitBase,
                    MergeUtil.getKeyMatchingDocumentPart(header, splitBase));
            } else if (MergeUtil.hasKeyMatchingDocumentPart(header, splitPatch)) {
                toAppend = MergeUtil.appendText(toAppend, header, splitPatch, true, true);
                mergedString = MergeUtil.addTextAndDeleteCurrentAnchor(mergedString, toAppend, splitPatch, splitBase,
                    MergeUtil.getKeyMatchingDocumentPart(header, splitPatch));
            }

            String docPart = "";
            MergeStrategy mergeStrat;
            Anchor tmpAnchor;
            ArrayList<Anchor> joinedKeySet = MergeUtil.joinKeySetsRetainOrder(splitBase, splitPatch);
            for (Iterator<Anchor> iterator = joinedKeySet.iterator(); iterator.hasNext();) {
                tmpAnchor = iterator.next();
                toAppend = "";
                if (!tmpAnchor.getDocPart().matches(footer)) {
                    docPart = tmpAnchor.getDocPart();
                    mergeStrat = tmpAnchor.getMergeStrat();
                    if (!MergeUtil.canBeSkipped(joinedKeySet, tmpAnchor)) {
                        if (MergeUtil.hasKeyMatchingDocumentPart(docPart, splitBase)) {
                            if (MergeUtil.hasKeyMatchingDocumentPart(docPart, splitPatch)) {
                                if (tmpAnchor.getNewlineName().matches("(newline_).+")) {
                                    switch (tmpAnchor.getNewlineName().toLowerCase()) {
                                    case "newline_appendbefore":
                                        toAppend += MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, true, true);
                                        mergedString = MergeUtil.addTextAndDeleteCurrentAnchor(mergedString, toAppend,
                                            splitPatch, splitBase, tmpAnchor);
                                        break;
                                    case "newline_appendafter":
                                    case "newline_append":
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                        toAppend += System.lineSeparator();
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                                        mergedString = MergeUtil.addTextAndDeleteCurrentAnchor(mergedString, toAppend,
                                            splitPatch, splitBase, tmpAnchor);
                                        break;
                                    default:
                                        throw new Exception("Error at anchor: " + tmpAnchor.getAnchor()
                                            + " Invalid merge strategy, newline is not compatible here.");
                                    }
                                } else if (tmpAnchor.getNewlineName().matches(".*(newline)")) {
                                    switch (tmpAnchor.getNewlineName().toLowerCase()) {
                                    case "appendbefore_newline":
                                        toAppend += System.lineSeparator();
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, true, true);
                                        mergedString = MergeUtil.addTextAndDeleteCurrentAnchor(mergedString, toAppend,
                                            splitPatch, splitBase, tmpAnchor);
                                        break;
                                    case "newline":
                                    case "appendafter_newline":
                                    case "append_newline":
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                                        toAppend += System.lineSeparator();
                                        mergedString = MergeUtil.addTextAndDeleteCurrentAnchor(mergedString, toAppend,
                                            splitPatch, splitBase, tmpAnchor);
                                        break;
                                    default:
                                        throw new Exception("Error at anchor: " + tmpAnchor.getAnchor()
                                            + " Invalid merge strategy, newline is not compatible here.");
                                    }
                                } else {
                                    switch (mergeStrat) {
                                    case APPENDBEFORE:
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, true, true);
                                        mergedString = MergeUtil.addTextAndDeleteCurrentAnchor(mergedString, toAppend,
                                            splitPatch, splitBase, tmpAnchor);
                                        break;
                                    case APPENDAFTER:
                                    case APPEND:
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, false);
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                                        mergedString = MergeUtil.addTextAndDeleteCurrentAnchor(mergedString, toAppend,
                                            splitPatch, splitBase, tmpAnchor);
                                        break;
                                    case OVERRIDE:
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                                        mergedString = MergeUtil.addTextAndDeleteCurrentAnchor(mergedString, toAppend,
                                            splitPatch, splitBase, tmpAnchor);
                                        break;
                                    case NOMERGE:
                                        toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, true);
                                        mergedString = MergeUtil.addTextAndDeleteCurrentAnchor(mergedString, toAppend,
                                            splitPatch, splitBase, tmpAnchor);
                                        break;
                                    case ERROR:
                                        throw new Exception("Error at anchor: " + tmpAnchor.getAnchor()
                                            + " Invalid merge strategy, merge strategy does not exist.");
                                    default:
                                        throw new Exception("Implementation error, please create a new issue at "
                                            + "https://github.com/devonfw/cobigen/issues "
                                            + "and provide your document and the faulty anchor "
                                            + tmpAnchor.getAnchor());
                                    }
                                }

                            } else {
                                toAppend = MergeUtil.appendText(toAppend, docPart, splitBase, false, true);
                                mergedString = MergeUtil.addTextAndDeleteCurrentAnchor(mergedString, toAppend,
                                    splitPatch, splitBase, tmpAnchor);
                            }
                        } else if (MergeUtil.hasKeyMatchingDocumentPart(docPart, splitPatch)) {
                            toAppend = MergeUtil.appendText(toAppend, docPart, splitPatch, false, true);
                            mergedString = MergeUtil.addTextAndDeleteCurrentAnchor(mergedString, toAppend, splitPatch,
                                splitBase, tmpAnchor);
                        }
                    }
                } else {
                    if (MergeUtil.hasKeyMatchingDocumentPart(footer, splitBase)) {
                        toAppend = MergeUtil.appendText(toAppend, footer, splitBase, false, true);
                        mergedString += toAppend;
                    } else if (MergeUtil.hasKeyMatchingDocumentPart(footer, splitPatch)) {
                        toAppend = MergeUtil.appendText(toAppend, footer, splitPatch, false, true);
                        mergedString += toAppend;
                    }
                    break;
                }
            }
        } else {
            mergedString = base;
            if (StringUtils.isNotEmpty(patch)) {
                if (type.equalsIgnoreCase("textmerge_override")) {
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

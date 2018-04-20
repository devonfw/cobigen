package com.capgemini.cobigen.textmerger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.api.extension.Merger;

/**
 * The {@link TextAppender} allows anchoring the patch to the base file
 * @author mbrunnli (03.06.2014)
 */
public class TextAppender implements Merger {

    /**
     * The Start of the anchors, as in anchor:${documentPart}:${mergeStrategy}:anchorend
     */
    private static final String anchorStart = "anchor:";

    /**
     * The End of the anchors, as in anchor:${documentPart}:${mergeStrategy}:anchorend
     */
    private static final String anchorEnd = ":anchorend";

    /**
     * Type (or name) of the instance
     */
    private String type;

    /**
     * States whether the patch should be anchored after adding a new line to the base file
     */
    private boolean withNewLineBeforehand;

    /**
     * Creates a new text appender which anchors the patch to the base file. If {@link #withNewLineBeforehand}
     * is set, the patch will be anchored by first adding a new line to the base file
     * @param type
     *            of the text appender instance
     * @param withNewLineBeforehand
     *            if <code>true</code> a new line will be inserted before each anchored text iff the anchored
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
        String key;
        try {
            mergedString = FileUtils.readFileToString(base, targetCharset);
        } catch (IOException e) {
            throw new MergeException(base, "Could not read base file.", e);
        }

        if (patch.contains("anchor:")) {
            Map<String, String> splitBase = MergeUtil.splitByAnchors(mergedString);
            Map<String, String> splitPatch = MergeUtil.splitByAnchors(mergedString);

            // code to put header into the front
            String header = MergeUtil.getRegexAnchorOfDocumentpart("header");
            if (MergeUtil.hasKeyMatchingRegularExpression(header, splitBase)) {
                key = MergeUtil.getKeyMatchingRegularExpression(header, splitBase);
                mergedString = key + splitBase.get(key) + mergedString;
            } else if (MergeUtil.hasKeyMatchingRegularExpression(header, splitPatch)) {
                key = MergeUtil.getKeyMatchingRegularExpression(header, splitPatch);
                mergedString = key + splitPatch.get(key) + mergedString;
            }

            // code to put keys and values into mergedstring

            String footer = MergeUtil.getRegexAnchorOfDocumentpart("footer");
            if (MergeUtil.hasKeyMatchingRegularExpression(footer, splitBase)) {
                key = MergeUtil.getKeyMatchingRegularExpression(footer, splitBase);
                mergedString = key + splitBase.get(key) + mergedString;
            } else if (MergeUtil.hasKeyMatchingRegularExpression(footer, splitPatch)) {
                key = MergeUtil.getKeyMatchingRegularExpression(footer, splitPatch);
                mergedString = key + splitPatch.get(key) + mergedString;
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

    /**
     * Inner class that implements a few very specific methods to help with working on maps and regular
     * expression and reduce code redundancy
     */
    public static class MergeUtil {

        /**
         * Helper method that takes a String and fills a map, with the keys being the anchor definitions and
         * the values being the text that come afterwards
         * @param toSplit
         *            The string that is to be split by anchors
         * @return a Hashmap<String, String> which contains anchors as keys and the following text as values
         */
        public static HashMap<String, String> splitByAnchors(String toSplit) {
            HashMap<String, String> result = new HashMap<>();
            toSplit.trim();
            toSplit += "anchor:";
            String anchor;
            int anchorstartCount = StringUtils.countMatches(toSplit, anchorStart);
            int anchorEndCount = StringUtils.countMatches(toSplit, anchorEnd);
            int anchorCount;

            if (anchorstartCount > anchorEndCount) {
                anchorCount = anchorEndCount;
            } else {
                anchorCount = anchorstartCount;
            }

            if (anchorCount > 0) {
                for (int i = 1; i <= anchorCount; i++) {
                    anchor = StringUtils.substring(toSplit, StringUtils.ordinalIndexOf(toSplit, anchorStart, i),
                        StringUtils.ordinalIndexOf(toSplit, anchorEnd, i) + anchorEnd.length());
                    String value = StringUtils.substring(toSplit,
                        StringUtils.ordinalIndexOf(toSplit, anchorEnd, i) + anchorEnd.length(),
                        StringUtils.ordinalIndexOf(toSplit, anchorStart, i + 1));
                    if (anchor.matches("/.*anchor:.*:.*:anchorend/i")) {

                        result.put(anchor, value);
                    }
                    result.put(anchor, value);
                }
            }
            return result;
        }

        /**
         * Helper method to check if a given map<String,String> contains a key that fits the given regular
         * expression.
         * @param regex
         *            The regular expression that is to be matched.
         * @param m
         *            The map which the check is to be performed on.
         * @return true if a key matches the regular expression, false if there is none.
         */
        public static boolean hasKeyMatchingRegularExpression(String regex, Map<String, String> m) {
            for (String s : m.keySet()) {
                if (s.matches(regex)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Helper method to get a key matching the given regular expression out of a given map<String,String>.
         * @param regex
         *            The regular expression that the key should match.
         * @param m
         *            The map that is to be checked.
         * @return null if the key does not exist, otherwise the full entry of the key matching the regex.
         */
        public static String getKeyMatchingRegularExpression(String regex, Map<String, String> m) {
            if (hasKeyMatchingRegularExpression(regex, m)) {
                for (String s : m.keySet()) {
                    if (s.matches(regex)) {
                        return s;
                    }
                }
            }
            return null;
        }

        /**
         * @param docPart
         *            A string defining the ${documentpart} of the anchor
         * @return a regular expression that matches an anchor of the specified documentpart
         */
        public static String getRegexAnchorOfDocumentpart(String docPart) {
            return "/.*anchor:" + docPart + ":.*:anchorend/i";
        }

    }
}

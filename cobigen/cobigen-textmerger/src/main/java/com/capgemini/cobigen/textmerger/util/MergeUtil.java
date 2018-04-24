package com.capgemini.cobigen.textmerger.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Inner class that implements a few very specific methods to help with working on maps and regular expression
 * and reduce code redundancy
 */
public class MergeUtil {

    /**
     * Regular expression displaying what anchors should look like
     */
    private static final String anchorRegex = "\n.*anchor:.*:.*:anchorend\n";

    /**
     * Helper method that takes a String and fills a LinkedHashMap, with the keys being the anchor definitions
     * and the values being the text that come afterwards. This method exists to trim the code of
     * merge(File,String,String)
     * @param toSplit
     *            The string that is to be split by anchors
     * @return a LinkedHashMap<String, String> which contains anchors as keys and the following text as values
     */
    public static LinkedHashMap<String, String> splitByAnchors(String toSplit) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        toSplit.trim();
        toSplit += "\n anchor:::anchorend\n";
        toSplit = "\n" + toSplit;
        String anchor;
        int anchorCount = 0;
        Pattern regex = Pattern.compile(anchorRegex);
        Matcher m = regex.matcher(toSplit);
        Matcher tmp = regex.matcher(toSplit);

        while (tmp.find()) {
            anchorCount++;
        }
        if (anchorCount > 0) {
            for (int i = 1; i < anchorCount; i++) {
                if (m.find()) {
                    tmp = regex.matcher(toSplit);
                    anchor = StringUtils.substring(toSplit, m.start() + 1, m.end());
                    for (int j = 0; j <= i; j++) {
                        tmp.find();
                    }
                    String value = StringUtils.substring(toSplit, m.end(), tmp.start());
                    if (anchor.matches(".*anchor:.*:.*:anchorend.*\n")) {
                        result.put(anchor.trim(), value.trim());
                    }
                }
            }
        }
        return result;
    }

    /**
     * Helper method to check if a given map<String,String> contains a key that fits the given regular
     * expression. This method exists to avoid code redundancy.
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
     * This method exists to avoid code redundancy.
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
     * Helper Method to create a regular expression of an anchor that contains a documentpart. The result is
     * used to find an anchor of a specific documentpart. This method exists to ensure consistency in creation
     * of regular expressions.
     * @param docPart
     *            A string defining the ${documentpart} of the anchor
     * @return a regular expression that matches an anchor of the specified documentpart
     */
    public static String getAnchorRegexDocumentpart(String docPart) {
        return ".*anchor:" + docPart + ":.*:anchorend";
    }

    /**
     * Helper Method to create a regular expression of an anchor that contains a mergestrategy. The result is
     * used to check an anchor of a documentpart for its mergestrategy. This method exists to ensure
     * consistency in creation of regular expressions.
     * @param mergestrategy
     *            A string defining the ${documentpart} of the anchor
     * @return a regular expression that matches an anchor of the specified documentpart
     */
    public static String getAnchorRegexMergestrategy(String mergestrategy) {
        return ".*anchor:.*" + mergestrategy + ":anchorend";
    }

    /**
     * Method that searches for a key matching the give regular expression and appends it and its value before
     * or after the given text. This method exists to avoid code redundancy.
     * @param text
     *            The original text that something is to be appended to.
     * @param regex
     *            The regular expression to be searched for in the map.
     * @param m
     *            The map that contains the data that should be appended to the text.
     * @param before
     *            Specifies whether the new text should be appended before or after the existing text.
     * @return The original text with new text appended to it. An empty string if there is no key in the map
     *         matching the regular expression.
     */
    public static String appendText(String text, String regex, Map<String, String> m, boolean before) {
        String key = MergeUtil.getKeyMatchingRegularExpression(regex, m);
        if (key != null) {
            if (before) {
                return key + "\n" + m.get(key) + "\n" + text + "\n";
            } else {
                return text + "\n" + key + "\n" + m.get(key) + "\n";
            }
        } else {
            return "";
        }
    }
}
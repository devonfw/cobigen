package com.capgemini.cobigen.textmerger.util;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper class that implements a few very specific methods to help with working on maps and regular
 * expression and reduce code redundancy.
 */
public class MergeUtil {

    /**
     * Regular expression displaying what anchors should look like
     */
    private static final String anchorRegex = System.lineSeparator() + ".*anchor:.*:anchorend" + System.lineSeparator();

    private static int lineSepLength = System.lineSeparator().length() - 1;

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
        if (!StringUtils.substring(toSplit, 0, StringUtils.ordinalIndexOf(toSplit, "\n", 1) - lineSepLength)
            .matches(".*anchor:.*:.*:anchorend")) {
            toSplit = "// anchor:default:replace:anchorend" + System.lineSeparator() + toSplit;
        }

        toSplit += System.lineSeparator() + "anchor:::anchorend" + System.lineSeparator();
        toSplit = System.lineSeparator() + toSplit;
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
                    // System.out.println(m.group());
                    tmp = regex.matcher(toSplit);
                    anchor = StringUtils.substring(toSplit, m.start() + 1, m.end());
                    anchor = anchor.trim();
                    for (int j = 0; j <= i; j++) {
                        tmp.find();
                    }
                    String value = StringUtils.substring(toSplit, m.end(), tmp.start());
                    if (anchor.matches(".*anchor:.*:.*:anchorend.*")) {
                        result.put(anchor.trim(), value.trim());
                    } else if (anchor.matches(".*anchor:.*:anchorend")) {
                        String[] addColon = anchor.split(":");
                        String s = addColon[0] + ":" + ":" + addColon[1] + ":" + addColon[2];
                        result.put(s.trim(), value.trim());
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
        return ".*anchor:.*:" + mergestrategy + ":anchorend";
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
     * @param withKey
     *            Specifies whether the key of the map should be appended too, or just the value.
     * @return The original text with new text appended to it. An empty string if there is no key in the map
     *         matching the regular expression.
     */
    public static String appendText(String text, String regex, Map<String, String> m, boolean before, boolean withKey) {
        String key = MergeUtil.getKeyMatchingRegularExpression(regex, m);
        if (key != null) {
            if (before) {
                if (withKey) {
                    return System.lineSeparator() + key + System.lineSeparator() + m.get(key) + text;
                } else {
                    return System.lineSeparator() + m.get(key) + text;
                }
            } else {
                if (withKey) {
                    return System.lineSeparator() + key + text + System.lineSeparator() + m.get(key);
                } else {
                    return text + System.lineSeparator() + m.get(key);
                }
            }
        } else {
            return "";
        }
    }

    /**
     * Helper method to check if a given text contains anchors fitting the declaration specification
     * @param patch
     *            The text to be checked
     * @return true if the text contains anchors, false if not
     */
    public static boolean hasAnchors(String patch) {
        Pattern regex = Pattern.compile(".*anchor:.*:.*:anchorend.*");
        Matcher m = regex.matcher(patch);
        return m.find();
    }

    /**
     * Helper method to remove a key-value pairing from base and patch map to avoid typing 2 lines on every
     * key deleted
     * @param base
     *            the map containing the anchor-text pairings of the base text
     * @param patch
     *            the map containing the anchor-text pairings of the patch
     * @param regex
     *            the regular expression used to search for the key
     */
    public static void removeKeyFromMaps(Map<String, String> base, Map<String, String> patch, String regex) {
        base.remove(MergeUtil.getKeyMatchingRegularExpression(regex, base));
        patch.remove(MergeUtil.getKeyMatchingRegularExpression(regex, patch));
    }

    /**
     * Helper method that merges two keysets of maps while retaining the order of their entries. For example,
     * {1,2,3} and {1,4,3} get merged into {1,2,4,3}; {1,2,4} and {1,4,5} get merged into {1,2,4,5}
     * @param one
     *            the first map
     * @param two
     *            the second map
     * @return a set of the keysets merged into one
     */
    public static LinkedHashSet<String> joinKeySetsRetainOrder(Map<String, String> one, Map<String, String> two) {
        LinkedHashSet<String> joinedSet = new LinkedHashSet<>();
        Set<String> checkSame = new HashSet<>();
        checkSame.addAll(one.keySet());
        if (checkSame.retainAll(two.keySet())) {
            int i = 0;
            int x = 0;

            for (String s : one.keySet()) {
                i++;
                if (two.containsKey(s)) {
                    for (String t : two.keySet()) {
                        x++;
                        if (s.equals(t) && x == i) {
                            joinedSet.add(s);
                            break;
                        } else if (!joinedSet.contains(t)) {
                            joinedSet.add(t);
                            if (one.containsKey(t)) {
                                break;
                            }
                        }
                    }
                }
                joinedSet.add(s);
                x = 0;
            }
            for (String t : two.keySet()) {
                if (!joinedSet.contains(t)) {
                    joinedSet.add(t);
                }
            }
            return joinedSet;
        } else {
            for (String s : one.keySet()) {
                joinedSet.add(s);
            }
            return joinedSet;
        }
    }

    /**
     * Helper method that checks if the current anchor should be skipped or not. Needed to make sure the
     * latest mergestrategy of a documentpart is in the resulting document
     * @param toCheck
     *            Set of anchors to count documentparts
     * @param anchor
     *            The anchor that will be checked
     * @return true if it's the only anchor of its documentpart, true if it's the second anchor(thus from the
     *         patch) of its documentpart, false if it's the first of multiple or the third an later of its
     *         documentpart
     */
    public static boolean canBeSkipped(LinkedHashSet<String> toCheck, String anchor) {
        int docPartCount = 0;
        String docPart = MergeUtil.getDocumentPart(anchor);
        String[] anchorsWithDocPart = new String[toCheck.size()];
        for (String s : toCheck) {
            if (docPart.equals(MergeUtil.getDocumentPart(s))) {
                anchorsWithDocPart[docPartCount] = s;
                docPartCount++;
            }
        }
        if (docPartCount < 2) {
            return false;
        }
        if (anchorsWithDocPart[1].equals(anchor)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Helper method that gives the regex that matches the mergestrategy of a specific anchor
     * @param s
     *            The anchor to get the mergestrategy of
     * @return A regular expression of a specific anchor specifying the mergestrategy
     */
    public static String getMergeStrategy(String s) {
        return getAnchorRegexMergestrategy(
            s.substring(StringUtils.ordinalIndexOf(s, ":", 2) + 1, StringUtils.ordinalIndexOf(s, ":", 3)));
    }

    /**
     * Helper method that gives the regex that matches the documentpart of a specific anchor
     * @param s
     *            The anchor to get the documentpart of
     * @return A regular expression of a specific anchor specifying the documentpart
     */
    public static String getDocumentPart(String s) {
        return MergeUtil
            .getAnchorRegexDocumentpart(s.substring(s.indexOf(":") + 1, StringUtils.ordinalIndexOf(s, ":", 2)));
    }
}
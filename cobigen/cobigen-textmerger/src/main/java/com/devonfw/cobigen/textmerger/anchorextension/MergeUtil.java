package com.devonfw.cobigen.textmerger.anchorextension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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
     * Regular expression matching what correct anchors should look like.
     */
    public static final String correctAnchorRegex =
        "(.*)anchor:([^:]+):(newline_)?([^:]+)(_newline)?:anchorend\\s*(\\r\\n|\\r|\\n)";

    /**
     * Regular expression matching what correct anchors should look like after invoking trim() on them.
     */
    public static final String correctAnchorRegexTrimmed = "(.*)anchor:([^:]+):(newline_)?([^:]+)(_newline)?:anchorend";

    /**
     * Regular expression matching all anchors, correct and wrong, to properly throw exceptions to show users
     * what is wrong.
     */
    private static final String anchorRegex = "(.*)anchor:([^:]*)(:([^:]*))?:anchorend\\s*(\\r\\n|\\r|\\n)";

    /**
     * Regular expression matching all anchors, correct and wrong, after invoking trim() on them.
     */
    private static final String anchorRegexTrimmed = "(.*)anchor:([^:]*)(:([^:]*))?:anchorend";

    /**
     * Length of the systems line separator.
     */
    private static final int lineSepLength = System.lineSeparator().length() - 1;

    /**
     * Helper method that takes a String and fills a LinkedHashMap, with the keys being the anchor definitions
     * and the values being the text that come afterwards. This method exists to trim the code of
     * merge(File,String,String)
     * @param toSplit
     *            The string that is to be split by anchors
     * @return a LinkedHashMap which contains anchors as keys and the following text as values
     * @throws Exception
     *             when an anchor contains a wrong definition
     */
    public static LinkedHashMap<Anchor, String> splitByAnchors(String toSplit, MergeStrategy defaultMergeStrategy)
        throws Exception {
        LinkedHashMap<Anchor, String> result = new LinkedHashMap<>();
        toSplit.trim();
        if (!StringUtils.substring(toSplit, 0, StringUtils.ordinalIndexOf(toSplit, "\n", 1) - lineSepLength).trim()
            .matches(anchorRegexTrimmed)) {
            throw new Exception(
                "Incorrect document structure. Anchors are defined but there is no anchor at the start of the document.\n"
                    + "See https://github.com/devonfw/cobigen/wiki/cobigen-textmerger#general and "
                    + "https://github.com/devonfw/cobigen/wiki/cobigen-textmerger#error-list "
                    + "for more details");
        }

        toSplit = toSplit + System.lineSeparator() + "anchor:::anchorend" + System.lineSeparator();
        Anchor anchor;
        int anchorCount = 0;
        Pattern regex = Pattern.compile(anchorRegex);
        Matcher m = regex.matcher(toSplit);
        Matcher tmp = regex.matcher(toSplit);
        while (tmp.find()) {
            anchorCount++;
        }
        tmp = regex.matcher(toSplit);
        tmp.find();
        for (int i = 1; i < anchorCount; i++) {
            if (m.find()) {
                tmp.find();
                if (m.group(4) == null || m.group(4).equals("")) {
                    anchor = new Anchor(m.group(1), m.group(2), defaultMergeStrategy, false, false);
                } else {
                    MergeStrategy strat = getMergeStrategyOfName(m.group(4));
                    if (m.group(4).contains("newline_")) {
                        anchor = new Anchor(m.group(1), m.group(2), strat, true, true);
                    } else if (m.group(4).contains("_newline")) {
                        anchor = new Anchor(m.group(1), m.group(2), strat, true, false);
                    } else {
                        anchor = new Anchor(m.group(1), m.group(2), strat, false, false);
                    }
                }
                String value = StringUtils.substring(toSplit, m.end(), tmp.start());
                if (anchor.getMergeStrat().equals(MergeStrategy.ERROR)) {
                    throw new Exception("Error at anchor for documentpart: " + anchor.getAnchor()
                        + ". Incorrect anchor definition, no proper mergestrategy defined.\nSee "
                        + "https://github.com/devonfw/cobigen/wiki/cobigen-textmerger#mergestrategies "
                        + "for additional info");
                } else if (anchor.getAnchor().matches(correctAnchorRegexTrimmed)) {
                    result.put(anchor, value.trim());
                }
            }
        }
        return result;
    }

    /**
     * Helper method to check if a given map contains a key that fits the given regular expression. This
     * method exists to avoid code redundancy.
     * @param docPart
     *            The documentpart to search for
     * @param m
     *            The map which the check is to be performed on.
     * @return true if a key matches the regular expression, false if there is none.
     */
    public static boolean hasKeyMatchingDocumentPart(String docPart, Map<Anchor, String> m) {
        for (Anchor a : m.keySet()) {
            if (a.getDocPart().equals(docPart)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to get a key matching the given regular expression out of a given map. This method exists
     * to avoid code redundancy.
     * @param docPart
     *            The documentpart that the key should match.
     * @param m
     *            The map that is to be checked.
     * @return null if the key does not exist, otherwise the full entry of the key matching the regex.
     */
    public static Anchor getKeyMatchingDocumentPart(String docPart, Map<Anchor, String> m) {
        if (hasKeyMatchingDocumentPart(docPart, m)) {
            for (Anchor a : m.keySet()) {
                if (a.getDocPart().matches(docPart)) {
                    return a;
                }
            }
        }
        return null;
    }

    /**
     * Method that searches for a key matching the give regular expression and appends it and its value before
     * or after the given text. This method exists to avoid code redundancy. A single exception is made for
     * anchors containing the mergestrategy newline_appendbefore because it doesn't work when doing it the
     * same way other strategies do.
     * @param text
     *            The original text that something is to be appended to.
     * @param docPart
     *            The documentpart to be searched for in the map.
     * @param m
     *            The map that contains the data that should be appended to the text.
     * @param before
     *            Specifies whether the new text should be appended before or after the existing text.
     * @param withKey
     *            Specifies whether the key of the map should be appended too, or just the value.
     * @return The original text with new text appended to it. An empty string if there is no key in the map
     *         matching the regular expression.
     */
    public static String appendText(String text, String docPart, Map<Anchor, String> m, boolean before,
        boolean withKey) {
        Anchor key = MergeUtil.getKeyMatchingDocumentPart(docPart, m);
        if (key.getNewlineName().equalsIgnoreCase("newline_appendbefore") && withKey) {
            return System.lineSeparator() + key.getAnchor() + System.lineSeparator() + System.lineSeparator()
                + m.get(key) + text;
        } else if (before) {
            if (withKey) {
                return System.lineSeparator() + key.getAnchor() + System.lineSeparator() + m.get(key) + text;
            } else {
                return System.lineSeparator() + m.get(key) + text;
            }
        } else {
            if (withKey) {
                return System.lineSeparator() + key.getAnchor() + text + System.lineSeparator() + m.get(key);
            } else {
                return text + System.lineSeparator() + m.get(key);
            }
        }
    }

    /**
     * Helper method to check if a given text contains anchors fitting the declaration specification
     * @param patch
     *            The text to be checked
     * @return true if the text contains anchors, false if not
     */
    public static boolean hasAnchors(String patch) {
        Pattern regex = Pattern.compile(anchorRegexTrimmed);
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
     * @param anchor
     *            The anchor to be removed
     */
    public static void removeKeyFromMaps(Map<Anchor, String> base, Map<Anchor, String> patch, Anchor anchor) {
        base.remove(anchor);
        patch.remove(anchor);
    }

    /**
     * Helper method that merges two keysets of maps while retaining the order of their entries. For example,
     * {1,2,3} and {1,4,3} get merged into {1,2,4,3}; {1,2,4} and {1,4,5} get merged into {1,2,4,5}
     * @param base
     *            the first map
     * @param patch
     *            the second map
     * @return a set of the keysets merged into one
     */
    public static ArrayList<Anchor> joinKeySetsRetainOrder(Map<Anchor, String> base, Map<Anchor, String> patch) {
        ArrayList<Anchor> joinedList = new ArrayList<>();
        Set<Anchor> checkSame = new HashSet<>();
        checkSame.addAll(base.keySet());
        if (checkSame.retainAll(patch.keySet())) {
            int i = 0;
            int x = 0;
            String tmp = "";

            for (Anchor s : base.keySet()) {
                i++;
                if (patch.containsKey(s)) {
                    for (Anchor t : patch.keySet()) {
                        x++;
                        if (s.getAnchor().equals(t.getAnchor()) && x == i) {
                            if (!joinedList.contains(s)) {
                                joinedList.add(s);
                            }
                            break;
                        } else if (!joinedList.contains(t)) {
                            joinedList.add(t);
                            if (base.containsKey(t)) {
                                break;
                            }
                        }
                    }
                }
                if (!joinedList.contains(s)) {
                    joinedList.add(s);
                }
                x = 0;
            }
            for (Anchor t : patch.keySet()) {
                if (!joinedList.contains(t)) {
                    joinedList.add(t);
                }
            }
            return joinedList;
        } else {
            for (Anchor s : base.keySet()) {
                if (!joinedList.contains(s)) {
                    joinedList.add(s);
                }
            }
            return joinedList;
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
    public static boolean canBeSkipped(List<Anchor> toCheck, Anchor anchor) {
        int docPartCount = 0;
        String docPart = anchor.getDocPart();
        Anchor[] anchorsWithDocPart = new Anchor[toCheck.size()];
        for (Anchor s : toCheck) {
            if (docPart.equals(s.getDocPart())) {
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
     * Method that takes the base String of the resulting document, appends the current anchor/document part,
     * and deletes it from both maps
     * @param mergedString
     *            The String that will become the new document
     * @param toAppend
     *            The text of the current anchor
     * @param splitBase
     *            The map containing the anchors of the base document
     * @param splitPatch
     *            The map containing the anchors of the patch
     * @param curAnchor
     *            The current anchor that should be deleted from the maps
     * @return the current resulting document
     */
    public static String addTextAndDeleteCurrentAnchor(String mergedString, String toAppend,
        Map<Anchor, String> splitBase, Map<Anchor, String> splitPatch, Anchor curAnchor) {
        mergedString += toAppend;
        MergeUtil.removeKeyFromMaps(splitBase, splitPatch, curAnchor);
        return mergedString;
    }

    /**
     * @param name
     *            The name of the merge strategy you want a MergeStrategy object of
     * @return The MergeStrategy of the given name if it exists, otherwise null
     */
    public static MergeStrategy getMergeStrategyOfName(String name) {
        name = name.replace("newline_", "");
        name = name.replace("_newline", "");
        for (MergeStrategy ms : MergeStrategy.values()) {
            if (ms.getName().equals(name)) {
                return ms;
            }
        }
        return null;
    }
}

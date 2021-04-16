package com.devonfw.cobigen.api.util;

/**
 * This class provides a set of transformation functions on {@link String}s
 */
public class StringUtil {

    /**
     * Capitalizes the given {@link String}
     * @param in
     *            {@link String} to be capitalized
     * @return the capitalized {@link String}
     */
    public static String capFirst(String in) {
        if (in == null || in.isEmpty()) {
            return in;
        }
        return in.substring(0, 1).toUpperCase() + in.substring(1, in.length());
    }

    /**
     * Converts the first letter of the string to a small one
     * @param in
     *            {@link String} to be modified
     * @return the {@link String} beginning with a small letter
     */
    public static String uncapFirst(String in) {
        if (in == null || in.isEmpty()) {
            return in;
        }
        return in.substring(0, 1).toLowerCase() + in.substring(1, in.length());
    }

    /**
     * Consolidates all line endings to the given one
     *
     * @param codeBlock
     *            which should be consolidate
     * @param lineDelimiter
     *            the line delimiter of the file or null if none
     * @return the consolidated code block
     * @author mbrunnli (04.06.2013)
     */
    public static String consolidateLineEndings(String codeBlock, String lineDelimiter) {
        if (lineDelimiter != null) {
            return codeBlock.replaceAll("\r\n|\r|\n", lineDelimiter);
        }
        return codeBlock;
    }

}

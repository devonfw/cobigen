package com.capgemini.cobigen.senchaplugin.util;

/**
 * This class provides a set of transformation functions on {@link String}s
 * @author mbrunnli (12.02.2013)
 */
public class StringUtil {

    /**
     * Capitalizes the given {@link String}
     * @param in
     *            {@link String} to be capitalized
     * @return the capitalized {@link String}
     * @author mbrunnli (06.02.2013)
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
     * @author mbrunnli (18.02.2013)
     */
    public static String uncapFirst(String in) {
        if (in == null || in.isEmpty()) {
            return in;
        }
        return in.substring(0, 1).toLowerCase() + in.substring(1, in.length());
    }

}

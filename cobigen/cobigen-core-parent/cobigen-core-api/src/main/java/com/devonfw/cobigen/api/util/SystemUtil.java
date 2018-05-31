package com.devonfw.cobigen.api.util;

/**
 * This util class provides system properties
 */
public class SystemUtil {

    /**
     * File separator, e.g for windows '\'
     */
    public static final String FILE_SEPARATOR = java.lang.System.getProperty("file.separator");

    /**
     * Line separator, e.g. for windows '\r\n'
     */
    public static final String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");

}

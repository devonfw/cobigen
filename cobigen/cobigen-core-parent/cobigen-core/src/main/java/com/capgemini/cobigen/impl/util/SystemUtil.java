package com.capgemini.cobigen.impl.util;

/**
 * This util class provides system properties
 *
 * @author mbrunnli (10.04.2013)
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

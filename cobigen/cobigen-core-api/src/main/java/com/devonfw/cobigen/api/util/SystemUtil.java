package com.devonfw.cobigen.api.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

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

    /**
     * Determines the line delimiter
     *
     * @param path
     *            The path containing the input file
     * @param targetCharset
     *            target char set of the file to be read and write
     * @return The line delimiter corresponding to the input file
     * @throws IOException
     *             If an exception occurs while processing the {@link BufferedInputStream} or the
     *             {@link InputStreamReader}
     */
    public static String determineLineDelimiter(Path path, String targetCharset) throws IOException {

        try (FileInputStream stream = new FileInputStream(path.toString());
            BufferedInputStream bis = new BufferedInputStream(stream);
            InputStreamReader reader = new InputStreamReader(bis, targetCharset)) {

            bis.mark(0);
            try {
                while (reader.ready()) {
                    int nextChar = reader.read();
                    if (nextChar == '\r') {
                        nextChar = reader.read();
                        if (nextChar == '\n') {
                            return "\r\n";
                        }
                        return "\r";
                    } else if (nextChar == '\n') {
                        return "\n";
                    }
                }
                return null;
            } finally {
                emptyReader(reader);
                bis.reset();
            }

        } catch (IOException e) {
            throw new IOException("Could not read file:" + path.toString(), e);
        }

    }

    /**
     * Empties the {@link InputStreamReader}
     *
     * @param reader
     *            The {@link InputStreamReader} that is to be emptied
     * @throws IOException
     *             If an exception occurs while processing the {@link InputStreamReader}
     */
    private static void emptyReader(InputStreamReader reader) throws IOException {
        while (reader.ready()) {
            reader.read();
        }

    }

}

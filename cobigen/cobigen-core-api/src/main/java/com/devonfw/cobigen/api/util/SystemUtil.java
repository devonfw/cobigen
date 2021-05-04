package com.devonfw.cobigen.api.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This util class provides system properties
 */
public class SystemUtil {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(SystemUtil.class);

    /**
     * File separator, e.g for windows '\'
     */
    public static final String FILE_SEPARATOR = java.lang.System.getProperty("file.separator");

    /**
     * Line separator, e.g. for windows '\r\n'
     */
    public static final String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");

    /** Current Operating System, the code is exectued on */
    private static final String OS = System.getProperty("os.name").toLowerCase();

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

    /**
     * @return the absolute path of the mvn executable if available, otherwise null
     */
    public static String determineMvnPath() {
        String MVN_EXEC = null;
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("which", "mvn");
        try {
            Process process = processBuilder.start();

            try (InputStreamReader in = new InputStreamReader(process.getInputStream());
                BufferedReader reader = new BufferedReader(in)) {

                // read first line only (e.g. we have multiple or windows returns with and without extension)
                String line = reader.readLine();

                int retVal = process.waitFor();
                if (retVal == 0) {
                    LOG.info("Determined mvn executable to be located in {}", line);
                    MVN_EXEC = line;
                } else {
                    LOG.warn("Could not determine mvn executable location. 'which mvn' returned {}", retVal);
                }
            }
        } catch (InterruptedException | IOException e) {
            LOG.warn("Could not determine mvn executable location", e);
        }

        if (MVN_EXEC == null) {
            String m2Home = System.getenv().get("MAVEN_HOME");
            if (m2Home != null) {
                System.setProperty("maven.home", m2Home);
            } else {
                m2Home = System.getenv().get("M2_HOME");
                if (m2Home != null) {
                    System.setProperty("maven.home", m2Home);
                } else if ("true".equals(System.getenv("TRAVIS"))) {
                    System.setProperty("maven.home", "/usr/local/maven"); // just travis
                } else {
                    LOG.error("Could not determine maven home from environment variables MAVEN_HOME or M2_HOME!");
                }
            }
        } else {
            LOG.debug("Detected to run on OS {}", OS);
            if (OS.contains("win")) {
                // running in git bash, we need to transform paths of format /c/path to C:\path
                MVN_EXEC = MVN_EXEC.replaceFirst("/([a-zA-Z])/(.*)", "$1:\\$2");
                MVN_EXEC = MVN_EXEC.replaceAll("/", "\\");
                LOG.debug("Reformatted mvn execution path to '{}' as running on win within a shell or bash", MVN_EXEC);
            }
            System.setProperty("maven.home", MVN_EXEC);
        }
        return MVN_EXEC;
    }

}

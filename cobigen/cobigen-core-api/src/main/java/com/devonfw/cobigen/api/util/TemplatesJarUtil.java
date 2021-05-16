package com.devonfw.cobigen.api.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.devonfw.cobigen.api.constants.TemplatesJarConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;

/**
 * Utilities related to the templates jar. Includes the downloading, retrieval of the jar and the checkup of
 * the templates version, to know if they are outdated.
 */
public class TemplatesJarUtil {

    /**
     * Filters the files on a directory so that we can check whether the templates jar are already downloaded
     */
    static FilenameFilter fileNameFilterJar = new FilenameFilter() {

        @Override
        public boolean accept(File dir, String name) {
            String lowercaseName = name.toLowerCase();
            String regex = TemplatesJarConstants.JAR_FILE_REGEX_NAME;

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(lowercaseName);
            return m.find();
        }
    };

    /**
     * Filters the files on a directory so that we can check whether the templates jar are already downloaded
     */
    static FilenameFilter fileNameFilterSources = new FilenameFilter() {

        @Override
        public boolean accept(File dir, String name) {
            String lowercaseName = name.toLowerCase();
            String regex = TemplatesJarConstants.SOURCES_FILE_REGEX_NAME;

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(lowercaseName);
            return m.find();
        }
    };

    /**
     * @param groupId
     *            of the artifact to download
     * @param artifactId
     *            of the artifact to download
     * @param version
     *            of the artifact to download
     * @param isDownloadSource
     *            true if downloading source jar file
     * @param templatesDirectory
     *            directory where the templates jar are located
     * @return fileName Name of the file downloaded
     */
    private static String downloadJar(String groupId, String artifactId, String version, boolean isDownloadSource,
        File templatesDirectory) {

        // By default the version should be latest
        if (version.isEmpty() || version == null) {
            version = "LATEST";
        }

        String mavenUrl = "https://repository.sonatype.org/service/local/artifact/maven/"
            + "redirect?r=central-proxy&g=" + groupId + "&a=" + artifactId + "&v=" + version;

        if (isDownloadSource) {
            mavenUrl = mavenUrl + "&c=sources";
        }

        String fileName = "";

        File[] jarFiles;

        if (isDownloadSource) {
            jarFiles = templatesDirectory.listFiles(fileNameFilterSources);
        } else {
            jarFiles = templatesDirectory.listFiles(fileNameFilterJar);
        }

        try {
            if (jarFiles.length <= 0 || isJarOutdated(jarFiles[0], mavenUrl, isDownloadSource, templatesDirectory)) {

                HttpURLConnection conn = initializeConnection(mavenUrl);
                try (InputStream inputStream = conn.getInputStream()) {

                    fileName = conn.getURL().getFile().substring(conn.getURL().getFile().lastIndexOf("/") + 1);
                    File file = new File(templatesDirectory.getPath() + File.separator + fileName);
                    Path targetPath = file.toPath();
                    if (!file.exists()) {
                        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                conn.disconnect();
            } else {
                fileName = jarFiles[0].getPath().substring(jarFiles[0].getPath().lastIndexOf(File.separator) + 1);

            }
        } catch (IOException e) {
            throw new CobiGenRuntimeException("Could not download file from " + mavenUrl, e);
        }
        return fileName;
    }

    /**
     * @param isDownloadSource
     *            true if downloading source jar file
     * @param templatesDirectory
     *            directory where the templates jar are located
     * @return fileName Name of the file downloaded
     */
    public static String downloadLatestDevon4jTemplates(boolean isDownloadSource, File templatesDirectory) {

        return downloadJar(TemplatesJarConstants.DEVON4J_TEMPLATES_GROUPID,
            TemplatesJarConstants.DEVON4J_TEMPLATES_ARTIFACTID, "LATEST", isDownloadSource, templatesDirectory);
    }

    /**
     * Checks whether there is a newer version of the templates on Maven
     * @param jarFile
     *            our jar file that we want to check
     * @param mavenUrl
     *            the URL from where we are going to retrieve the latest jar
     * @param isDownloadSource
     *            true if downloading source jar file
     * @param templatesDirectory
     *            directory where the templates jar are located
     * @return true if our jar is outdated false otherwise
     * @throws IOException
     *             {@link IOException} occurred
     * @throws ProtocolException
     *             {@link ProtocolException} occurred
     * @throws MalformedURLException
     *             {@link MalformedURLException} occurred
     */
    private static boolean isJarOutdated(File jarFile, String mavenUrl, boolean isDownloadSource,
        File templatesDirectory) throws MalformedURLException, ProtocolException, IOException {
        String fileName = jarFile.getPath().substring(jarFile.getPath().lastIndexOf(File.separator) + 1);
        Matcher m = matchJarVersion(fileName, isDownloadSource);
        if (m.find() == false || m.group(2).isEmpty()) {
            // Maybe the jar is corrupted, let's update it
            return true;
        } else {
            // Split the version number because it contains dots e.g. 3.1.0
            int[] versionNumbers = Arrays.stream(m.group(2).split("\\.")).mapToInt(Integer::parseInt).toArray();

            // We do the same for the latest jar in Maven, therefore we need to download it
            HttpURLConnection conn = initializeConnection(mavenUrl);
            try (InputStream inputStream = conn.getInputStream()) {
                String latestJar = conn.getURL().getFile().substring(conn.getURL().getFile().lastIndexOf("/") + 1);
                m = matchJarVersion(latestJar, isDownloadSource);
            }

            if (m.find() == false || m.group(2).isEmpty()) {
                return false;
            }
            // Split the version number because it contains dots e.g. 3.1.0
            int[] versionNumbersLatest = Arrays.stream(m.group(2).split("\\.")).mapToInt(Integer::parseInt).toArray();

            for (int i = 0; i < versionNumbersLatest.length; i++) {
                if (versionNumbersLatest[i] > versionNumbers[i]) {
                    if (isDownloadSource == false) {
                        // we now need to download the latest sources
                        downloadLatestDevon4jTemplates(true, templatesDirectory);
                    }
                    jarFile.delete();
                    return true;
                }
            }

            return false;
        }

    }

    /**
     * Used for matching the jar version. For instance, we want to match "3.0.0" from templates-devon4j-3.0.0
     * @param fileName
     *            String to get matched by the regex
     * @param isDownloadSource
     *            true if downloading source jar file
     * @return the Matcher
     */
    private static Matcher matchJarVersion(String fileName, boolean isDownloadSource) {
        String lowercaseName = fileName.toLowerCase();
        String regex = TemplatesJarConstants.JAR_VERSION_REGEX_CHECK;
        if (isDownloadSource) {
            regex = TemplatesJarConstants.SOURCES_VERSION_REGEX_CHECK;
        }

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(lowercaseName);
        return m;
    }

    /**
     * Initializes a new connection to the specified Maven URL
     * @param mavenUrl
     *            the URL we need to connect to
     * @return the connection instance
     * @throws MalformedURLException
     *             if the URL is invalid
     * @throws IOException
     *             if we could not connect properly
     * @throws ProtocolException
     *             if the request protocol is invalid
     */
    private static HttpURLConnection initializeConnection(String mavenUrl)
        throws MalformedURLException, IOException, ProtocolException {
        URL url = new URL(mavenUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        return conn;
    }

    /**
     * Returns the file path of the templates jar
     * @param isSource
     *            true if we want to get source jar file path
     * @param templatesDirectory
     *            directory where the templates are located
     * @return file of the jar downloaded or null if it was not found
     */
    public static File getJarFile(boolean isSource, File templatesDirectory) {

        File[] jarFiles;

        if (isSource) {
            jarFiles = templatesDirectory.listFiles(fileNameFilterSources);
        } else {
            jarFiles = templatesDirectory.listFiles(fileNameFilterJar);
        }

        if (jarFiles.length > 0) {
            return jarFiles[0];
        } else {
            // There are no jars downlaoded
            return null;
        }
    }
}

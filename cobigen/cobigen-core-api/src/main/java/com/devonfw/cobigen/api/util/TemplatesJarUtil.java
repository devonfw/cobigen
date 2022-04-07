package com.devonfw.cobigen.api.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.TemplatesJarConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;

/**
 * Utilities related to the templates jar. Includes the downloading, retrieval of the jar and the checkup of the
 * templates version, to know if they are outdated.
 */
public class TemplatesJarUtil {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(TemplatesJarUtil.class);

  /**
   * @param groupId of the artifact to download
   * @param artifactId of the artifact to download
   * @param version of the artifact to download
   * @param isDownloadSource true if downloading source jar file
   * @param templatesDirectory directory where the templates jar are located
   * @return fileName Name of the file downloaded
   */
  private static String downloadJar(String groupId, String artifactId, String version, boolean isDownloadSource,
      File templatesDirectory) {

    // By default the version should be latest
    if (version.isEmpty() || version == null) {
      version = "LATEST";
    }

    String mavenUrl = "https://repository.sonatype.org/service/local/artifact/maven/" + "redirect?r=central-proxy&g="
        + groupId + "&a=" + artifactId + "&v=" + version;

    if (isDownloadSource) {
      mavenUrl = mavenUrl + "&c=sources";
    }

    String fileName = "";

    Path jarFilePath = getJarFile(isDownloadSource, templatesDirectory.toPath());
    try {
      if (jarFilePath == null || !Files.exists(jarFilePath)
          || isJarOutdated(jarFilePath.toFile(), mavenUrl, isDownloadSource, templatesDirectory)) {

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
        fileName = jarFilePath.toFile().getPath()
            .substring(jarFilePath.toFile().getPath().lastIndexOf(File.separator) + 1);
      }
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Could not download file from " + mavenUrl, e);
    }
    return fileName;
  }

  /**
   * @param isDownloadSource true if downloading source jar file
   * @param templatesDirectory directory where the templates jar are located
   * @return fileName Name of the file downloaded
   */
  public static String downloadLatestDevon4jTemplates(boolean isDownloadSource, File templatesDirectory) {

    return downloadJar(TemplatesJarConstants.DEVON4J_TEMPLATES_GROUPID,
        TemplatesJarConstants.DEVON4J_TEMPLATES_ARTIFACTID, "LATEST", isDownloadSource, templatesDirectory);
  }

  /**
   * Checks whether there is a newer version of the templates on Maven
   *
   * @param jarFile our jar file that we want to check
   * @param mavenUrl the URL from where we are going to retrieve the latest jar
   * @param isDownloadSource true if downloading source jar file
   * @param templatesDirectory directory where the templates jar are located
   * @return true if our jar is outdated false otherwise
   * @throws IOException {@link IOException} occurred
   * @throws ProtocolException {@link ProtocolException} occurred
   * @throws MalformedURLException {@link MalformedURLException} occurred
   */
  private static boolean isJarOutdated(File jarFile, String mavenUrl, boolean isDownloadSource, File templatesDirectory)
      throws MalformedURLException, ProtocolException, IOException {

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
   *
   * @param fileName String to get matched by the regex
   * @param isDownloadSource true if downloading source jar file
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
   *
   * @param mavenUrl the URL we need to connect to
   * @return the connection instance
   * @throws MalformedURLException if the URL is invalid
   * @throws IOException if we could not connect properly
   * @throws ProtocolException if the request protocol is invalid
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
   * Returns a list of the file paths of the template set jars
   *
   * @param templatesDirectory directory where the templates are located
   *
   * @return file of the jar downloaded or null if it was not found
   */
  public static List<Path> getJarFiles(Path templatesDirectory) {

    ArrayList<Path> jarPaths = new ArrayList<>();

    try (Stream<Path> files = Files.list(templatesDirectory)) {
      files.forEach(path -> {
        if (path.toString().endsWith(".jar")) {
          jarPaths.add(path);
        }
      });
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Could not read configuration root directory.", e);
    }

    if (!jarPaths.isEmpty()) {
      return jarPaths;
    } else {
      // There are no jars downloaded
      return null;
    }
  }

  /**
   * Returns the file path of the templates jar
   *
   * @param isSource true if we want to get source jar file path
   * @param templatesDirectory directory where the templates are located
   * @return file of the jar downloaded or null if it was not found
   *
   */
  public static Path getJarFile(boolean isSource, Path templatesDirectory) {

    List<Path> jarPaths = null;
    String regex = isSource ? TemplatesJarConstants.SOURCES_FILE_REGEX_NAME : TemplatesJarConstants.JAR_FILE_REGEX_NAME;
    Pattern pattern = Pattern.compile(regex);

    try (Stream<Path> stream = Files.list(templatesDirectory)) {
      jarPaths = stream.filter(path -> pattern.matcher(path.toString()).find()).collect(Collectors.toList());
    } catch (IOException e) {
      LOG.error("Error while reading templates directory", e);
    }

    if (jarPaths != null && !jarPaths.isEmpty()) {
      return jarPaths.get(0);
    } else {
      // There are no jars downlaoded
      return null;
    }
  }

}

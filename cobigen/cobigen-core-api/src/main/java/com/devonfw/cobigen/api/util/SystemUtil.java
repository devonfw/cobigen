package com.devonfw.cobigen.api.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;

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

  /** Maven exectuable */
  private static Path MVN_EXEC = null;

  /**
   * Determines the line delimiter
   *
   * @param path The path containing the input file
   * @param targetCharset target char set of the file to be read and write
   * @return The line delimiter corresponding to the input file
   * @throws IOException If an exception occurs while processing the {@link BufferedInputStream} or the
   *         {@link InputStreamReader}
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
      } catch (IOException e) {
        throw new IOException("Could not read next char of file:" + path.toString(), e);
      }

    } catch (IOException e) {
      throw new IOException("Could not read file:" + path.toString(), e);
    }

  }

  /**
   * Empties the {@link InputStreamReader}
   *
   * @param reader The {@link InputStreamReader} that is to be emptied
   * @throws IOException If an exception occurs while processing the {@link InputStreamReader}
   */
  private static void emptyReader(InputStreamReader reader) throws IOException {

    while (reader.ready()) {
      reader.read();
    }

  }

  /**
   * @return the absolute path of the mvn executable if available, otherwise null
   */
  public static Path determineMvnPath() {

    if (MVN_EXEC != null) {
      return MVN_EXEC;
    }

    ProcessBuilder processBuilder = new ProcessBuilder();
    if (OS.contains("win")) {
      processBuilder.command("where", "mvn");
    } else {
      processBuilder.command("which", "mvn");
    }
    String mvnExecPath = null;
    try {
      Process process = processBuilder.start();

      try (InputStream in = process.getInputStream();
          InputStreamReader inr = new InputStreamReader(in);
          BufferedReader reader = new BufferedReader(inr)) {

        List<String> foundEntries = reader.lines().collect(Collectors.toList());
        LOG.debug("Found following executables: ");
        foundEntries.forEach(e -> LOG.debug("  - {}", e));
        if (foundEntries.size() > 0) {
          if (foundEntries.size() > 1 && OS.contains("win")) {
            Pattern p = Pattern.compile(".+mvn\\.(bat|cmd)");
            Optional<String> foundPath = foundEntries.stream().filter(path -> p.matcher(path).matches()).findFirst();
            if (foundPath.isPresent()) {
              mvnExecPath = foundPath.get();
              LOG.debug("Taking {} instead of first entry as detected windows OS", mvnExecPath);
            }
          }
          if (mvnExecPath == null) {
            mvnExecPath = foundEntries.get(0);
          }
        }

        int retVal = process.waitFor();
        if (retVal == 0 && StringUtils.isNotEmpty(mvnExecPath)) {
          LOG.info("Determined mvn executable to be located in {}", mvnExecPath);
          MVN_EXEC = Paths.get(mvnExecPath);
        } else {
          LOG.warn("Could not determine mvn executable location. 'which mvn' returned {}", retVal);
        }
      }
    } catch (InterruptedException | IOException e) {
      LOG.warn("Could not determine mvn executable location, trying to look for environment variables for maven home.",
          e);
    }

    if (MVN_EXEC == null) {
      String m2Home = System.getenv().get("MAVEN_HOME");
      if (m2Home == null) {
        m2Home = System.getenv().get("M2_HOME");
        if (m2Home == null) {
          if ("true".equals(System.getenv("TRAVIS"))) {
            m2Home = "/usr/local/maven"; // just travis
          } else {
            throw new CobiGenRuntimeException(
                "Could not determine maven home from environment variables MAVEN_HOME or M2_HOME!");
          }
        }
      }
      MVN_EXEC = getMvnExecutable(m2Home);
      LOG.info("Determined maven executable at {}", MVN_EXEC);
    } else {
      LOG.debug("Detected to run on OS {}", OS);
      MVN_EXEC = convertUnixPathToWinOnWin(mvnExecPath);
    }
    return MVN_EXEC;
  }

  /**
   * Convert /c/path to C:\path if running on OS windows and the given path is a unix path (e.g. produced by git bash)
   *
   * @param path the path to be converted
   * @return the converted path or the input if not on windows
   */
  public static Path convertUnixPathToWinOnWin(String path) {

    Path returnVal = Paths.get(path);
    if (OS.contains("win")) {
      Pattern p = Pattern.compile("/([a-zA-Z])/(.+)");
      Matcher matcher = p.matcher(path);
      if (matcher.matches()) {
        LOG.debug("unix path {} matches with group1='{}' and group2='{}'", path, matcher.group(1), matcher.group(2));
        returnVal = Paths.get(matcher.group(1) + ":\\" + matcher.group(2).replace("/", "\\"));
        LOG.debug("Reformatted unix path to '{}' as running on windows within a shell or bash", returnVal);
      } else {
        LOG.debug("unix path {} does not match", path);
      }
    }
    return returnVal;
  }

  /**
   * Determine mvn executable depending on the OS
   *
   * @param mvnHome the maven home dir
   * @return the mvn executable path
   */
  private static Path getMvnExecutable(String mvnHome) {

    return Paths.get(mvnHome).resolve("bin/mvn" + (OS.contains("win") ? ".cmd" : ""));
  }

}

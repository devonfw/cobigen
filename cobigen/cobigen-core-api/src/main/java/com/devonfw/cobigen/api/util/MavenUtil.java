package com.devonfw.cobigen.api.util;

import java.awt.geom.IllegalPathStateException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import com.devonfw.cobigen.api.constants.MavenConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

/**
 * Utils to operate with maven artifacts
 */
public class MavenUtil {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(MavenUtil.class);

  // maven repository
  private static Path MAVEN_REPOSITORY = null;

  /**
   * Executes a Maven class path build command which will download all the transitive dependencies needed for the CLI
   *
   * @param pomFile POM file that defines the needed CobiGen dependencies to build
   * @param cpFile the cpFile to be created
   */
  public static void cacheMavenClassPath(Path pomFile, Path cpFile) {

    cleanupExistingClassPathCaches(cpFile);

    LOG.info("Calculating class path for {} and downloading the needed maven dependencies. Please be patient...",
        pomFile);
    List<String> args = Lists.newArrayList(SystemUtil.determineMvnPath().toString(), "dependency:build-classpath",
        "-Dmdep.outputFile=" + cpFile.toString());
    if (pomFile.getFileSystem().provider().getClass().getSimpleName().equals("ZipFileSystemProvider")) {
      pomFile = createCachedPomFromJar(pomFile, cpFile.getParent());
      // just add this command in case your are working on jar
      // otherwise, pom resolution will fail if you work on a general maven module
      args.add("-f");
      args.add(pomFile.toString());
    }
    runCommand(pomFile.getParent(), args);
    LOG.debug("Downloaded dependencies successfully.");
  }

  /**
   * Resolve all maven dependencies of given project
   *
   * @param mvnProjectRoot the maven project root
   */
  public static void resolveDependencies(Path mvnProjectRoot) {

    LOG.info(
        "Resolving maven dependencies for maven project {} to be able to make use of reflection in templates. Please be patient...",
        mvnProjectRoot);
    List<String> args = Lists.newArrayList(SystemUtil.determineMvnPath().toString(), "dependency:resolve");
    runCommand(mvnProjectRoot, args);
    LOG.debug("Downloaded dependencies successfully.");
  }

  /**
   * Resolve all maven dependencies of given pom.xml inside given project
   *
   * @param pomFile to cache and resolve from
   *
   * @param mvnProjectRoot the maven project root
   */
  public static void resolveDependencies(Path pomFile, Path mvnProjectRoot) {

    LOG.info(
        "Resolving maven dependencies for maven project {} to be able to make use of reflection in templates. Please be patient...",
        mvnProjectRoot);

    if (pomFile.getFileSystem().provider().getClass().getSimpleName().equals("ZipFileSystemProvider")) {
      pomFile = createCachedPomFromJar(pomFile, mvnProjectRoot);
    }

    List<String> args = Lists.newArrayList(SystemUtil.determineMvnPath().toString());
    args.add("-f");
    args.add(pomFile.toString());
    args.add("dependency:resolve");
    runCommand(mvnProjectRoot, args);
    LOG.debug("Downloaded dependencies successfully.");
  }

  /**
   * Generates a cached-pom.xml file from a jar archive, automatically deletes the cached-pom.xml on exit
   *
   * @param pomFile to cache
   * @param outputPath output directory
   * @return the cached-pom.xml file
   */
  public static Path createCachedPomFromJar(Path pomFile, Path outputPath) {

    Path cachedPomXml = outputPath.resolve("cached-pom.xml");
    try {
      if (Files.exists(cachedPomXml)) {
        Files.delete(cachedPomXml);
      }
      Files.copy(pomFile, cachedPomXml);
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Unable to extract " + pomFile.toUri() + " from JAR to " + cachedPomXml, e);
    }
    pomFile = cachedPomXml;
    cachedPomXml.toFile().deleteOnExit();
    return cachedPomXml;
  }

  /**
   * Adds URLs from class paths cache file to URLClassLoader. If no class paths cache file was found a new one will be
   * generated.
   *
   * @param classPathCacheFile the class paths cache file to read/create
   * @param pomFile POM file that defines the needed CobiGen dependencies to build
   * @param parentClassLoader parent ClassLoader
   *
   * @return URLClassLoader
   */
  public static URLClassLoader addURLsFromCachedClassPathsFile(Path classPathCacheFile, Path pomFile,
      ClassLoader parentClassLoader) {

    if (!Files.exists(classPathCacheFile)) {
      LOG.debug("Building class paths for maven configuration ...");
      cacheMavenClassPath(pomFile, classPathCacheFile);
    } else {
      LOG.debug("Taking cached class paths from: {}", classPathCacheFile);
    }
    try (Stream<String> fileLinesStream = Files.lines(classPathCacheFile)) {
      URL[] classPathEntries = fileLinesStream
          .flatMap(e -> Arrays.stream(e.split(SystemUtil.getOS().contains("win") ? ";" : ":"))).map(path -> {
            try {
              return new File(path).toURI().toURL();
            } catch (MalformedURLException e) {
              LOG.error("URL of class path entry {} is malformed", path, e);
            }
            return null;
          }).toArray(size -> new URL[size]);
      try {
        validateCachedClassPaths(classPathEntries);
      } catch (FileNotFoundException | URISyntaxException | IllegalPathStateException e) {
        cacheMavenClassPath(pomFile, classPathCacheFile);
      }
      return new URLClassLoader(classPathEntries, parentClassLoader);
    } catch (IOException e) {
      throw new CobiGenRuntimeException("Unable to read " + classPathCacheFile, e);
    }
  }

  /**
   * Validating the cached classpath entries and resolving missing files and dependencies from a repository that is not
   * the current maven repository with a update of the classpath cache
   *
   * @param classPathEntries URLs of the cached classPath entries
   * @throws URISyntaxException
   * @throws FileNotFoundException
   * @throws MalformedURLException
   */
  private static void validateCachedClassPaths(URL[] classPathEntries)
      throws URISyntaxException, FileNotFoundException, MalformedURLException {

    URI repo = determineMavenRepositoryPath().toUri().toURL().toURI(); // to change /// to / in the URI
    for (URL classPath : classPathEntries) {
      try {
        URI cp = classPath.toURI();
        if (!Paths.get(cp).startsWith(Paths.get(repo))) {
          LOG.warn(
              "Cache {} pointing to another maven Repository, this could cause some problems, the dependencies will be resolved again",
              cp.toString());
          throw new IllegalPathStateException();
        }
        File cpFile = new File(cp);
        if (!cpFile.exists()) {
          LOG.warn("Cache {} is outdated, the dependencies will be resolved again", cp.toString());
          throw new FileNotFoundException();
        }
      } catch (URISyntaxException e) {
        LOG.error("Error while reading files from Cache");
        throw e;
      }
    }
  }

  /**
   * Generates a hash for the provided POM file
   *
   * @param pomFile to generate hash from
   * @return String generated hash
   */
  public static String generatePomFileHash(Path pomFile) {

    String pomFileHash;
    try {
      // concat pom.xml and m2repo Path bytes
      ByteSource m2repo = ByteSource.wrap(determineMavenRepositoryPath().toString().getBytes());
      ByteSource m2repoAndPom = ByteSource.concat(m2repo, ByteSource.wrap(Files.readAllBytes(pomFile)));
      pomFileHash = m2repoAndPom.hash(Hashing.murmur3_128()).toString();
    } catch (IOException e) {
      LOG.warn("Could not calculate hash of {}", pomFile.toUri());
      pomFileHash = "";
    }
    return pomFileHash;
  }

  /**
   * @return the maven repository path
   */
  public static Path determineMavenRepositoryPath() {

    if (MAVEN_REPOSITORY != null) {
      return MAVEN_REPOSITORY;
    }
    LOG.info("Determine maven repository path");
    String m2Repo = runCommand(SystemUtils.getUserHome().toPath(),
        Lists.newArrayList(SystemUtil.determineMvnPath().toString(), "help:evaluate",
            "-Dexpression=settings.localRepository", "-DforceStdout"));
    LOG.debug("Determined {} as maven repository path.", m2Repo);
    return Paths.get(m2Repo);
  }

  /**
   * Execute any command in the given execution directory with the given arguments
   *
   * @param execDir the execution directory the command should run in
   * @param args the maven arguments to execute
   * @return the process output
   */
  private static String runCommand(Path execDir, List<String> args) {

    // https://stackoverflow.com/a/66801171
    args.add("-Djansi.force=true");
    args.add("-Djansi.passthrough=true");
    args.add("-B");
    args.add("-q");
    args.add("-Dorg.slf4j.simpleLogger.defaultLogLevel=" + (LOG.isDebugEnabled() ? "DEBUG" : "INFO"));
    args.add("-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=WARN");

    try {
      StartedProcess process = new ProcessExecutor().readOutput(true).destroyOnExit().directory(execDir.toFile())
          .environment("MAVEN_OPTS", replaceAllUnixPathsOnWin(System.getenv("MAVEN_OPTS")))
          .environment("M2_REPO", replaceAllUnixPathsOnWin(System.getenv("M2_REPO"))).command(args)
          .redirectError(
              Slf4jStream.of(LoggerFactory.getLogger(MavenUtil.class.getName() + "." + "dep-build")).asError())
          .redirectOutput(
              Slf4jStream.of(LoggerFactory.getLogger(MavenUtil.class.getName() + "." + "dep-build")).asInfo())
          .start();

      Future<ProcessResult> future = process.getFuture();
      ProcessResult processResult = future.get();

      if (processResult.getExitValue() != 0) {
        LOG.error("Error while getting all the needed transitive dependencies. Please check your internet connection.");
        throw new CobiGenRuntimeException("Unable to build cobigen dependencies");
      }
      return processResult.getOutput().getString("UTF-8");
    } catch (InterruptedException | ExecutionException | IOException e) {
      throw new CobiGenRuntimeException("Unable to build cobigen dependencies", e);
    }
  }

  /**
   * @return the string while all absolute unix paths in the string are converted to windows absolute paths if running
   *         on windows OS
   */
  private static String replaceAllUnixPathsOnWin(String string) {

    if (StringUtils.isNotEmpty(string)) {
      Pattern p = Pattern.compile("=((/[^/]+)+/?)");
      Matcher matcher = p.matcher(string);
      StringBuffer sb = new StringBuffer();

      while (matcher.find()) {
        matcher.appendReplacement(sb,
            "=" + SystemUtil.convertUnixPathToWinOnWin(matcher.group(1)).toString().replace("\\", "\\\\"));
      }
      matcher.appendTail(sb);
      string = sb.toString();
    }
    return string;
  }

  /**
   * Cleanup old classpath cache files
   *
   * @param cpFile classpath file location
   */
  private static void cleanupExistingClassPathCaches(Path cpFile) {

    Path rootPath = cpFile.getParent();
    try {
      Files.walk(rootPath)
          .filter(p -> p.getFileName().startsWith(MavenConstants.CLASSPATH_CACHE_FILE.replace("%s.txt", "")))
          .forEach(p -> {
            try {
              Files.delete(p);
            } catch (IOException e) {
              LOG.warn("Failed cleaning up old classpath cache file {}", p);
            }
          });
    } catch (IOException e) {
      LOG.warn("Failed cleaning up old classpath cache files in {}", rootPath);
    }
  }

  /**
   * Tries to find a pom.xml file in the passed folder
   *
   * @param source folder where we check if a pom.xml file is found
   * @param foundPom whether a pom has already been detected
   * @param topLevel whether to find top-level pom only
   * @return the pom.xml file if it was found, null otherwise
   */
  private static Path findPom(Path source, boolean foundPom, boolean topLevel) {

    if (source == null) {
      return null;
    }

    if (Files.isRegularFile(source)) {
      if (source.getFileName().toString().equals(MavenConstants.POM)) {
        if (Files.exists(source)) {
          LOG.info("{} is already a pom.xml.");
          return source;
        }
      }
      LOG.warn("File {} neither exists nor is a {}, trying to search for {} in one of the parent folders.", source,
          MavenConstants.POM, MavenConstants.POM);
      return findPom(source.getParent(), foundPom, topLevel);
    } else {
      Path pomFile = source.resolve(MavenConstants.POM);
      if (Files.exists(pomFile) && Files.isRegularFile(pomFile)) {
        // try searching another parent
        if (!topLevel || findPom(source.getParent(), false, topLevel) == null) {
          LOG.debug("Stop searching pom as no pom.xml in parent directory. Taking {} as top-level pom.xml", pomFile);
          return pomFile;
        }
      }

      if (foundPom) {
        return null;
      } else {
        return findPom(source.getParent(), foundPom, topLevel);
      }
    }
  }

  /**
   * Tries to find the root folder of the project in order to build the classpath. This method is trying to find the
   * first pom.xml file and then getting the folder where is located
   *
   * @param inputFile passed by the user
   * @param topLevel whether to find top-level pom only
   * @return the project folder
   *
   */
  public static Path getProjectRoot(Path inputFile, boolean topLevel) {

    Path pomFile = findPom(inputFile, false, topLevel);
    if (pomFile != null) {
      LOG.info("Found project root in path {}", pomFile.getParent());
      return pomFile.getParent();
    }
    LOG.debug("Project root could not be found.");
    return null;
  }

}
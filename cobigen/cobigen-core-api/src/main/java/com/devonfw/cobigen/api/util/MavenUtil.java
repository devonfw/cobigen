package com.devonfw.cobigen.api.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import com.devonfw.cobigen.api.constants.MavenConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.google.common.collect.Lists;

/**
 * Utils to operate with maven artifacts
 */
public class MavenUtil {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(MavenUtil.class);

    /**
     * Executes a Maven class path build command which will download all the transitive dependencies needed
     * for the CLI
     * @param pomFile
     *            POM file that defines the needed CobiGen dependencies to build
     * @param cpFile
     *            the cpFile to be created
     */
    public static void cacheMavenClassPath(Path pomFile, Path cpFile) {
        cleanupExistingClassPathCaches(cpFile);

        LOG.info("Calculating class path for {} and downloading the needed maven dependencies. Please be patient...",
            pomFile);
        List<String> args = Lists.newArrayList(SystemUtil.determineMvnPath(), "dependency:build-classpath",
            // https://stackoverflow.com/a/66801171
            "-Djansi.force=true", "-Djansi.passthrough=true", "-B",
            "-Dorg.slf4j.simpleLogger.defaultLogLevel=" + (LOG.isDebugEnabled() ? "DEBUG" : "INFO"),
            "-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=WARN",
            "-Dmdep.outputFile=" + cpFile.toString(), "-q");
        if (pomFile.getFileSystem().provider().getClass().getSimpleName().equals("ZipFileSystemProvider")) {
            Path cachedPomXml = cpFile.resolveSibling("cached-pom.xml");
            try {
                Files.copy(pomFile, cachedPomXml);
            } catch (IOException e) {
                throw new CobiGenRuntimeException(
                    "Unable to extract " + pomFile.toUri() + " from JAR to " + cachedPomXml);
            }
            pomFile = cachedPomXml;
            cachedPomXml.toFile().deleteOnExit();
            // just add this command in case your are working on jar
            // otherwise, pom resolution will fail if you work on a general maven module
            args.add("-f");
            args.add(pomFile.toString());
        }
        runCommand(pomFile.getParent(), args);
        LOG.debug("Downloaded dependencies successfully.");

    }

    /**
     * Execute any command in the given execution directory with the given arguments
     * @param execDir
     *            the execution directory the command should run in
     * @param args
     *            the maven arguments to execute
     */
    private static void runCommand(Path execDir, List<String> args) {
        try {
            StartedProcess process = new ProcessExecutor().destroyOnExit().directory(execDir.toFile()).command(args)
                .redirectError(
                    Slf4jStream.of(LoggerFactory.getLogger(MavenUtil.class.getName() + "." + "dep-build")).asError())
                .redirectOutput(
                    Slf4jStream.of(LoggerFactory.getLogger(MavenUtil.class.getName() + "." + "dep-build")).asInfo())
                .start();

            Future<ProcessResult> future = process.getFuture();
            ProcessResult processResult = future.get();

            if (processResult.getExitValue() != 0) {
                LOG.error(
                    "Error while getting all the needed transitive dependencies. Please check your internet connection.");
                throw new CobiGenRuntimeException("Unable to build cobigen dependencies");
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new CobiGenRuntimeException("Unable to build cobigen dependencies", e);
        }
    }

    /**
     * Cleanup old classpath cache files
     * @param cpFile
     *            classpath file location
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
}

package com.devonfw.cobigen.api.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

        LOG.info("Calculating class path and downloading the needed maven dependencies. Please be patient...");
        try {
            StartedProcess process = new ProcessExecutor().destroyOnExit()
                .command(SystemUtil.determineMvnPath(), "dependency:build-classpath",
                    // https://stackoverflow.com/a/66801171
                    "-Djansi.force=true", "-Djansi.passthrough=true", "-B",
                    "-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn",
                    "-Dmdep.outputFile=" + cpFile.toString(), "-q", "-f", pomFile.toFile().getCanonicalPath())
                .redirectError(
                    Slf4jStream.of(LoggerFactory.getLogger(MavenUtil.class.getName() + "." + "dep-build")).asError())
                .redirectOutput(
                    Slf4jStream.of(LoggerFactory.getLogger(MavenUtil.class.getName() + "." + "dep-build")).asDebug())
                .start();

            Future<ProcessResult> future = process.getFuture();
            ProcessResult processResult = future.get();

            if (processResult.getExitValue() != 0) {
                LOG.error(
                    "Error while getting all the needed transitive dependencies. Please check your internet connection.");
                throw new CobiGenRuntimeException("Unable to build cobigen dependencies");
            }
            LOG.debug('\n' + "Download the needed dependencies successfully.");
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

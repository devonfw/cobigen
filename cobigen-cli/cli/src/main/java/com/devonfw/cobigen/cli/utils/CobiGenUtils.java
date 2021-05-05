package com.devonfw.cobigen.cli.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.jar.JarFile;

import net.sf.mmm.code.impl.java.JavaContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.InputInterpreter;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.api.util.SystemUtil;
import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MavenConstants;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.google.common.base.Charsets;

import classloader.Agent;

/**
 * Utilities class for CobiGen related operations. For instance, it creates a new CobiGen instance and
 * registers all the plug-ins
 */
public class CobiGenUtils {

    /**
     * Logger instance for the CLI
     */
    private static Logger LOG = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * Registers CobiGen plug-ins and instantiates CobiGen
     * @return object of CobiGen
     */
    public CobiGen initializeCobiGen() {
        CobiGen cg = null;
        try {
            registerPlugins();
            cg = CobiGenFactory.create();
        } catch (InvalidConfigurationException e) {
            // if the context configuration is not valid
            LOG.error("Invalid configuration of context", e);
        } catch (IOException e) {
            // If I/O operation failed then it will throw exception
            LOG.error("I/O operation is failed", e);
        }
        return cg;
    }

    /**
     * Registers the given different CobiGen plug-ins by building an artificial POM extracted next to the CLI
     * location and then adding the needed URLs to the class loader.
     */
    public void registerPlugins() {

        try {
            // Get location of the current CLI jar
            File locationCLI = new File(CobiGenUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path rootCLIPath = locationCLI.getParentFile().toPath();

            File pomFile = extractArtificialPom(rootCLIPath);

            File cpFile = rootCLIPath.resolve(MavenConstants.CLASSPATH_OUTPUT_FILE).toFile();
            if (!cpFile.exists()) {
                buildCobiGenDependencies(pomFile);
            }

            // Read classPath.txt file and add to the class path all dependencies
            try (BufferedReader br = new BufferedReader(new FileReader(cpFile))) {
                String allJars = br.readLine();

                addJarsToClassLoader(allJars);
            } catch (IOException e) {
                LOG.error("Unable to read classPath.txt file.", e);
            }

        } catch (URISyntaxException e) {
            LOG.error("Not able to convert current location of the CLI to URI. Most probably this is a bug", e);
        }

    }

    /**
     * Executes a Maven class path build command which will download all the transitive dependencies needed
     * for the CLI
     * @param pomFile
     *            POM file that defines the needed CobiGen dependencies to build
     */
    private void buildCobiGenDependencies(File pomFile) {
        LOG.info(
            "As this is your first execution of the CLI, we are going to download the needed dependencies. Please be patient...");
        try {
            StartedProcess process = new ProcessExecutor()
                .command(SystemUtil.determineMvnPath(), "dependency:build-classpath",
                    // https://stackoverflow.com/a/66801171
                    "-Djansi.force=true", "-Djansi.passthrough=true", "-B",
                    "-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn",
                    "-Dmdep.outputFile=" + MavenConstants.CLASSPATH_OUTPUT_FILE, "-q",
                    "-f " + pomFile.getCanonicalPath())
                .redirectError(
                    Slf4jStream.of(LoggerFactory.getLogger(getClass().getName() + "." + "dep-build")).asError())
                .redirectOutput(
                    Slf4jStream.of(LoggerFactory.getLogger(getClass().getName() + "." + "dep-build")).asDebug())
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
     * Extracts an artificial POM which defines all the CobiGen plug-ins that are needed
     * @param rootCLIPath
     *            path where the artificial POM will be extracted to
     * @return the extracted POM file
     */
    public File extractArtificialPom(Path rootCLIPath) {
        File pomFile = rootCLIPath.resolve(MavenConstants.POM).toFile();
        if (!pomFile.exists()) {
            try (InputStream resourcesIS = (getClass().getResourceAsStream("/" + MavenConstants.POM));) {
                Files.copy(resourcesIS, pomFile.getAbsoluteFile().toPath());
            } catch (IOException e1) {
                throw new CobiGenRuntimeException("Failed to extract CobiGen plugins pom.", e1);
            }
        }
        return pomFile;
    }

    /**
     * Adds a jar file into the current class loader
     * @param allJars
     *            file to load
     */
    public void addJarsToClassLoader(String allJars) {
        String[] jarsToAdd = allJars.split(File.pathSeparator);
        try {
            for (String jarToAdd : jarsToAdd) {
                JarFile jar = new JarFile(jarToAdd);
                Agent.appendJarFile(jar);
            }
        } catch (MalformedURLException e) {
            LOG.error("Not able to form URL of jar file.", e);
        } catch (SecurityException e) {
            LOG.error(
                "Security exception. Most probably you do not have enough permissions. Please execute the CLI using admin rights.",
                e);
        } catch (IOException e) {
            LOG.error("CobiGen plug-in jar file that was being loaded was not found. "
                + "Please try again or file an issue in cobigen GitHub repo.", e);
        }

    }

    /**
     * For Increments Returns a list that retains only the elements in this list that are contained in the
     * specified collection (optional operation). In other words, the resultant list removes from this list
     * all of its elements that are not contained in the specified collection.
     *
     * @param currentList
     *            list containing elements to be retained in this list
     * @param listToIntersect
     *            second list to be used for the intersection
     * @return resultant list containing increments that are in both lists
     */
    public static List<IncrementTo> retainAllIncrements(List<IncrementTo> currentList,
        List<IncrementTo> listToIntersect) {

        List<IncrementTo> resultantList = new ArrayList<>();

        for (IncrementTo currentIncrement : currentList) {
            String currentIncrementDesc = currentIncrement.getDescription().trim().toLowerCase();
            for (IncrementTo intersectIncrement : listToIntersect) {

                String intersectIncrementDesc = intersectIncrement.getDescription().trim().toLowerCase();

                if (currentIncrementDesc.equals(intersectIncrementDesc)) {
                    resultantList.add(currentIncrement);
                    break;
                }
            }
        }
        return resultantList;
    }

    /**
     * For Templates Returns a list that retains only the elements in this list that are contained in the
     * specified collection (optional operation). In other words, the resultant list removes from this list
     * all of its elements that are not contained in the specified collection.
     *
     * @param currentList
     *            list containing elements to be retained in this list
     * @param listToIntersect
     *            second list to be used for the intersection
     * @return resultant list containing increments that are in both lists
     */
    public static List<TemplateTo> retainAllTemplates(List<TemplateTo> currentList, List<TemplateTo> listToIntersect) {

        List<TemplateTo> resultantList = new ArrayList<>();

        for (TemplateTo currentTemplate : currentList) {
            String currentTemplateDesc = currentTemplate.getId().trim().toLowerCase();
            for (TemplateTo intersectTemplate : listToIntersect) {

                String intersectTemplateDesc = intersectTemplate.getId().trim().toLowerCase();

                if (currentTemplateDesc.equals(intersectTemplateDesc)) {
                    resultantList.add(currentTemplate);
                    break;
                }
            }
        }
        return resultantList;
    }

    /**
     * Processes the given input file to be converted into a valid CobiGen input. Also if the input is Java,
     * will create the needed class loader
     * @param cg
     *            CobiGen instance
     * @param inputFile
     *            user's input file
     * @param isJavaInput
     *            true if input is Java code
     * @return valid cobiGen input
     * @throws InputReaderException
     *             throws {@link InputReaderException} when the input file could not be converted to a valid
     *             CobiGen input
     */
    public static Object getValidCobiGenInput(CobiGen cg, File inputFile, Boolean isJavaInput)
        throws InputReaderException {
        Object input;
        // If it is a Java file, we need the class loader
        if (isJavaInput) {
            JavaContext context = ParsingUtils.getJavaContext(inputFile, ParsingUtils.getProjectRoot(inputFile));
            input = process(cg, inputFile, context.getClassLoader());
        } else {
            input = process(cg, inputFile, null);
        }
        return input;
    }

    /**
     * Processes the given file to be converted into any CobiGen valid input format
     * @param file
     *            {@link File} converted into any CobiGen valid input format
     * @param cl
     *            {@link ClassLoader} to be used, when considering Java-related inputs
     * @param inputInterpreter
     *            parse cobiGen compliant input from the file
     * @throws InputReaderException
     *             if the input retrieval did not result in a valid CobiGen input
     * @return a CobiGen valid input
     */
    public static Object process(InputInterpreter inputInterpreter, File file, ClassLoader cl)
        throws InputReaderException {
        if (!file.exists() || !file.canRead()) {
            throw new InputReaderException("Could not read input file " + file.getAbsolutePath());
        }
        Object input = null;
        try {
            input = inputInterpreter.read(Paths.get(file.toURI()), Charsets.UTF_8, cl);
        } catch (InputReaderException e) {
            LOG.debug("No input reader was able to read file {}", file.toURI(), e);
        }
        if (input != null) {
            return input;
        }
        throw new InputReaderException("The file " + file.getAbsolutePath() + " is not a valid input for CobiGen.");
    }

}

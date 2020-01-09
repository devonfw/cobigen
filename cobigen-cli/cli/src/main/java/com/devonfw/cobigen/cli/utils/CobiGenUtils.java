package com.devonfw.cobigen.cli.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

import net.sf.mmm.code.impl.java.JavaContext;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.InputInterpreter;
import com.devonfw.cobigen.api.constants.TemplatesJarConstants;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InputReaderException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.api.util.CobiGenPathUtil;
import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MavenConstants;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.util.TemplatesJarUtil;
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
    private static Logger logger = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * File of the templates jar
     */
    File templatesJar;

    /**
     * Directory where all our templates jar are located
     */
    File jarsDirectory = CobiGenPathUtil.getTemplatesFolderPath().toFile();

    /**
     * Declare utiClasses as List
     */
    List<Class<?>> utilClasses;

    /**
     * getter for templates utils classes
     * @return list of UtilClasses
     */
    public List<Class<?>> getUtilClasses() {
        return utilClasses;
    }

    /**
     * Whether the template dependency is given.
     */
    private boolean templateDependencyIsGiven = false;

    /**
     * Resolves all utilities classes, which have been defined in the templates jar.
     * @param templatesJar
     *            templates jar where we will try to find the list of classes
     *
     * @return the list of classes
     *
     *         if no generator configuration project exists
     * @throws IOException
     *             {@link IOException} occurred
     */
    List<Class<?>> resolveTemplateUtilClassesFromJar(File templatesJar) throws IOException {
        final List<Class<?>> result = new LinkedList<>();
        ClassLoader inputClassLoader =
            URLClassLoader.newInstance(new URL[] { templatesJar.toURI().toURL() }, getClass().getClassLoader());
        URL contextConfigurationLocation = inputClassLoader.getResource("context.xml");
        if (contextConfigurationLocation == null
            || contextConfigurationLocation.getPath().endsWith("target/classes/context.xml")) {
            contextConfigurationLocation = inputClassLoader.getResource("src/main/templates/context.xml");
            if (contextConfigurationLocation == null) {
                throw new CobiGenRuntimeException("No context.xml could be found in the classpath!");
            } else {

                final Map<String, String> env = new HashMap<>();

                String[] pathTemplate = contextConfigurationLocation.toString().split("!");
                FileSystem fs;
                try {
                    fs = FileSystems.getFileSystem(URI.create(pathTemplate[0]));
                } catch (FileSystemNotFoundException e) {
                    fs = FileSystems.newFileSystem(URI.create(pathTemplate[0]), env);
                }
                final Path path = fs.getPath(pathTemplate[1]);

                Paths.get(URI.create("file://" + path.toString())).getParent().getParent().getParent();

            }
        } else {
            Paths.get(URI.create(contextConfigurationLocation.toString()));
        }
        logger.debug("Found context.xml @ " + contextConfigurationLocation.toString());
        final List<String> foundClasses = new LinkedList<>();
        if (contextConfigurationLocation.toString().startsWith("jar")) {
            logger.debug("Processing configuration archive " + contextConfigurationLocation.toString());
            try {
                // Get the URI of the jar from the URL of the contained context.xml
                URI jarUri = URI.create(contextConfigurationLocation.toString().split("!")[0]);
                FileSystem jarfs = FileSystems.getFileSystem(jarUri);

                // walk the jar file
                logger.debug("Searching for classes in " + jarUri.toString());
                Files.walkFileTree(jarfs.getPath("/"), new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (file.toString().endsWith(".class")) {
                            logger.debug("    * Found class file " + file.toString());
                            // remove the leading '/' and the trailing '.class'
                            String fileName = file.toString().substring(1, file.toString().length() - 6);
                            // replace the path separator '/' with package separator '.' and add it to the
                            // list of found files
                            foundClasses.add(fileName.replace("/", "."));
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        // Log errors but do not throw an exception
                        logger.warn(exc.getMessage());
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                logger.error("An exception occurred while processing Jar files to create CobiGen_Templates folder", e);
            }
            for (String className : foundClasses) {
                try {
                    result.add(inputClassLoader.loadClass(className));
                } catch (ClassNotFoundException e) {
                    logger.warn("Could not load " + className + " from classpath", e);
                }
            }
        }

        return result;

    }

    /**
     * Registers CobiGen plug-ins and instantiates CobiGen
     * @return object of CobiGen
     */
    public CobiGen initializeCobiGen() {
        CobiGen cg = null;
        try {
            registerPlugins();
            getTemplatesJar(false);
            getTemplates();
            cg = CobiGenFactory.create(templatesJar.toURI());
            return cg;

        } catch (InvalidConfigurationException e) {
            // if the context configuration is not valid
            logger.error("Invalid configuration of context ");
        } catch (IOException e) {
            // If I/O operation failed then it will throw exception
            logger.error("I/O operation is failed ");
        }

        return cg;

    }

    /**
     * @return list of all classes, which have been defined in the template configuration folder from a jar
     */
    public List<Class<?>> getTemplates() {
        templatesJar = TemplatesJarUtil.getJarFile(false, jarsDirectory);

        try {
            utilClasses = resolveTemplateUtilClassesFromJar(templatesJar);
        } catch (IOException e) {
            logger.error(
                "IO exception due to unable to resolves all classes, which have been defined in the template configuration folder from a jar");

        }
        return utilClasses;
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
                logger.error("Unable to read classPath.txt file.", e);
            }

        } catch (URISyntaxException e) {
            logger.error("Not able to convert current location of the CLI to URI. Most probably this is a bug", e);
        }

    }

    /**
     * Executes a Maven class path build command which will download all the transitive dependencies needed
     * for the CLI
     * @param pomFile
     *            POM file that defines the needed CobiGen dependencies to build
     */
    private void buildCobiGenDependencies(File pomFile) {
        logger.info(
            "As this is your first execution of the CLI, we are going to download the needed dependencies. Please be patient...");
        try {

            InvocationRequest request = new DefaultInvocationRequest();
            request.setPomFile(pomFile);
            request.setGoals(Arrays.asList(MavenConstants.DEPENDENCY_BUILD_CLASSPATH,
                "-Dmdep.outputFile=" + MavenConstants.CLASSPATH_OUTPUT_FILE, "-q"));

            Invoker invoker = new DefaultInvoker();
            InvocationResult result = null;

            // Progress bar starts
            Thread t1 = new Thread(new ProgressBar());
            t1.start();

            try {
                invoker.setMavenHome(new File(System.getenv("MAVEN_HOME")));
            } catch (NullPointerException e) {
                logger.error(
                    "MAVEN_HOME environment variable has not been set on your machine. CobiGen CLI needs Maven correctly configured.",
                    e);
            }
            result = invoker.execute(request);
            if (t1 != null) {
                t1.interrupt();
            }
            logger.debug('\n' + "Download the needed dependencies successfully.");
            if (result.getExitCode() != 0) {
                logger.error(
                    "Error while getting all the needed transitive dependencies. Please check your internet connection.");
            }

        } catch (MavenInvocationException e) {

            logger.error("The maven command for getting needed dependencies was malformed. This is a bug.");
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
                logger.error(
                    "Failed to extract CobiGen plugins pom into your computer. Maybe you need to use admin permissions.");
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

                // Setting the template jar path
                if (checkTemplateDepencency(jarToAdd)) {
                    jarsDirectory = new File(jarToAdd).getParentFile();
                    templateDependencyIsGiven = true;
                }
            }
        } catch (MalformedURLException e) {
            logger.error("Not able to form URL of jar file.", e);
        } catch (SecurityException e) {
            logger.error(
                "Security exception. Most probably you do not have enough permissions. Please execute the CLI using admin rights.");
        } catch (IOException e) {
            logger.error("CobiGen plug-in jar file that was being loaded was not found. "
                + "Please try again or file an issue in cobigen GitHub repo.");
        }

    }

    /**
     * Tries to find the templates jar. If it was not found, it will download it and then return it.
     * @param isSource
     *            true if we want to get source jar file path
     * @return the jar file of the templates
     */
    public File getTemplatesJar(boolean isSource) {
        File jarFileDir = jarsDirectory.getAbsoluteFile();
        if (TemplatesJarUtil.getJarFile(isSource, jarFileDir) == null) {
            try {
                if (!templateDependencyIsGiven) {
                    TemplatesJarUtil.downloadLatestDevon4jTemplates(isSource, jarFileDir);
                }

            } catch (MalformedURLException e) {
                // if a path of one of the class path entries is not a valid URL
                logger.error("Problem while downloading the templates, URL not valid. This is a bug", e);
            } catch (IOException e) {
                // IOException occurred
                logger.error(
                    "Problem while downloading the templates, most probably you are facing connection issues.\n\n"
                        + "Please try again later.",
                    e);
            }
        }
        return TemplatesJarUtil.getJarFile(isSource, jarFileDir);
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
     * @return <tt>resultant list</tt> containing increments that are in both lists
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
     * @return <tt>resultant list</tt> containing increments that are in both lists
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
            // nothing
        }
        if (input != null) {
            return input;
        }
        throw new InputReaderException("The file " + file.getAbsolutePath() + " is not a valid input for CobiGen.");
    }

    /**
     * Checks whether the current jar is CobiGen templates
     * @param jarToAdd
     *            jar that we check
     * @return true if jar is CobiGen templates
     */
    private boolean checkTemplateDepencency(String jarToAdd) {
        return jarToAdd.contains(TemplatesJarConstants.DEVON4J_TEMPLATES_ARTIFACTID);
    }

}

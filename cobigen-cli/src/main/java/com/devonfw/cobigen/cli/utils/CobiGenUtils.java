package com.devonfw.cobigen.cli.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import net.sf.mmm.code.impl.java.JavaContext;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.Merger;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.api.util.CobiGenPathUtil;
import com.devonfw.cobigen.cli.CobiGenCLI;
import com.devonfw.cobigen.cli.constants.MavenConstants;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.extension.PluginRegistry;
import com.devonfw.cobigen.impl.util.TemplatesJarUtil;
import com.devonfw.cobigen.maven.validation.InputPreProcessor;

/**
 * Utils class for CobiGen related operations. For instance, creates a new CobiGen instance and registers all
 * the plugins
 */
public class CobiGenUtils {

    private static Logger logger = LoggerFactory.getLogger(CobiGenCLI.class);

    /**
     * File of the templates jar
     */
    File templatesJar;

    /**
     * Directory where all our templates jar are located
     */
    File jarsDirectory = CobiGenPathUtil.getTemplatesFolderPath().toFile();

    List<Class<?>> utilClasses;

    /** Current registered input objects */
    private List<Object> inputs;

    /**
     * getter for templates utils classes
     * @return list of UtilClasses
     */
    public List<Class<?>> getUtilClasses() {
        return utilClasses;
    }

    /**
     * Resolves all classes, which have been defined in the template configuration folder from a jar.
     *
     * @return the list of classes
     *
     *         if no generator configuration project exists
     * @throws IOException
     *             {@link IOException} occurred
     */
    List<Class<?>> resolveTemplateUtilClassesFromJar(File jarPath) throws IOException {
        final List<Class<?>> result = new LinkedList<>();
        Path templateRoot;
        ClassLoader inputClassLoader =
            URLClassLoader.newInstance(new URL[] { jarPath.toURI().toURL() }, getClass().getClassLoader());
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

                templateRoot = Paths.get(URI.create("file://" + path.toString())).getParent().getParent().getParent();

            }
        } else {
            templateRoot = Paths.get(URI.create(contextConfigurationLocation.toString()));
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
     * @param User
     *            input entity file
     * @return
     */
    public CobiGen initializeCobiGen() {
        getTemplates();

        CobiGen cg = null;
        if (templatesJar != null) {
            try {
                registerPlugins();
                cg = CobiGenFactory.create(templatesJar.toURI());

                return cg;

            } catch (InvalidConfigurationException e) {
                // if the context configuration is not valid
                logger.error("Invalid configuration of context ");
            } catch (IOException e) {
                // If I/O operation failed then it will throw exception
                logger.error("I/O operation is failed ");
            }

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
        } catch (IOException e2) {
            logger.error(
                "IO exception due to unable to resolves all classes, which have been defined in the template configuration folder from a jar");

        }
        return utilClasses;
    }

    /**
     * @param fs
     * @param to
     * @throws IOException
     */
    public void extractPom(FileSystem fs, Path to) throws IOException {
        Path from = fs.getPath("META-INF", "maven", MavenConstants.COBIGEN_GROUP_ID, "javaplugin");

        try (final Stream<Path> sources = Files.walk(from)) {
            sources.forEach(src -> {
                if (src.getFileName().toString().equals("pom.xml")) {
                    try {
                        logger.debug("Extracting file {} to {}", from, to);
                        Files.copy(src, to, StandardCopyOption.REPLACE_EXISTING);

                    } catch (IOException e) {
                        throw new RuntimeException("Failed to unzip file.", e);
                    }
                }
            });
        }

    }

    /**
     * @param jar
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws MalformedURLException
     */
    public void addJarToClasspath(File jar) throws NoSuchMethodException, SecurityException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException, MalformedURLException {
        // Get the ClassLoader class
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        Class<?> clazz = cl.getClass();

        // Get the protected addURL method from the parent URLClassLoader class
        Method method = clazz.getSuperclass().getDeclaredMethod("addURL", new Class[] { URL.class });

        // Run projected addURL method to add JAR to classpath
        method.setAccessible(true);
        method.invoke(cl, new Object[] { jar.toURI().toURL() });
    }

    /**
     * Registers the given different CobiGen plugins
     */
    public void registerPlugins() {

        try {
            // Get location of the current CLI jar
            File locationCLI = new File(CobiGenUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path rootCLIPath = locationCLI.getParentFile().toPath();

            File pomFile = rootCLIPath.resolve(MavenConstants.POM).toFile();
            if (!pomFile.exists()) {
                try (InputStream resourcesIS = (getClass().getResourceAsStream("/" + MavenConstants.POM));) {
                    Files.copy(resourcesIS, pomFile.getAbsoluteFile().toPath());
                }
            }

            File cpFile = rootCLIPath.resolve(MavenConstants.OUTPUT_FILE).toFile();
            if (!cpFile.exists()) {
                logger.info(
                    "As this is your first execution of the CLI, we are going to download the needed dependencies...");
                InvocationRequest request = new DefaultInvocationRequest();
                request.setPomFile(pomFile);
                request.setGoals(Arrays.asList(MavenConstants.DEPENDENCY_BUILD_CLASSPATH,
                    "-Dmdep.outputFile=" + MavenConstants.OUTPUT_FILE));

                Invoker invoker = new DefaultInvoker();
                InvocationResult result = invoker.execute(request);
                if (result.getExitCode() != 0) {
                    logger.error(
                        "Error while getting all the needed transitive dependencies. Please check your internet connection.");
                }
            }

            // Read classPath.txt file and add to the class path all dependencies
            try (BufferedReader br = new BufferedReader(new FileReader(cpFile))) {
                String line = br.readLine();

                for (String jarToAdd : line.split(";")) {
                    addJarToClasspath(new File(jarToAdd));
                }
            }

        } catch (MavenInvocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (NoSuchMethodException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IllegalArgumentException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    /**
     * @param locationCLI
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    private File downloadPlugin(File locationCLI) throws MalformedURLException, IOException {
        // Download plug-in jar from Maven
        String downloadedJarName =
            TemplatesJarUtil.downloadJar(MavenConstants.COBIGEN_GROUP_ID, "javaplugin", "LATEST", false, locationCLI);

        File pluginJar = locationCLI.toPath().resolve(downloadedJarName).toFile();

        // We need to extract the pom.xml from the jar
        final URI jarFileUri = URI.create("jar:file:" + pluginJar.toURI().getPath());
        final FileSystem fs = FileSystems.newFileSystem(jarFileUri, new HashMap<>());
        extractPom(fs, locationCLI.toPath().resolve(MavenConstants.POM));

        return pluginJar;
    }

    /**
     * Register all mergers defined on that GeneratorPluginActivator
     * @param pluginActivator
     *            plugin activator that binds all mergers
     */
    private void registerAllMergers(GeneratorPluginActivator pluginActivator) {
        for (Merger merger : pluginActivator.bindMerger()) {
            PluginRegistry.registerMerger(merger);
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

        // We first check if we already have the CobiGen_Templates jar downloaded
        if (TemplatesJarUtil.getJarFile(isSource, jarFileDir) == null) {
            try {
                TemplatesJarUtil.downloadLatestDevon4jTemplates(isSource, jarFileDir);
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
     * @return valid cobiGen input
     * @throws MojoFailureException
     *             throws {@link MojoFailureException} when the input file could not be converted to a valid
     *             CobiGen input
     */
    public static Object getValidCobiGenInput(CobiGen cg, File inputFile, Boolean isJavaInput)
        throws MojoFailureException {
        Object input;
        // If it is a Java file, we need the class loader
        if (isJavaInput) {
            JavaContext context = ParsingUtils.getJavaContext(inputFile, ParsingUtils.getProjectRoot(inputFile));
            input = InputPreProcessor.process(cg, inputFile, context.getClassLoader());
        } else {
            input = InputPreProcessor.process(cg, inputFile, null);
        }
        return input;
    }

}

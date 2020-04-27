package com.devonfw.cobigen.impl.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;

/**
 * Utilities related to the retrieval of Templates utility classes
 */
public class TemplatesClassloaderUtil {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(TemplatesClassloaderUtil.class);

    /**
     * Locations to check for context.xml
     */
    private static final String[] configFileLocations =
        new String[] { "context.xml", "src/main/templates/context.xml" };

    /**
     * Locations to check for template utility classes
     */
    private static final String[] classFolderLocations = new String[] { "target/classes" };

    /**
     * Checks the ClassLoader for any context.xml provided either in configurationFolder or in
     * templates-plugin and returns its URL
     * @param classLoader
     *            ClassLoader to check resources from
     * @return URL of the context configuration file path
     * @throws InvalidConfigurationException
     *             if no configuration file was found
     */
    public static URL getContextConfiguration(ClassLoader classLoader) throws InvalidConfigurationException {
        URL contextConfigurationLocation = null;
        for (String possibleLocation : configFileLocations) {
            URL configLocation = classLoader.getResource(possibleLocation);
            if (configLocation != null) {
                contextConfigurationLocation = configLocation;
                LOG.debug("Found context.xml URL @ {}", contextConfigurationLocation);
                break;
            }
        }

        if (contextConfigurationLocation == null) {
            throw new InvalidConfigurationException("No context.xml could be found in the classpath!");
        }
        return contextConfigurationLocation;
    }

    /**
     * Initializes the ClassLoader with given URLs array
     * @param urls
     *            URL[] Array of URLs to load into ClassLoader
     * @return ClassLoader to load resources from
     */
    private static ClassLoader getUrlClassLoader(URL[] urls) {
        ClassLoader inputClassLoader = null;
        inputClassLoader = URLClassLoader.newInstance(urls, TemplatesClassloaderUtil.class.getClassLoader());
        return inputClassLoader;
    }

    /**
     * Adds folders to class loader urls e.g. src/main/templates for config.xml detection
     * @param configurationFolder
     *            Path configuration folder for which to generate paths
     * @return ArrayList<URL> of URLs
     * @throws MalformedURLException
     *             if the URL was malformed
     */
    private static ArrayList<URL> addFoldersToClassLoaderUrls(Path configurationFolder) throws MalformedURLException {
        ArrayList<URL> classLoaderUrls = new ArrayList<>();
        for (String possibleLocation : classFolderLocations) {
            Path folder = configurationFolder;
            folder = folder.resolve(possibleLocation);
            if (Files.exists(folder)) {
                classLoaderUrls.add(folder.toUri().toURL());
                LOG.debug("Added {} to class path", folder);
            }
        }
        return classLoaderUrls;
    }

    /**
     * Walks the class path in search of a 'context.xml' resource to identify the enclosing folder or jar
     * file. That location is then searched for class files and a list with those loaded classes is returned.
     * If the sources are not compiled, the templates will not be able to be generated.
     * @param configurationFolder
     *            Path to add to ClassLoader
     * @param classLoader
     *            ClassLoader to load jar from
     * @return a List of Classes for template generation.
     * @throws IOException
     *             if either templates jar or templates folder could not be read
     */
    public static List<Class<?>> resolveUtilClasses(Path configurationFolder, ClassLoader classLoader)
        throws IOException {
        List<Class<?>> result = new LinkedList<>();
        ArrayList<URL> classLoaderUrls = new ArrayList<>(); // stores ClassLoader URLs
        Path templateRoot = null;
        ClassLoader inputClassLoader = null;
        URL contextConfigurationLocation = null;
        if (configurationFolder != null) {
            templateRoot = configurationFolder;
            classLoaderUrls = addFoldersToClassLoaderUrls(configurationFolder);
            inputClassLoader = getUrlClassLoader(classLoaderUrls.toArray(new URL[] {}));
            contextConfigurationLocation = configurationFolder.toUri().toURL();
        } else {
            inputClassLoader = classLoader;
            contextConfigurationLocation = getContextConfiguration(inputClassLoader);
        }

        if (contextConfigurationLocation.toString().startsWith("jar")) {
            result = resolveFromJar(result, inputClassLoader, contextConfigurationLocation);
        } else {
            result = resolveFromFolder(result, templateRoot, inputClassLoader);
        }

        return result;
    }

    /**
     * Resolves utility classes from Folder
     *
     * @param result
     *            List to store utility classes in
     * @param templateRoot
     *            Path to template folder containing classes
     * @param inputClassLoader
     *            ClassLoader to use for storing of classes
     * @return List of classes to load utilities from
     */
    private static List<Class<?>> resolveFromFolder(List<Class<?>> result, Path templateRoot,
        ClassLoader inputClassLoader) {
        LOG.debug("Processing configuration folder {}", templateRoot.toString());
        LOG.info("Searching for classes in configuration folder...");
        List<Path> foundPaths = new LinkedList<>();

        try {
            foundPaths = walkTemplateFolder(templateRoot);
        } catch (IOException e) {
            LOG.error("Could not read templates folder", e);
        }
        if (foundPaths.size() > 0) {

            // clean up test classes
            Iterator<Path> it = foundPaths.iterator();
            while (it.hasNext()) {
                Path next = it.next();
                if (!templateRoot.relativize(next).startsWith("target/classes")) {
                    LOG.debug("    * Removed test class file {}", next);
                    it.remove();
                }
            }

            for (Path path : foundPaths) {
                try {
                    result.add(loadClassByPath(templateRoot.relativize(path), inputClassLoader));
                } catch (ClassNotFoundException e) {
                    LOG.error("Class could not be loaded into ClassLoader", e);
                }
            }
        } else {
            LOG.info("Could not find any compiled classes to be loaded as util classes in template folder.");
        }

        return result;
    }

    /**
     * Resolves utility classes from Jar archive
     *
     * @param result
     *            List to store utility classes in
     * @param inputClassLoader
     *            ClassLoader to use for storing of classes
     * @param contextConfigurationLocation
     *            URL to check for classes
     * @return List of classes to load utilities from
     */
    private static List<Class<?>> resolveFromJar(List<Class<?>> result, ClassLoader inputClassLoader,
        URL contextConfigurationLocation) {
        LOG.debug("Processing configuration archive {}", contextConfigurationLocation);
        LOG.info("Searching for classes in configuration archive...");

        List<String> foundClasses = new LinkedList<>();
        try {
            // Get the URI of the jar from the URL of the contained context.xml
            URI jarUri = URI.create(contextConfigurationLocation.toString().split("!")[0]);

            // Make sure to create file system for jar file
            FileSystem jarfs = FileSystemUtil.getOrCreateFileSystem(jarUri);

            foundClasses = walkJarFile(jarUri, jarfs);
        } catch (IOException e) {
            LOG.error("Could not read templates jar file", e);
        }
        if (foundClasses.size() > 0) {
            for (String className : foundClasses) {
                try {
                    result.add(inputClassLoader.loadClass(className));
                } catch (ClassNotFoundException e) {
                    LOG.warn("Could not load {} from classpath", className);
                    LOG.debug("Class was not found", e);
                }
            }
        } else {
            LOG.info("Could not find any compiled classes to be loaded as util classes in jar file.");
        }
        return result;
    }

    /**
     * Walks the template folder in search of utility classes
     *
     * @param templateRoot
     *            Path to template folder
     * @return List<Path> of paths containing a class file
     * @throws IOException
     *             if file could not be visited
     */
    private static List<Path> walkTemplateFolder(Path templateRoot) throws IOException {
        final List<Path> foundPaths = new LinkedList<>();
        Files.walkFileTree(templateRoot, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".class")) {
                    foundPaths.add(file);
                    LOG.debug("    * Found class file {}", file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // Log errors but do not throw an exception
                LOG.warn("An IOException occurred while reading file on path {} with message: {}", file,
                    exc.getMessage(), LOG.isDebugEnabled() ? exc : null);
                return FileVisitResult.CONTINUE;
            }
        });
        return foundPaths;
    }

    /**
     * Walks the jar file in search of utility classes
     *
     * @param jarUri
     *            URI of jar file
     * @param jarfs
     *            FileSystem of jar file
     * @return List<String> of file paths containing class files
     * @throws IOException
     *             if file could not be visited
     */
    private static List<String> walkJarFile(URI jarUri, FileSystem jarfs) throws IOException {
        List<String> foundClasses = new LinkedList<>();
        // walk the jar file
        LOG.debug("Searching for classes in {}", jarUri);
        Files.walkFileTree(jarfs.getPath("/"), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".class")) {
                    LOG.debug("    * Found class file {}", file);
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
                LOG.warn("An IOException occurred while reading file on path {} with message: {}", file,
                    exc.getMessage(), LOG.isDebugEnabled() ? exc : null);
                return FileVisitResult.CONTINUE;
            }
        });
        return foundClasses;
    }

    /**
     * Tries to load a class over it's file path. If the path is /a/b/c/Some.class this method tries to load
     * the following classes in this order: <list>
     * <li>Some</li>
     * <li>c.Some</li>
     * <li>b.c.Some</li>
     * <li>a.b.c.Some</> </list>
     * @param classPath
     *            the {@link Path} of the Class file
     * @param cl
     *            the used ClassLoader
     * @return Class<?> of the class file
     * @throws ClassNotFoundException
     *             if no class could be found all the way up to the path root
     */
    private static Class<?> loadClassByPath(Path classPath, ClassLoader cl) throws ClassNotFoundException {
        // Get a list with all path segments, starting with the class name
        Queue<String> pathSegments = new LinkedList<>();
        // Split the path by the systems file separator and without the .class suffix
        for (int i = classPath.getNameCount() - 1; i > -1; i--) {
            pathSegments.add(FilenameUtils.removeExtension(classPath.getName(i).getFileName().toString()));
        }

        if (!pathSegments.isEmpty()) {
            String className = "";
            while (!pathSegments.isEmpty()) {
                if (className == "") {
                    className = pathSegments.poll();
                } else {
                    className = pathSegments.poll() + "." + className;
                }
                try {
                    return cl.loadClass(className);
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    continue;
                }
            }
        }
        throw new ClassNotFoundException("Could not find class on path " + classPath.toString());

    }
}

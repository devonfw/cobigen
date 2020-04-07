package com.devonfw.cobigen.impl.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities related to the retrieval of Templates utility classes
 */
public class TemplatesClassloaderUtil {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(TemplatesClassloaderUtil.class);

    /**
     * Stores the URLs for the ClassLoader
     */
    private ArrayList<URL> classLoaderUrls;

    /**
     * Checks the ClassLoader for any context.xml provided either in configurationFolder or in
     * templates-plugin and returns its URL
     * @param classLoader
     *            ClassLoader to check resources from
     * @return URL of the context configuration file path
     * @throws IOException
     *             if no configuration file was found
     */
    public URL getContextConfiguration(ClassLoader classLoader) throws IOException {
        URL contextConfigurationLocation = null;
        String[] possibleLocations = new String[] { "context.xml", "src/main/templates/context.xml" };

        for (String possibleLocation : possibleLocations) {
            URL configLocation = classLoader.getResource(possibleLocation);
            if (configLocation != null) {
                contextConfigurationLocation = configLocation;
                LOG.debug("Found context.xml URL in the classpath @ " + contextConfigurationLocation.toString());
                break;
            }
        }

        if (contextConfigurationLocation == null) {
            throw new IOException("No context.xml could be found in the classpath!");
        }
        return contextConfigurationLocation;
    }

    /**
     * Initializes the ClassLoader with given URLs array
     * @param urls
     *            URL[] Array of URLs to load into ClassLoader
     * @return ClassLoader to load resources from
     */
    private ClassLoader getUrlClassLoader(URL[] urls) {
        ClassLoader inputClassLoader = null;
        inputClassLoader = URLClassLoader.newInstance(urls, getClass().getClassLoader());
        return inputClassLoader;
    }

    /**
     * Adds folders to class loader urls e.g. src/main/templates for config.xml detection
     * @param configurationFolder
     *            Path configuration folder for which to generate paths
     * @throws MalformedURLException
     *             if the URL was malformed
     */
    private void addFoldersToClassLoaderUrls(Path configurationFolder) throws MalformedURLException {
        String[] possibleLocations = new String[] { "src/main/templates", "target/classes" };

        for (String possibleLocation : possibleLocations) {
            Path folder = configurationFolder;
            folder = folder.resolve(possibleLocation);
            if (Files.exists(folder)) {
                classLoaderUrls.add(folder.toUri().toURL());
                LOG.debug("Added " + folder.toUri().toURL().toString() + " to class path");
            }
        }
    }

    /**
     * Walks the class path in search of a 'context.xml' resource to identify the enclosing folder or jar
     * file. That location is then searched for class files and a list with those loaded classes is returned.
     * If the sources are not compiled, the templates will not be able to be generated.
     * @param configurationFolder
     *            Path to add to ClassLoader
     * @return a List of Classes for template generation.
     * @throws IOException
     *             if either templates jar or templates folder could not be read
     */
    public List<Class<?>> resolveUtilClasses(Path configurationFolder) throws IOException {
        final List<Class<?>> result = new LinkedList<>();

        Path templateRoot = null;
        ClassLoader inputClassLoader = null;
        if (configurationFolder != null) {
            classLoaderUrls.add(configurationFolder.toUri().toURL());
            LOG.debug("Added " + configurationFolder.toUri().toURL().toString() + " to class path");
            templateRoot = configurationFolder;
            addFoldersToClassLoaderUrls(configurationFolder);
        }

        inputClassLoader = getUrlClassLoader(classLoaderUrls.toArray(new URL[] {}));

        URL contextConfigurationLocation = getContextConfiguration(inputClassLoader);

        LOG.debug("Found context.xml @ " + contextConfigurationLocation.toString());
        final List<String> foundClasses = new LinkedList<>();
        if (contextConfigurationLocation.toString().startsWith("jar")) {
            LOG.info("Processing configuration archive " + contextConfigurationLocation.toString());

            // Make sure to create file system for jar file
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");

            URI uri = URI.create(contextConfigurationLocation.toString());
            FileSystem fs;
            try {
                fs = FileSystems.getFileSystem(uri);
            } catch (FileSystemNotFoundException e) {
                fs = FileSystems.newFileSystem(uri, env);
            }
            Paths.get(uri);

            try {
                // Get the URI of the jar from the URL of the contained context.xml
                URI jarUri = URI.create(contextConfigurationLocation.toString().split("!")[0]);
                FileSystem jarfs = FileSystems.getFileSystem(jarUri);

                // walk the jar file
                LOG.debug("Searching for classes in " + jarUri.toString());
                Files.walkFileTree(jarfs.getPath("/"), new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (file.toString().endsWith(".class")) {
                            LOG.debug("    * Found class file " + file.toString());
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
                        LOG.warn("visitFileFailed @", exc);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                LOG.error("Could not read templates jar file", e);
            }
            for (String className : foundClasses) {
                try {
                    result.add(inputClassLoader.loadClass(className));
                } catch (ClassNotFoundException e) {
                    LOG.warn("Could not load " + className + " from classpath");
                    LOG.debug("Class was not found", e);
                }
            }
        } else {
            LOG.info("Processing configuration folder " + templateRoot.toString());
            LOG.debug("Searching for classes ...");
            final List<Path> foundPaths = new LinkedList<>();

            try {
                Files.walkFileTree(templateRoot, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (file.toString().endsWith(".class")) {
                            foundPaths.add(file);
                            LOG.debug("    * Found class file " + file.toString());
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        // Log errors but do not throw an exception
                        LOG.warn("visitFileFailed @", exc);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                LOG.error("Could not read templates folder", e);
            }
            if (foundPaths.size() > 0) {

                // clean up test classes
                Iterator<Path> it = foundPaths.iterator();
                while (it.hasNext()) {
                    Path next = it.next();
                    LOG.info("    * found class file " + next.toString());
                    if (!templateRoot.relativize(next).startsWith("target/classes")) {
                        LOG.info("    * Removed class file " + next.toString());
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
                LOG.info("Could not find any compiled classes to be loaded as util classes.");
            }
        }

        return result;
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
    public Class<?> loadClassByPath(Path classPath, ClassLoader cl) throws ClassNotFoundException {
        // Get a list with all path segments, starting with the class name
        Queue<String> pathSegments = new LinkedList<>();
        // Split the path by the systems file separator and without the .class suffix
        String[] pathSegmentsArray = classPath.toString().substring(0, classPath.toString().length() - 6)
            .split("\\".equals(File.separator) ? "\\\\" : File.separator);
        for (int i = pathSegmentsArray.length - 1; i > -1; i--) {
            pathSegments.add(pathSegmentsArray[i]);
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

package com.devonfw.cobigen.impl.extension;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.extension.GeneratorPluginActivator;
import com.devonfw.cobigen.api.extension.TextTemplateEngine;

/**
 * This class is is the manual implementation of a Lazy {@link ServiceLoader} allowing introspection without
 * instantiating a plug-in. Introspection get's supported by JDK 9 earliest by the ServiceLoader
 * implementation of JDK. This class can be treated as a workaround for JDK 8 and can be removed as soon as we
 * raise the lower limit of JDK support to at least JDK 9.
 */
public class ClassServiceLoader {

    /** Logger instance. */
    private static final Logger LOG = LoggerFactory.getLogger(ClassServiceLoader.class);

    /** Classes detected as GeneratorPluginActivators */
    private static Set<Class<? extends GeneratorPluginActivator>> generatorPluginActivatorClasses = new HashSet<>();

    /** Classes detected as TemplateEngines */
    private static Set<Class<? extends TextTemplateEngine>> templateEngineClasses = new HashSet<>();

    static {
        LOG.debug("Initiating CobiGen");
        lookupServices(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Detects CobiGen extensions on the classpath of the given class loader
     * @param classLoader
     *            determining the classpath to look at
     */
    public static void lookupServices(ClassLoader classLoader) {
        LOG.info("Searching for plug-ins at classloader {}", classLoader);
        if (LOG.isDebugEnabled()) {
            if (classLoader instanceof URLClassLoader) {
                LOG.debug("URL Classloader with URLs:");
                Arrays.stream(((URLClassLoader) classLoader).getURLs()).forEach(url -> LOG.debug("  * {}", url));
            }
        }
        generatorPluginActivatorClasses.clear();
        templateEngineClasses.clear();
        LOG.info("Loading plug-in activators...");
        lookupServices(GeneratorPluginActivator.class, generatorPluginActivatorClasses, classLoader);
        LOG.info("Loading template engines...");
        lookupServices(TextTemplateEngine.class, templateEngineClasses, classLoader);
    }

    /**
     * Detects services of extensions type and adds them to the clazzSet
     * @param <T>
     *            the extension type to be found by {@link ServiceLoader} mechanism
     * @param extensionType
     *            the extension type
     * @param clazzSet
     *            the set to add the detected classes
     * @param contextClassLoader
     *            the classloader to be used for classpath scanning
     */
    @SuppressWarnings("unchecked")
    private static <T> void lookupServices(Class<T> extensionType, Set<Class<? extends T>> clazzSet,
        ClassLoader contextClassLoader) {
        try {
            Enumeration<URL> foundGeneratorPluginActivators =
                contextClassLoader.getResources("META-INF/services/" + extensionType.getName());

            while (foundGeneratorPluginActivators.hasMoreElements()) {
                URL url = foundGeneratorPluginActivators.nextElement();
                LOG.debug("Found classpath entry: {}", url);
                String activatorClassName = null;
                try {
                    URLConnection con = url.openConnection();
                    try (InputStream in = con.getInputStream()) {
                        List<String> lines = IOUtils.readLines(in, Charsets.UTF_8);
                        LOG.debug("Lines of service loader file: {}", lines);
                        if (!lines.isEmpty()) {
                            activatorClassName = lines.get(0);
                            Class<?> loadClass = contextClassLoader.loadClass(activatorClassName);
                            if (extensionType.isAssignableFrom(loadClass)) {
                                LOG.info("Found {} {}", extensionType.getSimpleName(), activatorClassName);
                                clazzSet.add((Class<T>) loadClass);
                            } else {
                                LOG.warn("ServiceLoader extension with class {} is not a subclass of {}. Skipping...",
                                    activatorClassName, extensionType.getCanonicalName());
                            }
                        }
                    }
                } catch (IOException e) {
                    LOG.error("Could not read plug-in at {}", url, LOG.isDebugEnabled() ? e : null);
                } catch (ClassNotFoundException e) {
                    LOG.error("Could not load plug-in with class {}", activatorClassName,
                        LOG.isDebugEnabled() ? e : null);
                }
            }
            if (clazzSet.isEmpty()) {
                LOG.error("At least one plug-in should be registered of type {}", extensionType.getSimpleName());
            }
        } catch (Throwable e1) {
            LOG.error("Unable to retrieve {} by ServiceLoader interface", extensionType, e1);
        }
    }

    /**
     * @return the detected classes of {@link GeneratorPluginActivator}
     */
    public static Set<Class<? extends GeneratorPluginActivator>> getGeneratorPluginActivatorClasses() {
        return generatorPluginActivatorClasses;
    }

    /**
     * @return the detected classes of {@link TextTemplateEngine}
     */
    public static Set<Class<? extends TextTemplateEngine>> getTemplateEngineClasses() {
        return templateEngineClasses;
    }

}

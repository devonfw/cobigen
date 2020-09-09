package com.devonfw.cobigen.impl.extension;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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

    private static Set<Class<? extends GeneratorPluginActivator>> generatorPluginActivatorClasses = new HashSet<>();

    private static Set<Class<? extends TextTemplateEngine>> templateEngineClasses = new HashSet<>();

    static {
        lookupServices(GeneratorPluginActivator.class, generatorPluginActivatorClasses);
        lookupServices(TextTemplateEngine.class, templateEngineClasses);
    }

    @SuppressWarnings("unchecked")
    private static <T> void lookupServices(Class<T> extensionType, Set<Class<? extends T>> clazzSet) {
        try {
            Enumeration<URL> foundGeneratorPluginActivators =
                ClassLoader.getSystemResources("META-INF/services/" + extensionType.getCanonicalName());

            while (foundGeneratorPluginActivators.hasMoreElements()) {
                URL url = foundGeneratorPluginActivators.nextElement();
                String activatorClassName = null;
                try {
                    URLConnection con = url.openConnection();
                    try (InputStream in = con.getInputStream()) {
                        List<String> lines = IOUtils.readLines(in, Charsets.UTF_8);
                        if (!lines.isEmpty()) {
                            activatorClassName = lines.get(0);
                            Class<?> loadClass =
                                ClassServiceLoader.class.getClassLoader().loadClass(activatorClassName);
                            if (extensionType.isAssignableFrom(loadClass)) {
                                clazzSet.add((Class<T>) loadClass);
                            } else {
                                LOG.warn("ServiceLoader extension with class {} is not a subclass of {}. Skipping...",
                                    activatorClassName, extensionType.getCanonicalName());
                            }
                        }
                    }
                } catch (IOException e) {
                    LOG.warn("Could not read plug-in at {}", url, LOG.isDebugEnabled() ? e : null);
                } catch (ClassNotFoundException e) {
                    LOG.warn("Could not load plug-in with class {}", activatorClassName,
                        LOG.isDebugEnabled() ? e : null);
                }
            }
        } catch (Throwable e1) {
            LOG.error("Unable to retrieve {} by ServiceLoader interface", extensionType, e1);
        }
    }

    public static Set<Class<? extends GeneratorPluginActivator>> getGeneratorPluginActivatorClasses() {
        return generatorPluginActivatorClasses;
    }

    public static Set<Class<? extends TextTemplateEngine>> getTemplateEngineClasses() {
        return templateEngineClasses;
    }

}

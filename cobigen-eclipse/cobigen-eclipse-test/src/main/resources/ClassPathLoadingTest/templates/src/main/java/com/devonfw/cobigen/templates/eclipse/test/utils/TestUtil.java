package com.devonfw.cobigen.templates.eclipse.test.utils;

import java.lang.annotation.Annotation;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.ws.rs.Path;

public class TestUtil {

    /** not supported anymore as of the new jdk.internal.loader.ClassLoaders$AppClassLoader of JDK9+ 
     * depends on module specification, respectively inclusion of namespaces.
     */
    public static Object getAnnotation(Class<?> type, String name) {

        return getAnnotation(type, name, Thread.currentThread().getContextClassLoader());
    }

    public static Object getAnnotation2(Class<?> type, String name) {

        return getAnnotation(type, name, type.getClassLoader());
    }

    public static Object getAnnotation(Class<?> type, String name, ClassLoader loader) {

        try {
            Class a = loader.loadClass(name);
            return getAnnotation(type, a);
        } catch (ClassNotFoundException e) {
            return e;
        }
    }

    public Object getAnnotation(Class<?> type) {

        return getAnnotation(type, Path.class);
    }

    public static Object getAnnotation(Class<?> type, Class<? extends Annotation> a) {

        Annotation annotation = type.getAnnotation(a);
        if (annotation == null) {
            return "null";
        }
        return annotation;
    }

    public String loadInternalClass() {
        return Path.class.getCanonicalName();
    }

}

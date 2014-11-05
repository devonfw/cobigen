/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.common.tools;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * Util functionality for {@link ClassLoader} issues
 *
 * @author mbrunnli (08.04.2013)
 */
public class ClassLoaderUtil {

    /**
     * Returns the java {@link ClassLoader} of the {@link IJavaProject} passed
     * @param proj
     *            {@link IJavaProject} for which the {@link ClassLoader} should be returned
     * @return the java {@link ClassLoader} of the {@link IJavaProject} passed
     * @throws CoreException
     *             if the Java runtime class path could not be determined
     * @throws MalformedURLException
     *             if a path of one of the class path entries is not a valid URL
     * @author mbrunnli (05.02.2013)
     */
    public static URLClassLoader getProjectClassLoader(IJavaProject proj) throws CoreException,
        MalformedURLException {
        IClasspathEntry[] classPathEntries = proj.getResolvedClasspath(true);

        List<URL> urlList = new ArrayList<>();
        for (IClasspathEntry entry : classPathEntries) {
            IPath path = entry.getPath();
            if (entry.getEntryKind() != IClasspathEntry.CPE_SOURCE) {
                urlList.add(path.toFile().toURI().toURL());
            }
        }

        String[] sourceClassPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(proj);
        for (String entry : sourceClassPathEntries) {
            urlList.add(new Path(entry).toFile().toURI().toURL());
        }

        ClassLoader parentClassLoader = proj.getClass().getClassLoader();
        URL[] urls = urlList.toArray(new URL[urlList.size()]);
        return new URLClassLoader(urls, parentClassLoader);
    }
}

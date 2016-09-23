package com.capgemini.cobigen.eclipse.common.tools;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;

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
     */
    public static URLClassLoader getProjectClassLoader(IJavaProject proj) throws CoreException, MalformedURLException {
        IClasspathEntry[] classPathEntries = proj.getResolvedClasspath(true);
        proj.readRawClasspath();

        List<URL> urlList = new ArrayList<>();
        for (IClasspathEntry entry : classPathEntries) {
            if (entry.getEntryKind() != IClasspathEntry.CPE_SOURCE) {
                urlList.add(entry.getPath().toFile().toURI().toURL());
            } else {
                IPath outputLocation;
                if (entry.getOutputLocation() != null) {
                    outputLocation = entry.getOutputLocation();
                } else {
                    outputLocation = proj.getOutputLocation();
                }
                urlList.add(ResourcesPlugin.getWorkspace().getRoot().getLocation().append(outputLocation).toFile()
                    .toURI().toURL());
            }
        }
        urlList.add(ResourcesPlugin.getWorkspace().getRoot().getLocation().append(proj.readOutputLocation()).toFile()
            .toURI().toURL());

        ClassLoader parentClassLoader = proj.getClass().getClassLoader();
        URL[] urls = urlList.toArray(new URL[urlList.size()]);
        return new URLClassLoader(urls, parentClassLoader);
    }
}

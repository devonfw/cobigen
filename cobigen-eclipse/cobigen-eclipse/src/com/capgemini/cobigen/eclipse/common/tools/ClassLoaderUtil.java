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
 */
public class ClassLoaderUtil {

    /**
     * Returns the java {@link ClassLoader} of the {@link IJavaProject} passed
     * @param project
     *            {@link IJavaProject} for which the {@link ClassLoader} should be returned
     * @param parentClassLoader
     *            parent {@link ClassLoader} to be registered
     * @return the java {@link ClassLoader} of the {@link IJavaProject} passed
     * @throws CoreException
     *             if the Java runtime class path could not be determined
     * @throws MalformedURLException
     *             if a path of one of the class path entries is not a valid URL
     */
    public static URLClassLoader getProjectClassLoader(IJavaProject project, ClassLoader parentClassLoader)
        throws CoreException, MalformedURLException {
        IClasspathEntry[] classPathEntries = project.getResolvedClasspath(true);
        project.readRawClasspath();

        List<URL> urlList = new ArrayList<>();
        for (IClasspathEntry entry : classPathEntries) {
            if (entry.getEntryKind() != IClasspathEntry.CPE_SOURCE) {
                urlList.add(entry.getPath().toFile().toURI().toURL());
            } else {
                IPath outputLocation;
                if (entry.getOutputLocation() != null) {
                    outputLocation = entry.getOutputLocation();
                } else {
                    outputLocation = project.getOutputLocation();
                }
                urlList.add(project.getProject().getLocation()
                    .append(PathUtil.getProjectDependendFilePath(outputLocation.toString())).toFile().toURI()
                    .toURL());
            }
        }
        urlList.add(ResourcesPlugin.getWorkspace().getRoot().getLocation()
            .append(PathUtil.getProjectDependendFilePath(project.readOutputLocation().toString())).toFile()
            .toURI().toURL());

        URL[] urls = urlList.toArray(new URL[urlList.size()]);
        return new URLClassLoader(urls, parentClassLoader);
    }

    /**
     * Returns the java {@link ClassLoader} of the {@link IJavaProject} passed
     * @param project
     *            {@link IJavaProject} for which the {@link ClassLoader} should be returned
     * @return the java {@link ClassLoader} of the {@link IJavaProject} passed
     * @throws CoreException
     *             if the Java runtime class path could not be determined
     * @throws MalformedURLException
     *             if a path of one of the class path entries is not a valid URL
     */
    public static URLClassLoader getProjectClassLoader(IJavaProject project)
        throws CoreException, MalformedURLException {
        return getProjectClassLoader(project, null);
    }
}

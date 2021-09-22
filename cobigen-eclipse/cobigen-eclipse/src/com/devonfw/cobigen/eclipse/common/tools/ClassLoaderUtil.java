package com.devonfw.cobigen.eclipse.common.tools;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util functionality for {@link ClassLoader} issues
 */
public class ClassLoaderUtil {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(ClassLoaderUtil.class);

  /**
   * Returns the java {@link ClassLoader} of the {@link IJavaProject} passed
   *
   * @param project {@link IJavaProject} for which the {@link ClassLoader} should be returned
   * @param parentClassLoader parent {@link ClassLoader} to be registered
   * @return the java {@link ClassLoader} of the {@link IJavaProject} passed
   * @throws CoreException if the Java runtime class path could not be determined
   * @throws MalformedURLException if a path of one of the class path entries is not a valid URL
   */
  public static URLClassLoader getProjectClassLoader(IJavaProject project, ClassLoader parentClassLoader)
      throws CoreException, MalformedURLException {

    LinkedHashSet<URL> urlList = getProjectClasspathURLs(project);
    LOG.debug("Project class loader URLs used for generation: " + urlList);
    URL[] urls = urlList.toArray(new URL[urlList.size()]);
    return new URLClassLoader(urls, parentClassLoader);
  }

  /**
   * Returns the {@link ClassLoader} URLs for the passed {@link IJavaProject}
   *
   * @param project {@link IJavaProject} for which the {@link ClassLoader} URLs should be calculated
   * @return an ordered Set of {@link ClassLoader} URLs of the given {@link IJavaProject}
   * @throws JavaModelException if the Java runtime class path could not be determined
   * @throws MalformedURLException if a path of one of the class path entries is not a valid URL
   */
  private static LinkedHashSet<URL> getProjectClasspathURLs(IJavaProject project)
      throws MalformedURLException, JavaModelException {

    LinkedHashSet<URL> urlList = new LinkedHashSet<>();
    IClasspathEntry[] classPathEntries = project.getResolvedClasspath(true);
    for (IClasspathEntry entry : classPathEntries) {
      switch (entry.getEntryKind()) {
        case IClasspathEntry.CPE_SOURCE:
          IPath outputLocation;
          if (entry.getOutputLocation() != null) {
            outputLocation = entry.getOutputLocation();
          } else {
            outputLocation = project.getOutputLocation();
          }
          urlList.add(project.getProject().getLocation()
              .append(PathUtil.getProjectDependendFilePath(outputLocation.toString())).toFile().toURI().toURL());
          break;
        case IClasspathEntry.CPE_PROJECT:
          IProject projectDependency = ResourcesPlugin.getWorkspace().getRoot()
              .getProject(entry.getPath().lastSegment());
          IJavaProject javaProjectDependency = JavaCore.create(projectDependency);
          if (javaProjectDependency != null) {
            urlList.addAll(getProjectClasspathURLs(javaProjectDependency));
          }
          break;
        default:
          urlList.add(entry.getPath().toFile().toURI().toURL());
          break;
      }
    }

    return urlList;
  }

  /**
   * Returns the java {@link ClassLoader} of the {@link IJavaProject} passed
   *
   * @param project {@link IJavaProject} for which the {@link ClassLoader} should be returned
   * @return the java {@link ClassLoader} of the {@link IJavaProject} passed
   * @throws CoreException if the Java runtime class path could not be determined
   * @throws MalformedURLException if a path of one of the class path entries is not a valid URL
   */
  public static URLClassLoader getProjectClassLoader(IJavaProject project) throws CoreException, MalformedURLException {

    return getProjectClassLoader(project, ClassLoaderUtil.class.getClassLoader());
  }
}

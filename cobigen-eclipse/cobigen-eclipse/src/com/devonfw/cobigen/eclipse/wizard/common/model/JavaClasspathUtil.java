package com.devonfw.cobigen.eclipse.wizard.common.model;

import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This util provides JDT classpath operations
 *
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public class JavaClasspathUtil {

  /**
   * Logger instance
   */
  private static final Logger LOG = LoggerFactory.getLogger(JavaClasspathUtil.class);

  /**
   * Checks whether the given path is a compiled resource due to the {@link IPackageFragmentRoot} classpath excludes the
   * given javaElement is defined in.
   *
   * @param javaElement {@link IJavaElement} to retrieve the {@link IPackageFragmentRoot} from
   * @param path to be checked
   * @return <code>true</code> iff the given path does not match any of the {@link IPackageFragmentRoot}s classpath
   *         exclusions.
   * @throws JavaModelException if an internal exception occurs while accessing the eclipse jdt java model
   */
  public static boolean isCompiledSource(IJavaElement javaElement, String path) throws JavaModelException {

    IClasspathEntry[] classpathEntries = javaElement.getJavaProject().getRawClasspath();
    for (IClasspathEntry classpathEntry : classpathEntries) {
      // only check classpath entry for current parent
      if (classpathEntry.getPath().isPrefixOf(new Path(path))) {
        for (IPath exclusionPattern : classpathEntry.getExclusionPatterns()) {
          if (SelectorUtils.matchPath(exclusionPattern.toString(), path)) {
            LOG.debug(
                "{} in source folder {} matches exclusion pattern {} and thus will be treated as a none-compiled resource.",
                path, javaElement.getPath().toString(), exclusionPattern.toString());
            return false;
          }
        }
        if (classpathEntry.getInclusionPatterns().length == 0) {
          // Eclipse interprets an empty list of inclusion patterns as 'include all'
          return true;
        } else {
          for (IPath inclusionPattern : classpathEntry.getInclusionPatterns()) {
            if (SelectorUtils.matchPath(inclusionPattern.toString(), path)) {
              LOG.debug(
                  "{} in source folder {} matches inclusion pattern {} and thus will be treated as a compiled resource.",
                  path, javaElement.getPath().toString(), inclusionPattern.toString());
              return true;
            }
          }
        }
        return false;
      }
    }
    return false;
  }

}

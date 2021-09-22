package com.devonfw.cobigen.eclipse.common.tools;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * The {@link PathUtil} class provides some common functionality on workspace relative paths
 */
public class PathUtil {

  /**
   * Constructs a relative path of the filePath based on the project's path.
   *
   * @param project to get a relative paths for
   * @param filePath the file paths to relativize.
   * @return an {@link IFile} representing the relativized path.
   */
  public static IFile getProjectDependentFile(IProject project, Path filePath) {

    return project.getFile(getProjectDependentFilePath(project, filePath));
  }

  /**
   * Constructs a relative path of the filePath based on the project's path.
   *
   * @param project to get a relative paths for
   * @param filePath the file paths to relativize.
   * @return the relativized path.
   */
  public static String getProjectDependentFilePath(IProject project, Path filePath) {

    return Paths.get(project.getLocationURI()).relativize(filePath).toString().replaceAll("\\\\", "/");
  }

  /**
   * Filters the first segment (the project) out of the given path
   *
   * @param path which should be shortened by the first segment
   * @return the project dependent path as the project (first segment) has been deleted
   */
  public static String getProjectDependendFilePath(String path) {

    return path.substring(path.indexOf("/", 1) + 1);
  }

  /**
   * Returns the project name of the path (the first segment)
   *
   * @param path of which the first segment should be interpreted as a project
   * @return the project name of the path (the first segment)
   */
  public static String getProject(String path) {

    int nextSlash = path.indexOf("/", 1);
    int substringEnd = nextSlash == -1 ? path.length() : nextSlash;
    if (path.startsWith("/")) {
      return path.substring(1, substringEnd);
    } else {
      return path.substring(0, substringEnd);
    }
  }

  /**
   * Creates the full workspace relative path for each given relative destination path
   *
   * @param proj project
   * @param relativeDestinationPaths project relative destination paths
   * @return the {@link Set} of all workspace relative paths
   */
  public static Set<String> createWorkspaceRelativePaths(IProject proj, Set<String> relativeDestinationPaths) {

    Set<String> adaptedPaths = new HashSet<>();
    for (String p : relativeDestinationPaths) {
      adaptedPaths.add("/" + proj.getName() + "/" + p);
    }
    return adaptedPaths;
  }

  /**
   * Creates the full workspace relative path for the given relative destination path
   *
   * @param proj project
   * @param relativeDestinationPath project relative destination path
   * @return the workspace relative paths
   */
  public static String createWorkspaceRelativePath(IProject proj, String relativeDestinationPath) {

    return "/" + proj.getName() + "/" + relativeDestinationPath;
  }

  /**
   * Tries to return the relative project of a template. This is useful when a template is going to be relocated to a
   * different project from the original one (the one were the input is located). Therefore the path is relative (i.e.
   * projectname-core/../api/src/main...) and we need to get that relative project (i.e. projectname-api)
   *
   * @param filePath the path of the template that maybe needs to be relocated
   * @param project the project containing the class used as input
   * @return the same project if no relative path was found, and the relative project if a relative path has been found
   */
  public static IProject getRelativeProjectIfNeeded(String filePath, IProject project) {

    if (filePath.contains("/../")) {
      String projectPath = project.getLocation().toFile().getParent().toString();
      String projectName = projectPath.substring(projectPath.lastIndexOf(File.separator));

      // We want to get the project name after "/../" which is the child project
      int nextChildProject = filePath.indexOf("/../") + 4;
      filePath = filePath.substring(nextChildProject, filePath.length());
      filePath = projectName + "-" + filePath;

      return ResourcesPlugin.getWorkspace().getRoot().getProject(PathUtil.getProject(filePath));
    }
    return project;
  }
}

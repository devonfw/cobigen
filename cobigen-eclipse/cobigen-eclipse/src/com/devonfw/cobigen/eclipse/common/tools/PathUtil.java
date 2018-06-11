package com.devonfw.cobigen.eclipse.common.tools;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

/**
 * The {@link PathUtil} class provides some common functionality on workspace relative paths
 */
public class PathUtil {

    /**
     * Constructs a relative path of the filePath based on the project's path.
     * @param project
     *            to get a relative paths for
     * @param filePath
     *            the file paths to relativize.
     * @return an {@link IFile} representing the relativized path.
     */
    public static IFile getProjectDependentFile(IProject project, Path filePath) {
        return project.getFile(getProjectDependentFilePath(project, filePath));
    }

    /**
     * Constructs a relative path of the filePath based on the project's path.
     * @param project
     *            to get a relative paths for
     * @param filePath
     *            the file paths to relativize.
     * @return the relativized path.
     */
    public static String getProjectDependentFilePath(IProject project, Path filePath) {
        return Paths.get(project.getLocationURI()).relativize(filePath).toString().replaceAll("\\\\", "/");
    }

    /**
     * Filters the first segment (the project) out of the given path
     * @param path
     *            which should be shortened by the first segment
     * @return the project dependent path as the project (first segment) has been deleted
     */
    public static String getProjectDependendFilePath(String path) {
        return path.substring(path.indexOf("/", 1) + 1);
    }

    /**
     * Returns the project name of the path (the first segment)
     * @param path
     *            of which the first segment should be interpreted as a project
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
     * @param proj
     *            project
     * @param relativeDestinationPaths
     *            project relative destination paths
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
     * @param proj
     *            project
     * @param relativeDestinationPath
     *            project relative destination path
     * @return the workspace relative paths
     */
    public static String createWorkspaceRelativePath(IProject proj, String relativeDestinationPath) {
        return "/" + proj.getName() + "/" + relativeDestinationPath;
    }
}

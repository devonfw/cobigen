package com.capgemini.cobigen.eclipse.common.tools;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;

/**
 * The {@link PathUtil} class provides some common functionality on workspace relative paths
 * @author mbrunnli (15.02.2013)
 */
public class PathUtil {

    /**
     * Filters the first segment (the project) out of the given path
     * @param path
     *            which should be shortened by the first segment
     * @return the project dependent path as the project (first segment) has been deleted
     * @author mbrunnli (14.02.2013)
     */
    public static String getProjectDependendFilePath(String path) {
        return path.substring(path.indexOf("/", 1) + 1);
    }

    /**
     * Returns the project name of the path (the first segment)
     * @param path
     *            of which the first segment should be interpreted as a project
     * @return the project name of the path (the first segment)
     * @author mbrunnli (14.02.2013)
     */
    public static String getProject(String path) {
        if (path.startsWith("/")) {
            return path.substring(1, path.indexOf("/", 1));
        } else {
            return path.substring(0, path.indexOf("/", 1));
        }
    }

    /**
     * Creates the full workspace relative path for each given relative destination path
     * @param proj
     *            project
     * @param relativeDestinationPaths
     *            project relative destination paths
     * @return the {@link Set} of all workspace relative paths
     * @author mbrunnli (18.02.2013)
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
     * @author trippl (22.04.2013)
     */
    public static String createWorkspaceRelativePath(IProject proj, String relativeDestinationPath) {
        return "/" + proj.getName() + "/" + relativeDestinationPath;
    }
}

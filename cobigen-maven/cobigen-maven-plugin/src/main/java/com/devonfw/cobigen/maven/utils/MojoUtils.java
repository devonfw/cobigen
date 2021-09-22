package com.devonfw.cobigen.maven.utils;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.devonfw.cobigen.maven.GenerateMojo;

/**
 * A class to provide some helper methods to the {@link GenerateMojo}
 */
public class MojoUtils {
  /**
   * Returns the closest common parent path for a list of paths
   *
   * @param inputPaths a list of paths
   * @return the closest common parent path or the path build from the '/' URI if no parent could be found
   */
  public Path getCommonParent(List<Path> inputPaths) {

    switch (inputPaths.size()) {
      case 0:
        throw new IllegalArgumentException("List of paths cannot be empty.");
      case 1:
        return inputPaths.get(0);
    }

    ArrayList<Path> paths = new ArrayList<>(inputPaths.size());
    // the smallest path in the list
    int minPathDepth = Integer.MAX_VALUE;
    for (Path path : inputPaths) {
      paths.add(path.toAbsolutePath());
      if (minPathDepth > path.toAbsolutePath().getNameCount()) {
        minPathDepth = path.toAbsolutePath().getNameCount();
      }
    }
    // Truncate all paths to the minPathDepth depth
    for (int i = 0; i < paths.size(); i++) {
      Path path = paths.get(i);
      while (path.getNameCount() > minPathDepth) {
        path = path.getParent();
      }
      paths.set(i, path);
    }

    // Search for the closest parent
    for (int level = 0; level <= minPathDepth; level++) {
      boolean sameParent = true;
      Path testAgainst = paths.get(0);
      // check if the current path is already the closest parent. By transitivity the check of one path
      // with all other paths suffices
      for (Path path : paths) {
        sameParent = testAgainst.equals(path);
        if (!sameParent) {
          break;
        }
      }
      // If all paths are equal (and therefore reduced to the nearest common parent) return any of them.
      // Otherwise reduce all paths in the list by one level, i.e. replace every path in the list by
      // it's direct parent
      if (sameParent) {
        return testAgainst;
      } else {
        for (int i = 0; i < paths.size(); i++) {
          paths.set(i, paths.get(i).getParent());
        }
      }
    }

    return Paths.get(URI.create("/"));

  }
}

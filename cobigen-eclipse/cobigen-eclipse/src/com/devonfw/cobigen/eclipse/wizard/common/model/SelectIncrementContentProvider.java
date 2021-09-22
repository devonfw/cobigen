package com.devonfw.cobigen.eclipse.wizard.common.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;

import com.devonfw.cobigen.eclipse.generator.entity.ComparableIncrement;

/**
 * {@link ITreeContentProvider} for displaying an hierarchical tree of dependent generation packages.
 */
public class SelectIncrementContentProvider implements ITreePathContentProvider {

  /** All current root packages */
  private List<ComparableIncrement> rootElements;

  @Override
  public void dispose() {

  }

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

  }

  @Override
  public Object[] getElements(Object inputElement) {

    if (inputElement instanceof ComparableIncrement[]) {
      this.rootElements = new LinkedList<>();
      ComparableIncrement[] rootIncrements = (ComparableIncrement[]) inputElement;
      for (ComparableIncrement pkg : rootIncrements) {
        if (!isChildOfAnyIncrement(rootIncrements, pkg)) {
          this.rootElements.add(pkg);
        }
      }
      return this.rootElements.toArray();
    }
    return new Object[0];
  }

  /**
   * Checks whether the given target is child of any other root increment recursively
   *
   * @param rootIncrements array of all root increments
   * @param target element to be checked
   * @return <code>true</code> if the target increment is child any root inrement<br>
   *         <code>false</code> otherwise
   */
  private boolean isChildOfAnyIncrement(ComparableIncrement[] rootIncrements, ComparableIncrement target) {

    for (ComparableIncrement pkg : rootIncrements) {
      Object o = getChildren(pkg);
      if (o instanceof ComparableIncrement[]) {
        for (ComparableIncrement child : (ComparableIncrement[]) o) {
          if (target.equals(child)) {
            return true;
          }
          if (isChildOfAnyIncrement(new ComparableIncrement[] { child }, target)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public Object[] getChildren(TreePath parentPath) {

    Object lastSegment = parentPath.getLastSegment();
    return getChildren(lastSegment);
  }

  /**
   * Returns all children for the given element
   *
   * @param element for which all children should be determined
   * @return all children for the given element
   */
  private Object[] getChildren(Object element) {

    if (element instanceof ComparableIncrement) {
      ComparableIncrement[] children = ((ComparableIncrement) element).getDependentComparableIncrements()
          .toArray(new ComparableIncrement[0]);
      return children;
    }
    return new Object[0];
  }

  @Override
  public boolean hasChildren(TreePath path) {

    return getChildren(path).length != 0;
  }

  @Override
  public TreePath[] getParents(Object element) {

    Set<TreePath> parents = new HashSet<>();
    if (element instanceof ComparableIncrement) {
      for (ComparableIncrement pkg : this.rootElements) {
        if (pkg.equals(element)) {
          parents.add(TreePath.EMPTY);
        }
        addTreePaths(element, TreePath.EMPTY.createChildPath(pkg), parents, true);
      }
    }
    return parents.toArray(new TreePath[0]);
  }

  /**
   * Adds all {@link TreePath}s ending on the given element or being the parent of the given element to the given
   * {@link Set} 'out'.
   *
   * @param element all paths should end with
   * @param treePath current {@link TreePath} in order to track the path over recursive method calls
   * @param out {@link Set} of {@link TreePath} all found {@link TreePath}s should be added to
   * @param parentPaths states whether the {@link TreePath}s of the child or of its parent should be determined
   */
  private void addTreePaths(Object element, TreePath treePath, Set<TreePath> out, boolean parentPaths) {

    for (Object child : getChildren(treePath)) {
      if (element.equals(child)) {
        out.add(parentPaths ? treePath : treePath.createChildPath(child));
      }
      addTreePaths(element, treePath.createChildPath(child), out, parentPaths);
    }
  }

  /**
   * Returns all paths for the given element
   *
   * @param element for which all paths should be determined
   * @return array of {@link TreePath}s ending on the given element
   */
  public TreePath[] getAllPaths(Object element) {

    Set<TreePath> paths = new HashSet<>();
    if (element instanceof ComparableIncrement) {
      for (ComparableIncrement increment : this.rootElements) {
        TreePath path = TreePath.EMPTY.createChildPath(increment);
        if (increment.equals(element)) {
          paths.add(path);
        }
        addTreePaths(element, path, paths, false);
      }
    }
    return paths.toArray(new TreePath[0]);
  }

  /**
   * @return {@link TreePath}s of all root elements
   */
  public TreePath[] getAllRootPaths() {

    Set<TreePath> paths = new HashSet<>();
    for (ComparableIncrement increment : this.rootElements) {
      paths.add(TreePath.EMPTY.createChildPath(increment));
    }
    return paths.toArray(new TreePath[0]);
  }
}

package com.devonfw.cobigen.eclipse.wizard.common.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The {@link HierarchicalTreeOperator} is a wrapper for all tree related functionality for displaying packages in a
 * hierarchical folded way
 */
public class HierarchicalTreeOperator {

  /**
   * Cache for mapping package fragments onto their folded representation.<br>
   * This cache works only under the assumption that the {@link SelectFileContentProvider} will be executed before the
   * {@link SelectFileLabelProvider}, which is usually the case.
   */
  private static Map<IPackageFragment, IPackageFragment> _cachedFoldings = Maps.newHashMap();

  /**
   * Cache for all seen package fragments. This cache improves performance significantly and provides the ability of
   * folding stubbed resources, i.e., retrieving the potentially folded parent of a tree node has to be done using this
   * cache. The cache maps {@link IPackageFragment#getElementName()} to {@link IPackageFragmentRoot#getElementName()} to
   * the cached {@link IPackageFragment}.
   */
  private static Map<String, Map<String, IPackageFragment>> _cachedPackageFragments = Maps.newHashMap();

  /**
   * Resets the internal caches. This method should only be called if the tree, which has been build is not used anymore
   */
  public static void resetCache() {

    _cachedFoldings.clear();
    _cachedPackageFragments.clear();
  }

  /**
   * Returns all package children (folded if possible)
   *
   * @param parentElement parent {@link IPackageFragmentRoot}
   * @param stubbedResources a {@link List} of stubbed resources
   * @return a list of {@link IPackageFragment} children
   * @throws JavaModelException if an internal exception occurs while accessing the eclipse jdt java model
   */
  public static List<Object> getPackageChildren(IPackageFragmentRoot parentElement, List<Object> stubbedResources)
      throws JavaModelException {

    List<Object> children = new LinkedList<>();
    for (IPackageFragment frag : retrievePackageChildren(parentElement, stubbedResources)) {
      if (isAtomicChild(null, frag, true)) {
        Object foldedPkg = fold(frag, stubbedResources);
        if (!children.contains(foldedPkg)) {
          children.add(foldedPkg);
        }
      }
    }
    return children;
  }

  /**
   * Returns all package children (folded if possible)
   *
   * @param parentElement parent {@link IPackageFragment}
   * @return a list of {@link IPackageFragment} children
   * @throws JavaModelException if an internal exception occurs while accessing the eclipse jdt java model
   */
  public static List<IPackageFragment> getPackageChildren(IPackageFragment parentElement) throws JavaModelException {

    return getPackageChildren(parentElement, Lists.newArrayList());
  }

  /**
   * Returns all package children (folded if possible)
   *
   * @param parentElement parent {@link IPackageFragment}
   * @param stubbedResources a {@link List} of stubbed resources
   * @return a list of {@link IPackageFragment} children
   * @throws JavaModelException if an internal exception occurs while accessing the eclipse jdt java model
   */
  public static List<IPackageFragment> getPackageChildren(IPackageFragment parentElement, List<Object> stubbedResources)
      throws JavaModelException {

    List<IPackageFragment> children = new LinkedList<>();
    for (IPackageFragment frag : retrievePackageChildren(parentElement, stubbedResources)) {
      if (isAtomicChild(parentElement, frag, false)) {
        children.add(fold(frag, stubbedResources));
      }
    }
    return children;
  }

  /**
   * Folds the given {@link IPackageFragment} and returns the folded non atomic child
   *
   * @param frag {@link IPackageFragment}
   * @param stubbedResources stubbed children
   * @return the folded non atomic child
   * @throws JavaModelException if one of the {@link IPackageFragment} children does not exist or if an exception occurs
   *         while accessing its corresponding resource
   */
  private static IPackageFragment fold(IPackageFragment frag, List<Object> stubbedResources) throws JavaModelException {

    if (_cachedFoldings.containsKey(frag)) {
      return _cachedFoldings.get(frag);
    }

    IPackageFragment curr = frag;
    Set<IPackageFragment> packageChildren = new HashSet<>(getPackageChildren(frag, stubbedResources));
    packageChildren.addAll(getStubbedAtomicPackageChildren(frag, stubbedResources));
    cachePackageFragment(curr);
    while (curr.getChildren().length == 0 && packageChildren.size() == 1
        && getStubbedAtomicNonPackageChildren(curr, stubbedResources).size() == 0) {
      curr = packageChildren.iterator().next();
      packageChildren.clear();
      packageChildren.addAll(getPackageChildren(curr, stubbedResources));
      packageChildren.addAll(getStubbedAtomicPackageChildren(curr, stubbedResources));
      cachePackageFragment(curr);
    }

    _cachedFoldings.put(frag, curr);
    return curr;
  }

  /**
   * Caches a package fragment into {@link #_cachedPackageFragments}
   *
   * @param curr to be cached
   */
  private static void cachePackageFragment(IPackageFragment curr) {

    if (!_cachedPackageFragments.containsKey(curr.getElementName())) {
      _cachedPackageFragments.put(curr.getElementName(), new HashMap<String, IPackageFragment>());
    }
    _cachedPackageFragments.get(curr.getElementName()).put(curr.getParent().getPath().toString(), curr);
  }

  /**
   *
   * This method calculates all atomic children from the stubbedResources and returns them
   *
   * @param parent to calculate children for
   * @param stubbedResources list of all stubbed resources
   * @return the stubbed resources, which represent atomic children for the given parent
   * @throws JavaModelException if an internal model exception occurs during folding
   */
  private static List<IPackageFragment> getStubbedAtomicPackageChildren(IPackageFragment parent,
      List<Object> stubbedResources) throws JavaModelException {

    if (stubbedResources == null) {
      return Lists.newArrayList();
    }

    List<IPackageFragment> stubbedPackages = Lists.newLinkedList();
    for (Object child : stubbedResources) {
      if (child instanceof IPackageFragment) {
        IPath childPackagePath = ((IPackageFragment) child).getPath();
        if (childPackagePath.toString().startsWith(parent.getPath().toString())
            && !childPackagePath.equals(parent.getPath())) {
          childPackagePath = childPackagePath.removeFirstSegments(parent.getPath().segmentCount());
          if (!childPackagePath.toString().contains("/")) {
            stubbedPackages.add(fold((IPackageFragment) child, stubbedResources));
          }
        }
      }
    }
    return stubbedPackages;
  }

  /**
   * This method calculates all atomic children from the stubbedResources and returns them
   *
   * @param parent to calculate children for
   * @param stubbedResources list of all stubbed resources
   * @return the stubbed resources, which represent atomic children for the given parent
   */
  private static List<Object> getStubbedAtomicNonPackageChildren(IPackageFragment parent,
      List<Object> stubbedResources) {

    if (stubbedResources == null) {
      return Lists.newArrayList();
    }

    List<Object> stubbedPackages = Lists.newLinkedList();
    for (Object child : stubbedResources) {
      IPath childPath = null;
      if (child instanceof ICompilationUnit) {
        childPath = ((ICompilationUnit) child).getPath();
      } else if (child instanceof IResource) {
        childPath = ((IResource) child).getFullPath();
      }
      if (childPath != null && childPath.toString().startsWith(parent.getPath().toString())
          && !childPath.equals(parent.getPath())) {
        childPath = childPath.removeFirstSegments(parent.getPath().segmentCount());
        if (!childPath.toString().contains("/")) {
          stubbedPackages.add(child);
        }
      }
    }
    return stubbedPackages;
  }

  /**
   * Returns the parent object (either an {@link IPackageFragment} or an {@link IPackageFragmentRoot})
   *
   * @param fragment {@link IPackageFragment} for which the parent should be retrieved
   * @return the parent object (either an {@link IPackageFragment} or an {@link IPackageFragmentRoot})
   * @throws JavaModelException if an internal exception occurs while accessing the eclipse jdt java model
   */
  private static Object getParent(IPackageFragment fragment) throws JavaModelException {

    IPackageFragmentRoot root = (IPackageFragmentRoot) fragment.getParent();
    if (isAtomicChild(null, fragment, true)) {
      return root;
    } else {
      String[] fragNameFragments = fragment.getElementName().split("\\.");
      for (int i = fragNameFragments.length - 1; i > 0; i--) {
        StringBuilder packageBuilder = new StringBuilder();
        for (int j = 0; j < i; j++) {
          packageBuilder.append(fragNameFragments[j]);
          packageBuilder.append(".");
        }
        if (packageBuilder.length() > 0) {
          packageBuilder.deleteCharAt(packageBuilder.length() - 1);
        }

        // assumption: all packages have been seen during first phase providing the contents. This
        // method will
        // be called while providing labels and thus all package fragments should be cached
        // beforehand.
        IPackageFragment parentPackage = _cachedPackageFragments.get(packageBuilder.toString())
            .get(root.getPath().toString());

        // rely on folding mapping cache (pass null parameter)
        if (parentPackage.equals(fold(parentPackage, null))) {
          return parentPackage;
        }
      }
      // else return source folder as a last option
      return root;
    }
  }

  /**
   * Returns the child name for the given {@link IPackageFragment}
   *
   * @param fragment {@link IPackageFragment}
   * @return the child name in a hierarchical manner
   * @throws JavaModelException if an internal exception occurs while accessing the eclipse jdt java model
   */
  public static String getChildName(IPackageFragment fragment) throws JavaModelException {

    Object parent = getParent(fragment);
    if (parent instanceof IPackageFragmentRoot) {
      return fragment.getElementName();
    } else if (parent instanceof IPackageFragment) {
      String parentName = ((IPackageFragment) parent).getElementName();
      String childName = fragment.getElementName();
      String fullChildName = childName.substring(parentName.length() + 1);
      while (parent instanceof IPackageFragment && isFolded((IPackageFragment) parent)) {
        parentName = ((IPackageFragment) parent).getElementName();
        childName = fragment.getElementName();
        fullChildName = childName.substring(parentName.length() + 1);
        parent = getParent((IPackageFragment) parent);
      }
      if (parent instanceof IPackageFragmentRoot) {
        fullChildName = childName;
      }
      return fullChildName;
    }
    return "INVALID";
  }

  /**
   * Checks whether the given {@link IPackageFragment} is folded
   *
   * @param parent {@link IPackageFragment}
   * @return <code>true</code> if the given {@link IPackageFragment} is folded<br>
   *         <code>false</code> otherwise
   */
  private static boolean isFolded(IPackageFragment parent) {

    return !parent.equals(_cachedFoldings.get(parent));
  }

  /**
   * Returns all {@link IPackageFragment} children of the given {@link IPackageFragmentRoot}
   *
   * @param parentElement {@link IPackageFragmentRoot}
   * @param stubbedResources stubbed resources
   * @return all {@link IPackageFragment} children of the given {@link IPackageFragmentRoot}
   * @throws JavaModelException if the parentElement does not exist or if an exception occurs while accessing its
   *         corresponding resource
   */
  public static List<IPackageFragment> retrievePackageChildren(IPackageFragmentRoot parentElement,
      List<Object> stubbedResources) throws JavaModelException {

    List<IPackageFragment> packageChildren = new LinkedList<>();
    for (IJavaElement child : parentElement.getChildren()) {
      if (child instanceof IPackageFragment) {
        packageChildren.add((IPackageFragment) child);
      }
    }
    if (stubbedResources != null) {
      for (Object stubbedResource : stubbedResources) {
        if (stubbedResource instanceof IPackageFragment && ((IPackageFragment) stubbedResource).getPath().toString()
            .startsWith(parentElement.getPath().toString())) {
          packageChildren.add((IPackageFragment) stubbedResource);
        }
      }
    }
    return packageChildren;
  }

  /**
   * Returns all {@link IPackageFragment} children of the given {@link IPackageFragment}
   *
   * @param parentElement {@link IPackageFragment}
   * @param stubbedResources stubbed resources
   * @return all {@link IPackageFragment} children of the given {@link IPackageFragment}
   * @throws JavaModelException if the parentElement does not exist or if an exception occurs while accessing its
   *         corresponding resource
   */
  private static List<IPackageFragment> retrievePackageChildren(IPackageFragment parentElement,
      List<Object> stubbedResources) throws JavaModelException {

    return retrievePackageChildren((IPackageFragmentRoot) parentElement.getParent(), stubbedResources);
  }

  /**
   * Checks whether the the given {@link IPackageFragment} child is an atomic child of the given
   * {@link IPackageFragment} parent. Atomic means only one further defined package beyond the given parent.
   *
   * @param parent {@link IPackageFragment}
   * @param child {@link IPackageFragment}
   * @param considerDefaultPackage states whether the default package should be considered or not
   * @return <code>true</code> if the child defines exactly one package beyond the given parent package<br>
   *         <code>false</code> otherwise
   */
  private static boolean isAtomicChild(IPackageFragment parent, IPackageFragment child,
      boolean considerDefaultPackage) {

    String parentName;
    if (parent == null) {
      parentName = "";
    } else {
      parentName = parent.getElementName();
    }
    String childName = child.getElementName();
    if (childName.startsWith(parentName)) {
      if (parentName.length() < childName.length()) {
        String childTail = child.getElementName().substring(parentName.length() + 1);
        return childTail.indexOf(".") == -1;
      } else if (considerDefaultPackage && child.isDefaultPackage()) {
        return true;
      }
    }
    return false;
  }
}

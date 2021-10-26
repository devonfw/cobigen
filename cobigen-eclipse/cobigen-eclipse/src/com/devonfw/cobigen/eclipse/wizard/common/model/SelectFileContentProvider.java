package com.devonfw.cobigen.eclipse.wizard.common.model;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.eclipse.common.exceptions.CobiGenEclipseRuntimeException;
import com.devonfw.cobigen.eclipse.common.tools.PathUtil;
import com.devonfw.cobigen.eclipse.wizard.common.model.stubs.ICompilationUnitStub;
import com.devonfw.cobigen.eclipse.wizard.common.model.stubs.IFileStub;
import com.devonfw.cobigen.eclipse.wizard.common.model.stubs.IFolderStub;
import com.devonfw.cobigen.eclipse.wizard.common.model.stubs.IJavaElementStub;
import com.devonfw.cobigen.eclipse.wizard.common.model.stubs.IPackageFragmentStub;
import com.devonfw.cobigen.eclipse.wizard.common.model.stubs.IResourceStub;
import com.devonfw.cobigen.eclipse.wizard.common.model.stubs.OffWorkspaceResourceTreeNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Content provider for the export tree viewer used in the export wizard
 */
public class SelectFileContentProvider implements ITreeContentProvider {

  /**
   * Assigning logger to SelectFileContentProvider
   */
  private static final Logger LOG = LoggerFactory.getLogger(SelectFileContentProvider.class);

  /**
   * The paths which are affected by the upcoming generation process
   */
  private Set<String> filteredPaths = new HashSet<>();

  /**
   * Cached {@link IPackageFragmentRoot}s of the top project tree members. Mapping name of the project to its cached
   * package fragment roots.
   */
  private Map<String, IPackageFragmentRoot[]> _cachedPackageFragmentRoots = Maps.newHashMap();

  /**
   * Cached children for each project until the filter is reset.
   */
  private Map<String, Object[]> _cachedChildren = Maps.newHashMap();

  /**
   * Cached already provided resources (including stubs) for performance improvements & reverse lookup (mapping from
   * project to path to resources)
   */
  private Map<String, Object> _cachedProvidedResources = Maps.newHashMap();

  /** Current resource tree for workspace external files */
  private List<OffWorkspaceResourceTreeNode> offScopeResourceTree;

  /**
   * Filters the {@link TreeViewer} contents by the given paths
   *
   * @param paths to be filtered
   * @param offScopeResourceTree resource tree for workspace external files
   */
  public void filter(Set<String> paths, List<OffWorkspaceResourceTreeNode> offScopeResourceTree) {

    this.filteredPaths = new HashSet<>(paths);
    this._cachedChildren.clear();
    this._cachedPackageFragmentRoots.clear();
    HierarchicalTreeOperator.resetCache();
    this.offScopeResourceTree = offScopeResourceTree;
    this._cachedProvidedResources.clear();

    Deque<OffWorkspaceResourceTreeNode> worklist = Lists.newLinkedList(offScopeResourceTree);
    while (!worklist.isEmpty()) {
      OffWorkspaceResourceTreeNode next = worklist.pop();
      if (next.hasChildren()) {
        worklist.addAll(next.getChildren());
      } else {
        this._cachedProvidedResources.put(next.getAbsolutePathStr(), next);
      }
    }
  }

  @Override
  public Object[] getElements(Object inputElement) {

    List<Object> result = new LinkedList<>();
    if (inputElement instanceof IProject[]) {
      for (IProject proj : (IProject[]) inputElement) {
        if (isElementToBeShown(proj.getFullPath())) {

          // hack due to bug 9262 (see javadoc of getElements)
          IJavaProject jProj = JavaCore.create(proj);
          if (jProj != null && jProj.exists()) {
            try {
              this._cachedPackageFragmentRoots.put(jProj.getElementName(), jProj.getPackageFragmentRoots());
            } catch (JavaModelException e) {
              // Ignore (only usability issue)
              LOG.error(
                  "An internal java model exception occured while retrieving the package fragment roots for project '{}'.",
                  jProj.getElementName(), e);
            }
            result.add(jProj);
          } else {
            result.add(proj);
          }
        }
      }
    }
    if (this.offScopeResourceTree != null) {
      result.addAll(this.offScopeResourceTree);
    }
    return result.toArray();
  }

  @Override
  public Object[] getChildren(Object parentElement) {

    if (parentElement instanceof IContainer) {

      // check cache
      String key = ((IContainer) parentElement).getFullPath().toString();
      if (this._cachedChildren.containsKey(key)) {
        return this._cachedChildren.get(key);
      }

      try {
        Set<Object> affectedChildren = new HashSet<>(
            Arrays.asList(getAffectedChildren(getNoneDuplicateResourceChildren((IContainer) parentElement, null))));

        // Add all non existent but targeting resources using Mocks
        affectedChildren.addAll(stubNonExistentChildren(parentElement, true));
        this._cachedChildren.put(((IContainer) parentElement).getFullPath().toString(), affectedChildren.toArray());
        return affectedChildren.toArray();
      } catch (CoreException e) {
        LOG.error("An eclipse internal exceptions occurs while fetching the children of {}.",
            ((IContainer) parentElement).getName(), e);
      }

    } else if (parentElement instanceof IParent && parentElement instanceof IJavaElement) {
      // check cache
      String key = ((IJavaElement) parentElement).getPath().toString();
      if (this._cachedChildren.containsKey(key)) {
        return this._cachedChildren.get(key);
      }

      try {
        List<Object> children = new ArrayList<>();
        if (parentElement instanceof IPackageFragmentRoot) {
          List<Object> stubbedChildren = stubNonExistentChildren(parentElement, true);
          children = HierarchicalTreeOperator.getPackageChildren((IPackageFragmentRoot) parentElement, stubbedChildren);
        } else if (parentElement instanceof IPackageFragment) {
          List<Object> stubbedChildren = stubNonExistentChildren(parentElement, true);
          if (!((IPackageFragment) parentElement).isDefaultPackage()) {
            children.clear();
            // add package children
            children
                .addAll(HierarchicalTreeOperator.getPackageChildren((IPackageFragment) parentElement, stubbedChildren));
            // add non-package children
            for (Object stub : stubbedChildren) {
              if (stub instanceof ICompilationUnitStub) {
                children.add(stub);
              }
            }
          }
        } else if (parentElement instanceof IParent && !(parentElement instanceof ICompilationUnit)) {
          List<Object> stubbedChildren = stubNonExistentChildren(parentElement, false);
          IJavaElement[] jChildren = ((IParent) parentElement).getChildren();
          children = new ArrayList<Object>(Arrays.asList(jChildren));
          // add non-package children
          for (Object stub : stubbedChildren) {
            children.add(stub);
          }
        }
        if (!(parentElement instanceof ICompilationUnit)) {
          List<Object> stubbedChildren = stubNonExistentChildren(parentElement, false);
          children.addAll(getNonPackageChildren((IParent) parentElement, stubbedChildren));
        }

        Object[] affectedChildren = getAffectedChildren(children);
        if (parentElement instanceof IPackageFragmentRoot) {
          List<Object> affectedChildrenList = Lists.newArrayList(affectedChildren);
          for (Object o : affectedChildren) {
            if (o instanceof IJavaElement) {
              if (((IJavaElement) o).getElementName().equals("")) {
                if (getChildren(o).length == 0) {
                  affectedChildrenList.remove(o);
                }
              }
            }
          }
          affectedChildren = affectedChildrenList.toArray();
        }

        Set<Object> affectedChildrenList = new HashSet<>(Arrays.asList(affectedChildren));
        this._cachedChildren.put(((IJavaElement) parentElement).getPath().toString(), affectedChildrenList.toArray());
        return affectedChildrenList.toArray();
      } catch (CoreException e) {
        throw new CobiGenEclipseRuntimeException("An eclipse internal exceptions occurs while fetching the children of "
            + ((IJavaElement) parentElement).getElementName() + ".", e);
      }
    } else if (parentElement instanceof OffWorkspaceResourceTreeNode) {
      return ((OffWorkspaceResourceTreeNode) parentElement).getChildren().toArray();
    }

    return new Object[0];
  }

  /**
   * Stubs all non existent Packages, which are selected to be generated and returns all stubbed packages
   *
   * @param parentElement parent {@link IJavaElement} to retrieve the children from
   * @param considerPackages states whether packages should be considered when retrieving the children. This also
   *        includes recursively retrieving children of packages
   * @return List of {@link IPackageFragment}s, which will be stubbed
   * @throws JavaModelException if an internal exception occurs while accessing the eclipse jdt java model
   */
  private List<Object> stubNonExistentChildren(Object parentElement, boolean considerPackages)
      throws JavaModelException {

    // Default package handling
    if (parentElement instanceof IPackageFragment && ((IPackageFragment) parentElement).isDefaultPackage()) {
      return Lists.newArrayList();// a default package cannot have packages as children
    }

    List<Object> stubbedChildren = new LinkedList<>();
    String debugInfo = null;
    if (parentElement instanceof IJavaElement) {

      List<String> nonExistentChildren = getNonExistentChildren(((IJavaElement) parentElement).getPath());
      for (String path : nonExistentChildren) {

        // check inclusion and exclusion patterns to stub the correct elements
        if (!JavaClasspathUtil.isCompiledSource((IJavaElement) parentElement, path)) {
          stubNonExistentChildren(((IJavaElement) parentElement).getCorrespondingResource(), stubbedChildren);
          continue;
        }

        IPath elementpath = new Path(path);

        IJavaElementStub javaElementStub;
        if (targetIsFile(elementpath, nonExistentChildren)) {

          // If the file is not a direct child of the parent, we will skip it
          IPath p = elementpath.removeFirstSegments(((IJavaElement) parentElement).getPath().segmentCount());
          if (p.segmentCount() != 1) {
            continue;
          } else if (this._cachedProvidedResources.containsKey(path)
              && !stubbedChildren.contains(this._cachedProvidedResources.get(path))) {
            // if already seen, just get it and skip creation
            stubbedChildren.add(this._cachedProvidedResources.get(path));
            continue;
          }

          // Create CompilationUnit Stub
          javaElementStub = new ICompilationUnitStub();
          javaElementStub.setElementType(IJavaElement.COMPILATION_UNIT);
          debugInfo = "COMPILATION_UNIT";
          javaElementStub.setElementName(elementpath.lastSegment());
          javaElementStub.setPath(elementpath);
        } else {
          // If path is not within an existing package fragment root, we cannot create packages for
          // it
          if (!isDefinedInSourceFolder(path) || !considerPackages) {
            continue;
          } else if (this._cachedProvidedResources.containsKey(path)
              && !stubbedChildren.contains(this._cachedProvidedResources.get(path))) {
            // if already seen, just get it and skip creation
            stubbedChildren.add(this._cachedProvidedResources.get(path));
            continue;
          }

          // Create PackageFragment Stub
          javaElementStub = new IPackageFragmentStub();
          javaElementStub.setElementType(IJavaElement.PACKAGE_FRAGMENT);
          debugInfo = "PACKAGE_FRAGMENT";
          ((IPackageFragmentStub) javaElementStub).setNonJavaResources(new Object[0]);

          javaElementStub.setPath(elementpath);

          IJavaElement parent = determineJavaModelParent((IJavaElement) parentElement, javaElementStub);
          IPath packagePath = elementpath.removeFirstSegments(parent.getPath().segmentCount());
          packagePath.removeTrailingSeparator();
          String elementName = packagePath.toString().replaceAll("/", ".");
          javaElementStub.setElementName(elementName);
        }

        if (((IJavaElement) parentElement) instanceof IPackageFragment) {
          javaElementStub.setParent(((IJavaElement) parentElement).getParent());
        } else {
          javaElementStub.setParent(determineJavaModelParent((IJavaElement) parentElement, javaElementStub));
        }

        IJavaElement[] javaChildren = new IJavaElement[0];
        if (considerPackages) {
          Object[] childrenArr = stubNonExistentChildren(javaElementStub, false).toArray();
          javaChildren = Arrays.copyOf(childrenArr, childrenArr.length, IJavaElement[].class);
        }
        javaElementStub.setChildren(javaChildren);

        if (!stubbedChildren.contains(javaElementStub)) {
          stubbedChildren.add(javaElementStub);
          this._cachedProvidedResources.put(javaElementStub.getPath().toString(), javaElementStub);
          LOG.debug("Stub created for {} with element name '{}' and path '{}'", debugInfo,
              javaElementStub.getElementName(), javaElementStub.getPath().toString());
        }
      }

      // for IPackageFragments as parents, also retrieve all stubbed children for each package to not
      // forget any stubbed file #151/#137
      if (parentElement instanceof IPackageFragmentRoot) {
        List<IPackageFragment> allExistingPackageFragments = HierarchicalTreeOperator
            .retrievePackageChildren((IPackageFragmentRoot) parentElement, Lists.newArrayListWithCapacity(0));
        for (IPackageFragment frag : allExistingPackageFragments) {
          List<Object> recStubbedChildren = stubNonExistentChildren(frag, considerPackages);
          for (Object c : recStubbedChildren) {
            if (!stubbedChildren.contains(c)) {
              stubbedChildren.add(c);
            }
          }
        }
      }
    } else if (parentElement instanceof IResource) {
      stubNonExistentChildren((IResource) parentElement, stubbedChildren);
    }

    return stubbedChildren;
  }

  /**
   * Stubs all non existent resources, which are selected to be generated
   *
   * @param parentElement parent {@link IJavaElement} to retrieve the children from
   * @param stubbedChildren the so far stubbed resources and similarly the output of this method as new stubbed
   *        resources will be added to this list. This is necessary in order to avoid duplicates in this list.
   */
  private void stubNonExistentChildren(IResource parentElement, List<Object> stubbedChildren) {

    String debugInfo;

    IPath parentPath = parentElement.getFullPath();
    List<String> nonExistentChildren = getNonExistentChildren(parentPath);
    for (String path : nonExistentChildren) {

      IResourceStub resourceStub = null;
      IPath childPath = new Path(path);
      IPath childPathFragment = childPath.removeFirstSegments(parentPath.segmentCount());

      if (childPathFragment.segmentCount() > 1) {
        // target path is no atomic child -> stub next element if necessary
        childPathFragment = childPathFragment.removeFirstSegments(1);
        IPath atomicChildPath = new Path(path);
        atomicChildPath = atomicChildPath.removeLastSegments(childPathFragment.segmentCount());

        // If resource already exists, we will continue as we will be called later again with this
        // folder as
        // parent
        if (ResourcesPlugin.getWorkspace().getRoot().exists(atomicChildPath)) {
          continue;
        } else if (this._cachedProvidedResources.containsKey(atomicChildPath.toString())) {
          // if already seen, just get it and skip creation
          Object cachedStub = this._cachedProvidedResources.get(atomicChildPath.toString());
          if (!stubbedChildren.contains(cachedStub)) {
            stubbedChildren.add(cachedStub);
          }
          continue;
        }

        if (targetIsFile(atomicChildPath, nonExistentChildren)) {
          resourceStub = new IFileStub();
          debugInfo = "File";
        } else {
          resourceStub = new IFolderStub();
          debugInfo = "Folder";
        }
        resourceStub.setFullPath(atomicChildPath);
      } else if (childPathFragment.segmentCount() == 1) {

        if (this._cachedProvidedResources.containsKey(childPath.toString())) {
          // if already seen, just get it and skip creation
          Object cachedStub = this._cachedProvidedResources.get(childPath.toString());
          if (!stubbedChildren.contains(cachedStub)) {
            stubbedChildren.add(cachedStub);
          }
          continue;
        }

        if (targetIsFile(childPath, nonExistentChildren)) {
          resourceStub = new IFileStub();
          debugInfo = "File";
        } else {
          resourceStub = new IFolderStub();
          debugInfo = "Folder";
        }
        resourceStub.setFullPath(childPath);
      } else {
        continue; // no child of parentPath
      }

      if (!stubbedChildren.contains(resourceStub)) {
        stubbedChildren.add(resourceStub);
        this._cachedProvidedResources.put(resourceStub.getFullPath().toString(), resourceStub);
      }
      LOG.debug("Stub created for {} with name '{}' and path '{}'", debugInfo, resourceStub.getName(),
          resourceStub.getFullPath().toString());
    }
  }

  /**
   * Retrieves the stub for the given path from the current cache state.
   *
   * @param path workspace dependent path as used for every eclipse resource
   * @return the stub object for the path or <code>null</code> if no stub has been created for this path
   */
  public Object getProvidedObject(String path) {

    return this._cachedProvidedResources.get(path);
  }

  /**
   * Checks whether the target of the given path should be interpreted as file.<br>
   * <i>This may be not the best idea to determine the difference between a file and a package path, but for now there
   * is no better way.</i>
   *
   * @param path to target
   * @param paths paths to all files and containing folders which aren't generated
   * @return <code>true</code> if the path isn't contained by another path<br>
   *         <code>false</code>, otherwise
   */
  private boolean targetIsFile(IPath path, List<String> paths) {

    int index = paths.indexOf(path.toString());
    if (index + 1 <= paths.size() - 1) {
      if (paths.get(index + 1).contains(path.toString())) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determines the correct parent for a stubbed {@link IJavaElement} according to the eclipse java model for a given
   * child and its parent in the tree.
   *
   * @param treeParent parent of the child in the displayed tree
   * @param child {@link IJavaElementStub} to determine the correct java model parent for
   * @return the java model parent
   */
  private IJavaElement determineJavaModelParent(IJavaElement treeParent, IJavaElementStub child) {

    if (child instanceof IPackageFragmentStub) {
      // parent should be the source folder
      if (treeParent instanceof IJavaProject) {
        // cache is correctly initialized and filled because of the invariant, that getElements will
        // be called
        // before getChildren
        for (IPackageFragmentRoot root : this._cachedPackageFragmentRoots.get(treeParent)) {
          if (!root.isReadOnly() && child.getPath().toString().startsWith(root.getPath().toString())) {
            return root;
          }
        }
      } else if (treeParent instanceof IPackageFragmentRoot) {
        return treeParent;
      } else if (treeParent instanceof IPackageFragment) {
        // invariant: we only consider children of the parent, so the child has to be in the same
        // source
        // folder as the parent
        return treeParent.getParent();
      }
    } else if (child instanceof ICompilationUnitStub) {
      // invariant: we only consider children of the parent, so the compilation unit has to be right
      // within the
      // parent, which has to be a package fragment
      return treeParent;
    }
    LOG.error("Unhandled exceptional case! Needs further investigation - Parent: {} Child: {}",
        treeParent.getClass().getCanonicalName(), child.getClass().getCanonicalName());
    throw new IllegalArgumentException("Unhandled exceptional case! Needs further investigation.");
  }

  /**
   * Calculates all non existent paths from the {@link #filteredPaths}. For one path /a/b/c in {@link #filteredPaths}
   * this method will return /a if not existent, /a/b if not existent /a/b/c if not existent.
   *
   * @param parentPath parent path, which will be included in all resulting non existent child paths
   * @return the list of paths for all non existent but addressed children
   */
  private List<String> getNonExistentChildren(IPath parentPath) {

    List<String> paths = new ArrayList<>();
    for (String fp : this.filteredPaths) {
      IPath filteredPath = new Path(fp);
      if (parentPath.isPrefixOf(filteredPath)) {
        filteredPath = filteredPath.removeFirstSegments(parentPath.segmentCount());
        IPath newChildPath = new Path(parentPath.toString());
        int filteredPathLength = filteredPath.segmentCount();
        for (int i = 0; i < filteredPathLength; i++) {
          newChildPath = newChildPath.addTrailingSeparator().append(filteredPath.segment(0));
          filteredPath = filteredPath.removeFirstSegments(1);
          if (!ResourcesPlugin.getWorkspace().getRoot().exists(newChildPath)
              && !paths.contains(newChildPath.toString())) {
            paths.add(newChildPath.toString());
          }
        }
      }
    }
    return paths;
  }

  @Override
  public Object getParent(Object element) {

    return null;
  }

  @Override
  public boolean hasChildren(Object element) {

    return getChildren(element).length > 0;
  }

  @Override
  public void dispose() {

  }

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    // will not occur / not needed
  }

  /**
   * Retrieves all non package children
   *
   * @param parent the children should be retrieved for
   * @param stubbedResources the list of already stubbed ressources
   * @throws CoreException if an internal eclipse exception occurs
   * @return an unordered set of all non package children from the parent
   */
  private Set<Object> getNonPackageChildren(IParent parent, List<Object> stubbedResources) throws CoreException {

    Set<Object> children = new HashSet<>();
    for (IJavaElement c : parent.getChildren()) {
      if (!(c instanceof IPackageFragment) && !(c instanceof IPackageFragmentRoot)) {
        children.add(c);
      }
    }
    if (parent instanceof IJavaElement) {
      IResource r = ((IJavaElement) parent).getResource();
      if (r != null && r instanceof IContainer) {
        children.addAll(getNoneDuplicateResourceChildren((IContainer) r, stubbedResources));
      }
    }
    return children;
  }

  /**
   * Returns all resource children of the given parent {@link IResource} if and only if it has no corresponding
   * {@link IJavaElement} determined by {@link JavaCore#create(org.eclipse.core.resources.IResource)}
   *
   * @param parent {@link IResource} the children should be determined
   * @param stubbedResources the list of already stubbed resources
   * @throws CoreException if an internal eclipse exception occurs
   * @return the list of resource children, which are not already found in the java model.
   */
  private List<Object> getNoneDuplicateResourceChildren(IContainer parent, List<Object> stubbedResources)
      throws CoreException {

    List<Object> children = new ArrayList<>();
    IResource[] resources = parent.members();
    for (IResource child : resources) {
      IJavaElement jChild = JavaCore.create(child);
      // only add child of type IResource if it has not java representation
      // in this case it should have been added before
      if (jChild == null && (!isPartOfAnySourceFolder(child.getFullPath().toString())
          || isPartOfAnyNonSourceFolderPath(child.getFullPath().toString()))) {
        children.add(child);
      }
    }

    if (stubbedResources != null) {
      for (Object stubbedResource : stubbedResources) {
        if (stubbedResource instanceof IResource
            && ((IResource) stubbedResource).getFullPath().toString().startsWith(parent.getFullPath().toString())) {
          children.add(stubbedResource);
        }
      }
    }
    return children;
  }

  /**
   * @param children a list of children which should be filtered
   * @return all children affected by the generation process
   */
  private Object[] getAffectedChildren(List<Object> children) {

    List<Object> affectedChildren = new ArrayList<>();
    for (Object e : children) {
      if (e instanceof IJavaElement && isElementToBeShown(((IJavaElement) e).getPath())) {
        affectedChildren.add(e);
        this._cachedProvidedResources.put(((IJavaElement) e).getPath().toString(), e);
      } else if (e instanceof IResource && isElementToBeShown(((IResource) e).getFullPath())) {
        affectedChildren.add(e);
        this._cachedProvidedResources.put(((IResource) e).getFullPath().toString(), e);
      }
    }
    return affectedChildren.toArray();
  }

  /**
   * Checks whether the given path is affected by the generation
   *
   * @param fullPath path of the current element
   * @return true, if the path is affected by any template generation<br>
   *         false, otherwise
   */
  private boolean isElementToBeShown(IPath fullPath) {

    for (String s : this.filteredPaths) {
      if (Paths.get(s).startsWith(Paths.get(fullPath.toString()))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether the given path is defined within an {@link IPackageFragmentRoot} or is part of one
   *
   * @param path to be checked
   * @return <code>true</code> if the path starts with the path of any {@link IPackageFragmentRoot} or is contained in
   *         it<br>
   *         <code>false</code> otherwise
   */
  private boolean isDefinedInSourceFolder(String path) {

    String projectName = PathUtil.getProject(path);
    if (this._cachedPackageFragmentRoots.containsKey(projectName)) {
      for (IPackageFragmentRoot root : this._cachedPackageFragmentRoots.get(projectName)) {
        if (!root.isReadOnly() && path.startsWith(root.getPath().toString())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Checks whether the given path is part of or equal to a path defined by any package fragment root, resp. source
   * folder.
   *
   * @param path to be checked
   * @return <code>true</code> if the given path is part of any source folder path<br>
   *         <code>false</code>, otherwise
   */
  private boolean isPartOfAnySourceFolder(String path) {

    String projectName = PathUtil.getProject(path);
    if (this._cachedPackageFragmentRoots.containsKey(projectName)) {
      for (IPackageFragmentRoot root : this._cachedPackageFragmentRoots.get(projectName)) {
        if (!root.isReadOnly() && root.getPath().toString().startsWith(path)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Checks whether the given path is part of any filtered path which is not defined within a source folder
   *
   * @param path to be checked
   * @return <code>true</code> if the given path is not defined within any source folder
   */
  private boolean isPartOfAnyNonSourceFolderPath(String path) {

    for (String resourcePath : getNonJavaResourcePaths()) {
      if (resourcePath.startsWith(path)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns all filtered paths which do not contain any jdt containers, resp., which are not dependent on any jdt
   * {@link IPackageFragmentRoot}
   *
   * @return all filtered paths which do not contain any jdt containers
   */
  private Set<String> getNonJavaResourcePaths() {

    Set<String> nonJavaPaths = new HashSet<>();
    for (String path : this.filteredPaths) {
      if (!isDefinedInSourceFolder(path)) {
        nonJavaPaths.add(path);
      }
    }
    return nonJavaPaths;
  }

}

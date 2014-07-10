/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.wizard.common.model;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.ICompilationUnitStub;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.IFileStub;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.IFolderStub;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.IJavaElementStub;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.IPackageFragmentStub;
import com.capgemini.cobigen.eclipse.wizard.common.model.stubs.IResourceStub;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Content provider for the export tree viewer used in the export wizard
 */
public class SelectFileContentProvider implements ITreeContentProvider {

    /**
     * The paths which are affected by the upcoming generation process
     */
    private Set<String> filteredPaths = new HashSet<String>();

    /**
     * Cached {@link IPackageFragmentRoot}s of the top project tree members
     */
    private IPackageFragmentRoot[] _cachePackageFragmentRoots;

    /**
     * Cached Children until the filter is reset
     */
    private Map<String, Object[]> _cacheChildren = Maps.newHashMap();

    /**
     * Assigning logger to SelectFileContentProvider
     */
    private static final Logger LOG = LoggerFactory.getLogger(SelectFileContentProvider.class);

    /**
     * Filters the {@link TreeViewer} contents by the given paths
     * @param paths
     * @author mbrunnli (14.02.2013)
     */
    public void filter(Set<String> paths) {
        filteredPaths = new HashSet<String>(paths);
        _cacheChildren.clear();
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (13.02.2013)
     */
    @Override
    public Object[] getElements(Object inputElement) {
        Object[] result = new Object[0];
        if (inputElement instanceof IProject[]) {
            // hack due to bug 9262 (see javadoc of getElements)
            IJavaProject jProj = JavaCore.create(((IProject[]) inputElement)[0]);
            if (jProj != null) {
                try {
                    _cachePackageFragmentRoots = jProj.getPackageFragmentRoots();
                } catch (JavaModelException e) {
                    // Ignore (only usablility issue)
                    LOG.error(
                        "An internal java model exception occured while retrieving the package fragment roots for project '{}'.",
                        jProj.getElementName(), e);
                }
                result = new IJavaProject[] { jProj };
            } else {
                result = new IProject[] { ((IProject[]) inputElement)[0] };
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (13.02.2013)
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IContainer) {

            // check cache
            String key = ((IContainer) parentElement).getFullPath().toString();
            if (_cacheChildren.containsKey(key)) {
                return _cacheChildren.get(key);
            }

            try {
                Set<Object> affectedChildren =
                    new HashSet<Object>(
                        Arrays
                            .asList(getAffectedChildren(getNoneDuplicateResourceChildren((IContainer) parentElement))));

                // Add all non existent but targeting resources using Mocks
                affectedChildren.addAll(stubNonExistentChildren(parentElement, true));
                _cacheChildren.put(((IContainer) parentElement).getFullPath().toString(),
                    affectedChildren.toArray());
                return affectedChildren.toArray();
            } catch (CoreException e) {
                LOG.error("An eclipse internal exceptions occurs while fetching the children of {}.",
                    ((IContainer) parentElement).getName(), e);
            }
        } else if (parentElement instanceof IParent && parentElement instanceof IJavaElement) {

            // check cache
            String key = ((IJavaElement) parentElement).getPath().toString();
            if (_cacheChildren.containsKey(key)) {
                return _cacheChildren.get(key);
            }

            try {
                List<Object> children = new ArrayList<Object>();
                if (parentElement instanceof IPackageFragmentRoot) {
                    children =
                        HierarchicalTreeOperator.getPackageChildren((IPackageFragmentRoot) parentElement);
                } else if (parentElement instanceof IPackageFragment) {
                    if (!((IPackageFragment) parentElement).isDefaultPackage()) {
                        children.clear();
                        children.addAll(HierarchicalTreeOperator
                            .getPackageChildren((IPackageFragment) parentElement));
                    }
                } else if (parentElement instanceof IParent && !(parentElement instanceof ICompilationUnit)) {
                    IJavaElement[] jChildren = ((IParent) parentElement).getChildren();
                    children = new ArrayList<Object>(Arrays.asList(jChildren));
                }
                if (!(parentElement instanceof ICompilationUnit)) {
                    children.addAll(getNonPackageChildren((IParent) parentElement));
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

                Set<Object> affectedChildrenList = new HashSet<Object>(Arrays.asList(affectedChildren));
                // Add all non existent but targeting resources using Mocks
                affectedChildrenList.addAll(stubNonExistentChildren((IJavaElement) parentElement, true));

                _cacheChildren.put(((IJavaElement) parentElement).getPath().toString(),
                    affectedChildrenList.toArray());
                return affectedChildrenList.toArray();
            } catch (CoreException e) {
                LOG.error("An eclipse internal exceptions occurs while fetching the children of {}.",
                    ((IJavaElement) parentElement).getElementName(), e);
            }
        }

        return new Object[0];
    }

    /**
     * Mocks all non existent Packages, which are selected to be generated and returns all mocked packages
     * @param parentElement
     *            parent {@link IJavaElement} to retrieve the children from
     * @param evaluateChildren
     *            states whether the children should be retrieved recursively
     * @return List of {@link IPackageFragment}s, which will be mocked
     * @throws JavaModelException
     * @author mbrunnli (01.04.2014)
     */
    private List<Object> stubNonExistentChildren(Object parentElement, boolean evaluateChildren)
        throws JavaModelException {
        List<Object> stubbedChildren = new LinkedList<Object>();
        if (parentElement instanceof IJavaElement) {
            for (String path : getNonExistentChildren(((IJavaElement) parentElement).getPath())) {

                String elementName = path.substring(path.lastIndexOf("/") + 1);
                IPath elementpath =
                    ((IJavaElement) parentElement).getPath().addTrailingSeparator().append(elementName);

                IJavaElementStub javaElementStub;
                if (elementName.contains(".")) {
                    javaElementStub = new ICompilationUnitStub();
                    javaElementStub.setElementType(IJavaElement.COMPILATION_UNIT);
                } else {
                    if (parentElement instanceof IPackageFragment
                        && ((IPackageFragment) parentElement).isDefaultPackage()) {
                        continue; // a default package cannot have packages as children
                    }
                    javaElementStub = new IPackageFragmentStub();
                    javaElementStub.setElementType(IJavaElement.PACKAGE_FRAGMENT);
                    ((IPackageFragmentStub) javaElementStub).setNonJavaResources(new Object[0]); // TODO check
                                                                                                 // simulated
                }
                javaElementStub.setPath(elementpath);

                IJavaElement[] javaChildren = new IJavaElement[0];
                if (evaluateChildren) {
                    Object[] childrenArr = stubNonExistentChildren(javaElementStub, false).toArray();

                    javaChildren = Arrays.copyOf(childrenArr, childrenArr.length, IJavaElement[].class);
                }

                javaElementStub.setElementName(elementName);
                javaElementStub.setChildren(javaChildren);
                if (((IJavaElement) parentElement) instanceof IPackageFragment) {
                    javaElementStub.setParent(((IJavaElement) parentElement).getParent());
                } else {
                    javaElementStub.setParent((IJavaElement) parentElement);
                }

                stubbedChildren.add(javaElementStub);
            }
        } else if (parentElement instanceof IResource) {
            for (String path : getNonExistentChildren(((IResource) parentElement).getFullPath())) {
                List<String> nonExistentChildren = getNonExistentChildren(new Path(path));
                IResourceStub resourceStub;
                if (nonExistentChildren.size() > 0) {
                    resourceStub = new IFolderStub();
                } else {
                    resourceStub = new IFileStub();
                }

                String elementName = path.substring(path.lastIndexOf("/") + 1);
                resourceStub.setFullPath(((IResource) parentElement).getFullPath().addTrailingSeparator()
                    .append(elementName));
                resourceStub.setName(elementName);

                stubbedChildren.add(resourceStub);
            }
        }
        return stubbedChildren;
    }

    /**
     * @param parentPath
     * @return the list of paths for all non existent but addressed children
     * @author mbrunnli (01.04.2014)
     */
    private List<String> getNonExistentChildren(IPath parentPath) {
        List<String> paths = new LinkedList<String>();
        for (String fp : filteredPaths) {
            IPath filteredPath = new Path(fp);
            if (parentPath.isPrefixOf(filteredPath)) {
                filteredPath = filteredPath.removeFirstSegments(parentPath.segmentCount());
                if (filteredPath.segmentCount() > 0) {
                    IPath newChildPath = new Path(parentPath.toString());
                    newChildPath = newChildPath.addTrailingSeparator().append(filteredPath.segment(0));
                    if (!ResourcesPlugin.getWorkspace().getRoot().exists(newChildPath)
                        && !paths.contains(newChildPath.toString())) {
                        paths.add(newChildPath.toString());
                    }
                }
            }
        }
        return paths;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (13.02.2013)
     */
    @Override
    public Object getParent(Object element) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (13.02.2013)
     */
    @Override
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (13.02.2013)
     */
    @Override
    public void dispose() {
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (13.02.2013)
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    /**
     * Retrieves all non package children
     * @param parent
     *            the children should be retrieved for
     * @return all non package children
     * @throws CoreException
     * @author mbrunnli (14.02.2013)
     */
    private Set<Object> getNonPackageChildren(IParent parent) throws CoreException {
        Set<Object> children = new HashSet<Object>();
        for (IJavaElement c : parent.getChildren()) {
            if (!(c instanceof IPackageFragment) && !(c instanceof IPackageFragmentRoot)) {
                children.add(c);
            }
        }
        if (parent instanceof IJavaElement) {
            IResource r = ((IJavaElement) parent).getResource();
            if (r != null && r instanceof IContainer) {
                children.addAll(getNoneDuplicateResourceChildren((IContainer) r));
            }
        }
        return children;
    }

    /**
     * Returns all children of the given parent {@link IResource} if and only if it has no corresponding
     * {@link IJavaElement} determined by {@link JavaCore#create(org.eclipse.core.resources.IResource)}
     * @param parent
     *            {@link IResource} the children should be determined
     * @return all {@link IResource} children which have no {@link IJavaElement} representation
     * @throws CoreException
     * @author mbrunnli (04.03.2013)
     */
    private List<Object> getNoneDuplicateResourceChildren(IContainer parent) throws CoreException {
        List<Object> children = new ArrayList<Object>();
        IResource[] resources = parent.members();
        for (IResource child : resources) {
            IJavaElement jChild = JavaCore.create(child);
            // only add child of type IResource if it has not java representation
            // in this case it should have been added before
            if (jChild == null) {
                if (isPartOfAnySourceFolder(child.getFullPath().toString())) {
                    if (isPartOfAnyNonJavaPath(child.getFullPath().toString())) {
                        children.add(child);
                    }
                } else {
                    children.add(child);
                }
            } else {
                if (!isPartOfAnySourceFolder(child.getFullPath().toString())) {
                    if (isPartOfAnyNonJavaPath(child.getFullPath().toString())) {
                        children.add(child);
                    }
                }
            }
        }
        return children;
    }

    /**
     * @param children
     *            a list of children which should be filtered
     * @return all children affected by the generation process
     * @author mbrunnli (14.02.2013)
     */
    private Object[] getAffectedChildren(List<Object> children) {
        List<Object> affectedChildren = new ArrayList<Object>();
        for (Object e : children) {
            if (e instanceof IJavaElement && isElementToBeShown(((IJavaElement) e).getPath())) {
                affectedChildren.add(e);
            } else if (e instanceof IResource && isElementToBeShown(((IResource) e).getFullPath())) {
                affectedChildren.add(e);
            }
        }
        return affectedChildren.toArray();
    }

    /**
     * Checks whether the given path is affected by the generation
     * @param fullPath
     *            path of the current element
     * @return true, if the path is affected by any template generation<br>
     *         false, otherwise
     * @author mbrunnli (14.02.2013)
     */
    private boolean isElementToBeShown(IPath fullPath) {
        for (String s : filteredPaths) {
            if (s.startsWith(fullPath.toString()) || fullPath.toString().contains("MOCK")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the given path is defined within an {@link IPackageFragmentRoot} or is part of one
     * @param path
     *            to be checked
     * @return <code>true</code> if the path starts with the path of any {@link IPackageFragmentRoot} or is
     *         contained in it<br>
     *         <code>false</code> otherwise
     * @author mbrunnli (11.03.2013)
     */
    private boolean isPartOfAnySourceFolder(String path) {
        for (IPackageFragmentRoot root : _cachePackageFragmentRoots) {
            if (root.getPath().toString().contains(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the given path is part of any filtered path which is not defined within a source folder
     * @param path
     *            to be checked
     * @return <code>true</code> if the given path is not defined within any source folder
     * @author mbrunnli (12.03.2013)
     */
    private boolean isPartOfAnyNonJavaPath(String path) {
        for (String resourcePath : getNonJavaResourcePaths()) {
            if (resourcePath.startsWith(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all filtered paths which do not contain any jdt containers
     * @return all filtered paths which do not contain any jdt containers
     * @author mbrunnli (11.03.2013)
     */
    private Set<String> getNonJavaResourcePaths() {
        Set<String> nonJavaPaths = new HashSet<String>();
        for (String path : filteredPaths) {
            if (!isDefinedInAnySourceFolder(path)) {
                nonJavaPaths.add(path);
            }
        }
        return nonJavaPaths;
    }

    /**
     * Checks whether the given path is defined within an {@link IPackageFragmentRoot}
     * @param path
     *            to be checked
     * @return <code>true</code> if the path starts with the path of any {@link IPackageFragmentRoot}<br>
     *         <code>false</code> otherwise
     * @author mbrunnli (11.03.2013)
     */
    private boolean isDefinedInAnySourceFolder(String path) {
        for (IPackageFragmentRoot root : _cachePackageFragmentRoots) {
            if (path.startsWith(root.getPath().toString())) {
                return true;
            }
        }
        return false;
    }

}

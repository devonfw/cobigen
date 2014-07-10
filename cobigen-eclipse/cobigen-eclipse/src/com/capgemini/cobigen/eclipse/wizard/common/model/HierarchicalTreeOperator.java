/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.wizard.common.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HierarchicalTreeOperator} is a wrapper for all tree related functionality for displaying
 * packages in a hierarchical folded way
 * @author mbrunnli (18.03.2013)
 */
public class HierarchicalTreeOperator {

    /**
     * Assigning logger to HierarchicalTreeOperator
     */
    private static final Logger LOG = LoggerFactory.getLogger(HierarchicalTreeOperator.class);

    /**
     * Returns all package children (folded if possible)
     * @param parentElement
     *            parent {@link IPackageFragmentRoot}
     * @return a list of {@link IPackageFragment} children
     * @throws JavaModelException
     * @author mbrunnli (18.03.2013)
     */
    public static List<Object> getPackageChildren(IPackageFragmentRoot parentElement)
        throws JavaModelException {
        List<Object> children = new LinkedList<Object>();
        for (IPackageFragment frag : retrievePackageChildren(parentElement)) {
            if (isAtomicChild(null, frag, true)) {
                Object foldedPkg = fold(frag);
                if (!children.contains(foldedPkg)) {
                    children.add(foldedPkg);
                }
            }
        }
        return children;
    }

    /**
     * Returns all package children (folded if possible)
     * @param parentElement
     *            parent {@link IPackageFragment}
     * @return a list of {@link IPackageFragment} children
     * @throws JavaModelException
     * @author mbrunnli (18.03.2013)
     */
    public static List<IPackageFragment> getPackageChildren(IPackageFragment parentElement)
        throws JavaModelException {
        List<IPackageFragment> children = new LinkedList<IPackageFragment>();
        for (IPackageFragment frag : retrievePackageChildren(parentElement)) {
            if (isAtomicChild(parentElement, frag, false)) {
                children.add(fold(frag));
            }
        }
        return children;
    }

    /**
     * Folds the given {@link IPackageFragment} and returns the folded non atomic child
     * @param frag
     *            {@link IPackageFragment}
     * @return the folded non atomic child
     * @throws JavaModelException
     *             if one of the {@link IPackageFragment} children does not exist or if an exception occurs
     *             while accessing its corresponding resource
     * @author mbrunnli (18.03.2013)
     */
    private static IPackageFragment fold(IPackageFragment frag) throws JavaModelException { // TODO fix
                                                                                            // folding for
        // stubs
        List<IPackageFragment> packageChildren = getPackageChildren(frag);
        IPackageFragment curr = frag;
        while (curr.getChildren().length == 0 && packageChildren.size() == 1) {
            curr = (IPackageFragment) packageChildren.get(0);
            packageChildren = getPackageChildren(curr);
        }
        return curr;
    }

    /**
     * Returns the parent object (either an {@link IPackageFragment} or an {@link IPackageFragmentRoot})
     * @param fragment
     *            {@link IPackageFragment} for which the parent should be retrieved
     * @return the parent object (either an {@link IPackageFragment} or an {@link IPackageFragmentRoot})
     * @author mbrunnli (18.03.2013)
     */
    public static Object getParent(IPackageFragment fragment) {
        IPackageFragmentRoot root = (IPackageFragmentRoot) fragment.getParent();
        if (isAtomicChild(null, fragment, true)) {
            return root;
        } else {
            String fragName = fragment.getElementName();
            String parentPackageName = fragName.substring(0, fragName.lastIndexOf("."));
            IPackageFragment parentPackage = root.getPackageFragment(parentPackageName);
            return parentPackage;
        }
    }

    /**
     * Returns the child name for the given {@link IPackageFragment}
     * @param fragment
     *            {@link IPackageFragment}
     * @return the child name in a hierarchical manner
     * @author mbrunnli (18.03.2013)
     */
    public static String getChildName(IPackageFragment fragment) {
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
     * @param parent
     *            {@link IPackageFragment}
     * @return <code>true</code> if the given {@link IPackageFragment} is folded<br>
     *         <code>false</code> otherwise
     * @author mbrunnli (18.03.2013)
     */
    private static boolean isFolded(IPackageFragment parent) {
        try {
            return !parent.equals(fold(parent));
        } catch (JavaModelException e) {
            LOG.error("A JavaModelException occured", e);
        }
        return false;
    }

    /**
     * Returns all {@link IPackageFragment} children of the given {@link IPackageFragmentRoot}
     * @param parentElement
     *            {@link IPackageFragmentRoot}
     * @return all {@link IPackageFragment} children of the given {@link IPackageFragmentRoot}
     * @throws JavaModelException
     *             if the parentElement does not exist or if an exception occurs while accessing its
     *             corresponding resource
     * @author mbrunnli (18.03.2013)
     */
    private static List<IPackageFragment> retrievePackageChildren(IPackageFragmentRoot parentElement)
        throws JavaModelException {
        List<IPackageFragment> packageChildren = new LinkedList<IPackageFragment>();
        for (IJavaElement child : parentElement.getChildren()) {
            if (child instanceof IPackageFragment) {
                packageChildren.add((IPackageFragment) child);
            }
        }
        return packageChildren;
    }

    /**
     * Returns all {@link IPackageFragment} children of the given {@link IPackageFragment}
     * @param parentElement
     *            {@link IPackageFragment}
     * @return all {@link IPackageFragment} children of the given {@link IPackageFragment}
     * @throws JavaModelException
     *             if the parentElement does not exist or if an exception occurs while accessing its
     *             corresponding resource
     * @author mbrunnli (18.03.2013)
     */
    private static List<IPackageFragment> retrievePackageChildren(IPackageFragment parentElement)
        throws JavaModelException {

        return retrievePackageChildren((IPackageFragmentRoot) parentElement.getParent());
    }

    /**
     * Checks whether the the given {@link IPackageFragment} child is an atomic child of the given
     * {@link IPackageFragment} parent. Atomic means only one further defined package beyond the given parent.
     * @param parent
     *            {@link IPackageFragment}
     * @param child
     *            {@link IPackageFragment}
     * @param considerDefaultPackage
     *            states whether the default package should be considered or not
     * @return <code>true</code> if the child defines exactly one package beyond the given parent package<br>
     *         <code>false</code> otherwise
     * @author mbrunnli (18.03.2013)
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

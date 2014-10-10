/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.eclipse.wizard.common.model.stubs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;

/**
 * Stub for {@link IPackageFragment} in order to simulate resources in the generate wizard
 *
 * @author mbrunnli (05.04.2014)
 */
public class IPackageFragmentStub extends IJavaElementStub implements IPackageFragment {

    /**
     * All non java resources
     */
    private Object[] nonJavaResources;

    /**
     * Setzt das Feld 'nonJavaResources'.
     *
     * @param nonJavaResources
     *            Neuer Wert für nonJavaResources
     * @author mbrunnli (05.04.2014)
     */
    public void setNonJavaResources(Object[] nonJavaResources) {

        this.nonJavaResources = nonJavaResources;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void close() throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public String findRecommendedLineSeparator() throws JavaModelException {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IBuffer getBuffer() throws JavaModelException {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean hasUnsavedChanges() throws JavaModelException {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean isConsistent() throws JavaModelException {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean isOpen() {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void makeConsistent(IProgressMonitor progress) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void open(IProgressMonitor progress) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void save(IProgressMonitor progress, boolean force) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void copy(IJavaElement container, IJavaElement sibling, String rename, boolean replace,
        IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void delete(boolean force, IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void move(IJavaElement container, IJavaElement sibling, String rename, boolean replace,
        IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void rename(String name, boolean replace, IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean containsJavaResources() throws JavaModelException {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public ICompilationUnit createCompilationUnit(String name, String contents, boolean force,
        IProgressMonitor monitor) throws JavaModelException {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IClassFile getClassFile(String name) {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IClassFile[] getClassFiles() throws JavaModelException {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public ICompilationUnit getCompilationUnit(String name) {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public ICompilationUnit[] getCompilationUnits() throws JavaModelException {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public ICompilationUnit[] getCompilationUnits(WorkingCopyOwner owner) throws JavaModelException {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public int getKind() throws JavaModelException {

        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public Object[] getNonJavaResources() throws JavaModelException {

        return this.nonJavaResources;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean hasSubpackages() throws JavaModelException {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean isDefaultPackage() {

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return getPath().toString().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == null) return false;
        if (obj instanceof IPackageFragment) {
            return ((IPackageFragment) obj).getPath().equals(getPath());
        }
        return false;
    }
}

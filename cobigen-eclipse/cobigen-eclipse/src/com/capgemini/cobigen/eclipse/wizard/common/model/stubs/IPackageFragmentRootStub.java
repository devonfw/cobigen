/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.eclipse.wizard.common.model.stubs;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Stub for {@link IPackageFragmentRoot} in order to simulate resources in the generate wizard
 * @author mbrunnli (05.04.2014)
 */
public class IPackageFragmentRootStub implements IPackageFragmentRoot {

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaElement[] getChildren() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean hasChildren() throws JavaModelException {
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean exists() {
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaElement getAncestor(int ancestorType) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public String getAttachedJavadoc(IProgressMonitor monitor) throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IResource getCorrespondingResource() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public String getElementName() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public int getElementType() {
        return 0;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public String getHandleIdentifier() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaModel getJavaModel() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaProject getJavaProject() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IOpenable getOpenable() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaElement getParent() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IPath getPath() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaElement getPrimaryElement() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IResource getResource() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public ISchedulingRule getSchedulingRule() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IResource getUnderlyingResource() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean isReadOnly() {
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean isStructureKnown() throws JavaModelException {
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void close() throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public String findRecommendedLineSeparator() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IBuffer getBuffer() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean hasUnsavedChanges() throws JavaModelException {
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean isConsistent() throws JavaModelException {
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean isOpen() {
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void makeConsistent(IProgressMonitor progress) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void open(IProgressMonitor progress) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void save(IProgressMonitor progress, boolean force) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void attachSource(IPath sourcePath, IPath rootPath, IProgressMonitor monitor)
        throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void copy(IPath destination, int updateResourceFlags, int updateModelFlags,
        IClasspathEntry sibling, IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IPackageFragment createPackageFragment(String name, boolean force, IProgressMonitor monitor)
        throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void delete(int updateResourceFlags, int updateModelFlags, IProgressMonitor monitor)
        throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public int getKind() throws JavaModelException {
        return 0;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public Object[] getNonJavaResources() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IPackageFragment getPackageFragment(String packageName) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IClasspathEntry getRawClasspathEntry() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IClasspathEntry getResolvedClasspathEntry() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IPath getSourceAttachmentPath() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IPath getSourceAttachmentRootPath() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean isArchive() {
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean isExternal() {
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void move(IPath destination, int updateResourceFlags, int updateModelFlags,
        IClasspathEntry sibling, IProgressMonitor monitor) throws JavaModelException {

    }

}

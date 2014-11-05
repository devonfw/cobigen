package com.capgemini.cobigen.eclipse.wizard.common.model.stubs;

import java.net.URI;

import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Stub for {@link IFolder} in order to simulate resources in the generate wizard
 * @author mbrunnli (06.04.2014)
 */
public class IFolderStub extends IResourceStub implements IFolder {

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean exists(IPath path) {
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IResource findMember(String path) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IResource findMember(String path, boolean includePhantoms) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IResource findMember(IPath path) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IResource findMember(IPath path, boolean includePhantoms) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public String getDefaultCharset() throws CoreException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public String getDefaultCharset(boolean checkImplicit) throws CoreException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IFile getFile(IPath path) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IFolder getFolder(IPath path) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IResource[] members() throws CoreException {
        return new IResource[0];
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IResource[] members(boolean includePhantoms) throws CoreException {
        return new IResource[0];
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IResource[] members(int memberFlags) throws CoreException {
        return new IResource[0];
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IFile[] findDeletedMembersWithHistory(int depth, IProgressMonitor monitor) throws CoreException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void setDefaultCharset(String charset) throws CoreException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void setDefaultCharset(String charset, IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IResourceFilterDescription createFilter(int type, FileInfoMatcherDescription matcherDescription,
        int updateFlags, IProgressMonitor monitor) throws CoreException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IResourceFilterDescription[] getFilters() throws CoreException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void create(boolean force, boolean local, IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void create(int updateFlags, boolean local, IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor)
        throws CoreException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void createLink(URI location, int updateFlags, IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IFile getFile(String name) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IFolder getFolder(String name) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor)
        throws CoreException {

    }

}

/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.eclipse.wizard.common.model.stubs;

import java.net.URI;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * Stub for {@link IResource} in order to simulate resources in the generate wizard
 *
 * @author mbrunnli (05.04.2014)
 */
public class IResourceStub implements IResource {

    /**
     * FullPath of the Resource
     */
    private IPath fullPath;

    /**
     * Setzt das Feld 'fullPath'.
     *
     * @param fullPath
     *            Neuer Wert für fullPath
     * @author mbrunnli (06.04.2014)
     */
    public void setFullPath(IPath fullPath) {

        this.fullPath = fullPath;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public Object getAdapter(Class adapter) {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean contains(ISchedulingRule rule) {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isConflicting(ISchedulingRule rule) {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void accept(IResourceVisitor visitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void clearHistory(IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor)
        throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor)
        throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public IMarker createMarker(String type) throws CoreException {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public IResourceProxy createProxy() {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void delete(boolean force, IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean exists() {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public IMarker findMarker(long id) throws CoreException {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public int findMaxProblemSeverity(String type, boolean includeSubtypes, int depth) throws CoreException {

        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public String getFileExtension() {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public IPath getFullPath() {

        return fullPath;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public long getLocalTimeStamp() {

        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public IPath getLocation() {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public URI getLocationURI() {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public IMarker getMarker(long id) {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public long getModificationStamp() {

        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public String getName() {

        if (fullPath != null) {
            return fullPath.lastSegment();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public IPathVariableManager getPathVariableManager() {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public IContainer getParent() {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public Map<QualifiedName, String> getPersistentProperties() throws CoreException {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public String getPersistentProperty(QualifiedName key) throws CoreException {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public IProject getProject() {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public IPath getProjectRelativePath() {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public IPath getRawLocation() {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public URI getRawLocationURI() {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public ResourceAttributes getResourceAttributes() {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public Map<QualifiedName, Object> getSessionProperties() throws CoreException {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public Object getSessionProperty(QualifiedName key) throws CoreException {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public int getType() {

        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public IWorkspace getWorkspace() {

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isAccessible() {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isDerived() {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isDerived(int options) {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isHidden() {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isHidden(int options) {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isLinked() {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isVirtual() {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isLinked(int options) {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isLocal(int depth) {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isPhantom() {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isReadOnly() {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isSynchronized(int depth) {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isTeamPrivateMember() {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public boolean isTeamPrivateMember(int options) {

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void move(IProjectDescription description, boolean force, boolean keepHistory,
        IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor)
        throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void revertModificationStamp(long value) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void setDerived(boolean isDerived) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void setDerived(boolean isDerived, IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void setHidden(boolean isHidden) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public long setLocalTimeStamp(long value) throws CoreException {

        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void setPersistentProperty(QualifiedName key, String value) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void setReadOnly(boolean readOnly) {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void setResourceAttributes(ResourceAttributes attributes) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void setSessionProperty(QualifiedName key, Object value) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (05.04.2014)
     */
    @Override
    public void touch(IProgressMonitor monitor) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void accept(IResourceProxyVisitor visitor, int memberFlags) throws CoreException {

    }

    /**
     * {@inheritDoc}
     *
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void accept(IResourceProxyVisitor visitor, int depth, int memberFlags) throws CoreException {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (obj instanceof IResource) {
            return ((IResource) obj).getFullPath().equals(getFullPath());
        }
        return false;
    }

}

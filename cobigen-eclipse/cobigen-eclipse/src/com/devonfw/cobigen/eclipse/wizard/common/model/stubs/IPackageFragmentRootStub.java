package com.devonfw.cobigen.eclipse.wizard.common.model.stubs;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IModuleDescription;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Stub for {@link IPackageFragmentRoot} in order to simulate resources in the generate wizard
 *
 * @author mbrunnli (05.04.2014)
 */
public class IPackageFragmentRootStub implements IPackageFragmentRoot {

  @Override
  public IJavaElement[] getChildren() throws JavaModelException {

    return null;
  }

  @Override
  public boolean hasChildren() throws JavaModelException {

    return false;
  }

  @Override
  public boolean exists() {

    return false;
  }

  @Override
  public IJavaElement getAncestor(int ancestorType) {

    return null;
  }

  @Override
  public String getAttachedJavadoc(IProgressMonitor monitor) throws JavaModelException {

    return null;
  }

  @Override
  public IResource getCorrespondingResource() throws JavaModelException {

    return null;
  }

  @Override
  public String getElementName() {

    return null;
  }

  @Override
  public int getElementType() {

    return 0;
  }

  @Override
  public String getHandleIdentifier() {

    return null;
  }

  @Override
  public IJavaModel getJavaModel() {

    return null;
  }

  @Override
  public IJavaProject getJavaProject() {

    return null;
  }

  @Override
  public IOpenable getOpenable() {

    return null;
  }

  @Override
  public IJavaElement getParent() {

    return null;
  }

  @Override
  public IPath getPath() {

    return null;
  }

  @Override
  public IJavaElement getPrimaryElement() {

    return null;
  }

  @Override
  public IResource getResource() {

    return null;
  }

  @Override
  public ISchedulingRule getSchedulingRule() {

    return null;
  }

  @Override
  public IResource getUnderlyingResource() throws JavaModelException {

    return null;
  }

  @Override
  public boolean isReadOnly() {

    return false;
  }

  @Override
  public boolean isStructureKnown() throws JavaModelException {

    return false;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object getAdapter(Class adapter) {

    return null;
  }

  @Override
  public void close() throws JavaModelException {

  }

  @Override
  public String findRecommendedLineSeparator() throws JavaModelException {

    return null;
  }

  @Override
  public IBuffer getBuffer() throws JavaModelException {

    return null;
  }

  @Override
  public boolean hasUnsavedChanges() throws JavaModelException {

    return false;
  }

  @Override
  public boolean isConsistent() throws JavaModelException {

    return false;
  }

  @Override
  public boolean isOpen() {

    return false;
  }

  @Override
  public void makeConsistent(IProgressMonitor progress) throws JavaModelException {

  }

  @Override
  public void open(IProgressMonitor progress) throws JavaModelException {

  }

  @Override
  public void save(IProgressMonitor progress, boolean force) throws JavaModelException {

  }

  @Override
  public void attachSource(IPath sourcePath, IPath rootPath, IProgressMonitor monitor) throws JavaModelException {

  }

  @Override
  public void copy(IPath destination, int updateResourceFlags, int updateModelFlags, IClasspathEntry sibling,
      IProgressMonitor monitor) throws JavaModelException {

  }

  @Override
  public IPackageFragment createPackageFragment(String name, boolean force, IProgressMonitor monitor)
      throws JavaModelException {

    return null;
  }

  @Override
  public void delete(int updateResourceFlags, int updateModelFlags, IProgressMonitor monitor)
      throws JavaModelException {

  }

  @Override
  public int getKind() throws JavaModelException {

    return 0;
  }

  @Override
  public Object[] getNonJavaResources() throws JavaModelException {

    return null;
  }

  @Override
  public IPackageFragment getPackageFragment(String packageName) {

    return null;
  }

  @Override
  public IClasspathEntry getRawClasspathEntry() throws JavaModelException {

    return null;
  }

  @Override
  public IClasspathEntry getResolvedClasspathEntry() throws JavaModelException {

    return null;
  }

  @Override
  public IPath getSourceAttachmentPath() throws JavaModelException {

    return null;
  }

  @Override
  public IPath getSourceAttachmentRootPath() throws JavaModelException {

    return null;
  }

  @Override
  public boolean isArchive() {

    return false;
  }

  @Override
  public boolean isExternal() {

    return false;
  }

  @Override
  public void move(IPath destination, int updateResourceFlags, int updateModelFlags, IClasspathEntry sibling,
      IProgressMonitor monitor) throws JavaModelException {

  }

  @Override
  public IModuleDescription getModuleDescription() {

    return null;
  }

}

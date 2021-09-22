package com.devonfw.cobigen.eclipse.wizard.common.model.stubs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IModularClassFile;
import org.eclipse.jdt.core.IOrdinaryClassFile;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;

/**
 * Stub for {@link IPackageFragment} in order to simulate resources in the generate wizard
 */
public class IPackageFragmentStub extends IJavaElementStub implements IPackageFragment {

  /** All non java resources */
  private Object[] nonJavaResources;

  /**
   * Setzt das Feld 'nonJavaResources'.
   *
   * @param nonJavaResources Neuer Wert für nonJavaResources
   */
  public void setNonJavaResources(Object[] nonJavaResources) {

    this.nonJavaResources = nonJavaResources;
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
  public void copy(IJavaElement container, IJavaElement sibling, String rename, boolean replace,
      IProgressMonitor monitor) throws JavaModelException {

  }

  @Override
  public void delete(boolean force, IProgressMonitor monitor) throws JavaModelException {

  }

  @Override
  public void move(IJavaElement container, IJavaElement sibling, String rename, boolean replace,
      IProgressMonitor monitor) throws JavaModelException {

  }

  @Override
  public void rename(String name, boolean replace, IProgressMonitor monitor) throws JavaModelException {

  }

  @Override
  public boolean containsJavaResources() throws JavaModelException {

    return false;
  }

  @Override
  public ICompilationUnit createCompilationUnit(String name, String contents, boolean force, IProgressMonitor monitor)
      throws JavaModelException {

    return null;
  }

  @Override
  public IClassFile getClassFile(String name) {

    return null;
  }

  @Override
  public IClassFile[] getClassFiles() throws JavaModelException {

    return null;
  }

  @Override
  public ICompilationUnit getCompilationUnit(String name) {

    return null;
  }

  @Override
  public ICompilationUnit[] getCompilationUnits() throws JavaModelException {

    return null;
  }

  @Override
  public ICompilationUnit[] getCompilationUnits(WorkingCopyOwner owner) throws JavaModelException {

    return null;
  }

  @Override
  public int getKind() throws JavaModelException {

    return 0;
  }

  @Override
  public Object[] getNonJavaResources() throws JavaModelException {

    return this.nonJavaResources;
  }

  @Override
  public boolean hasSubpackages() throws JavaModelException {

    return false;
  }

  @Override
  public boolean isDefaultPackage() {

    return false;
  }

  @Override
  public int hashCode() {

    return getPath().toString().hashCode();
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }
    if (obj instanceof IPackageFragment) {
      return ((IPackageFragment) obj).getPath().equals(getPath());
    }
    return false;
  }

  @Override
  public IOrdinaryClassFile getOrdinaryClassFile(String name) {

    return null;
  }

  @Override
  public IModularClassFile getModularClassFile() {

    return null;
  }

  @Override
  public IClassFile[] getAllClassFiles() throws JavaModelException {

    return null;
  }

  @Override
  public IOrdinaryClassFile[] getOrdinaryClassFiles() throws JavaModelException {

    return null;
  }
}

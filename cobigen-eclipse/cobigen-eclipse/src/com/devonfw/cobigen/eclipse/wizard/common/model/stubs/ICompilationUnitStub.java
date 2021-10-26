package com.devonfw.cobigen.eclipse.wizard.common.model.stubs;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IBufferFactory;
import org.eclipse.jdt.core.ICodeCompletionRequestor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ICompletionRequestor;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IProblemRequestor;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;

/**
 * Stub for {@link ICompilationUnit} in order to simulate resources in the generate wizard
 *
 * @author mbrunnli (05.04.2014)
 */
@SuppressWarnings("deprecation")
public class ICompilationUnitStub extends IJavaElementStub implements ICompilationUnit {

  @Override
  public IType findPrimaryType() {

    return null;
  }

  @Override
  public IJavaElement getElementAt(int position) throws JavaModelException {

    return null;
  }

  @Override
  public ICompilationUnit getWorkingCopy(WorkingCopyOwner owner, IProgressMonitor monitor) throws JavaModelException {

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
  public String getSource() throws JavaModelException {

    return null;
  }

  @Override
  public ISourceRange getSourceRange() throws JavaModelException {

    return null;
  }

  @Override
  public ISourceRange getNameRange() throws JavaModelException {

    return null;
  }

  @Override
  public void codeComplete(int offset, ICodeCompletionRequestor requestor) throws JavaModelException {

  }

  @Override
  public void codeComplete(int offset, ICompletionRequestor requestor) throws JavaModelException {

  }

  @Override
  public void codeComplete(int offset, CompletionRequestor requestor) throws JavaModelException {

  }

  @Override
  public void codeComplete(int offset, CompletionRequestor requestor, IProgressMonitor monitor)
      throws JavaModelException {

  }

  @Override
  public void codeComplete(int offset, ICompletionRequestor requestor, WorkingCopyOwner owner)
      throws JavaModelException {

  }

  @Override
  public void codeComplete(int offset, CompletionRequestor requestor, WorkingCopyOwner owner)
      throws JavaModelException {

  }

  @Override
  public void codeComplete(int offset, CompletionRequestor requestor, WorkingCopyOwner owner, IProgressMonitor monitor)
      throws JavaModelException {

  }

  @Override
  public IJavaElement[] codeSelect(int offset, int length) throws JavaModelException {

    return null;
  }

  @Override
  public IJavaElement[] codeSelect(int offset, int length, WorkingCopyOwner owner) throws JavaModelException {

    return null;
  }

  @Override
  public void commit(boolean force, IProgressMonitor monitor) throws JavaModelException {

  }

  @Override
  public void destroy() {

  }

  @Override
  public IJavaElement findSharedWorkingCopy(IBufferFactory bufferFactory) {

    return null;
  }

  @Override
  public IJavaElement getOriginal(IJavaElement workingCopyElement) {

    return null;
  }

  @Override
  public IJavaElement getOriginalElement() {

    return null;
  }

  @Override
  public IJavaElement getSharedWorkingCopy(IProgressMonitor monitor, IBufferFactory factory,
      IProblemRequestor problemRequestor) throws JavaModelException {

    return null;
  }

  @Override
  public IJavaElement getWorkingCopy() throws JavaModelException {

    return null;
  }

  @Override
  public IJavaElement getWorkingCopy(IProgressMonitor monitor, IBufferFactory factory,
      IProblemRequestor problemRequestor) throws JavaModelException {

    return null;
  }

  @Override
  public boolean isBasedOn(IResource resource) {

    return false;
  }

  @Override
  public IMarker[] reconcile() throws JavaModelException {

    return null;
  }

  @Override
  public void reconcile(boolean forceProblemDetection, IProgressMonitor monitor) throws JavaModelException {

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
  public UndoEdit applyTextEdit(TextEdit edit, IProgressMonitor monitor) throws JavaModelException {

    return null;
  }

  @Override
  public void becomeWorkingCopy(IProblemRequestor problemRequestor, IProgressMonitor monitor)
      throws JavaModelException {

  }

  @Override
  public void becomeWorkingCopy(IProgressMonitor monitor) throws JavaModelException {

  }

  @Override
  public void commitWorkingCopy(boolean force, IProgressMonitor monitor) throws JavaModelException {

  }

  @Override
  public IImportDeclaration createImport(String name, IJavaElement sibling, IProgressMonitor monitor)
      throws JavaModelException {

    return null;
  }

  @Override
  public IImportDeclaration createImport(String name, IJavaElement sibling, int flags, IProgressMonitor monitor)
      throws JavaModelException {

    return null;
  }

  @Override
  public IPackageDeclaration createPackageDeclaration(String name, IProgressMonitor monitor) throws JavaModelException {

    return null;
  }

  @Override
  public IType createType(String contents, IJavaElement sibling, boolean force, IProgressMonitor monitor)
      throws JavaModelException {

    return null;
  }

  @Override
  public void discardWorkingCopy() throws JavaModelException {

  }

  @Override
  public IJavaElement[] findElements(IJavaElement element) {

    return null;
  }

  @Override
  public ICompilationUnit findWorkingCopy(WorkingCopyOwner owner) {

    return null;
  }

  @Override
  public IType[] getAllTypes() throws JavaModelException {

    return null;
  }

  @Override
  public IImportDeclaration getImport(String name) {

    return null;
  }

  @Override
  public IImportContainer getImportContainer() {

    return null;
  }

  @Override
  public IImportDeclaration[] getImports() throws JavaModelException {

    return null;
  }

  @Override
  public ICompilationUnit getPrimary() {

    return null;
  }

  @Override
  public WorkingCopyOwner getOwner() {

    return null;
  }

  @Override
  public IPackageDeclaration getPackageDeclaration(String name) {

    return null;
  }

  @Override
  public IPackageDeclaration[] getPackageDeclarations() throws JavaModelException {

    return null;
  }

  @Override
  public IType getType(String name) {

    return null;
  }

  @Override
  public IType[] getTypes() throws JavaModelException {

    return null;
  }

  @Override
  public ICompilationUnit getWorkingCopy(IProgressMonitor monitor) throws JavaModelException {

    return null;
  }

  @Override
  public ICompilationUnit getWorkingCopy(WorkingCopyOwner owner, IProblemRequestor problemRequestor,
      IProgressMonitor monitor) throws JavaModelException {

    return null;
  }

  @Override
  public boolean hasResourceChanged() {

    return false;
  }

  @Override
  public boolean isWorkingCopy() {

    return false;
  }

  @Override
  public CompilationUnit reconcile(int astLevel, boolean forceProblemDetection, WorkingCopyOwner owner,
      IProgressMonitor monitor) throws JavaModelException {

    return null;
  }

  @Override
  public CompilationUnit reconcile(int astLevel, boolean forceProblemDetection, boolean enableStatementsRecovery,
      WorkingCopyOwner owner, IProgressMonitor monitor) throws JavaModelException {

    return null;
  }

  @Override
  public CompilationUnit reconcile(int astLevel, int reconcileFlags, WorkingCopyOwner owner, IProgressMonitor monitor)
      throws JavaModelException {

    return null;
  }

  @Override
  public void restore() throws JavaModelException {

  }

}

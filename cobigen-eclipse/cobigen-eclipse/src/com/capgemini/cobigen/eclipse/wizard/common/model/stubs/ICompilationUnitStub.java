package com.capgemini.cobigen.eclipse.wizard.common.model.stubs;

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
 * @author mbrunnli (05.04.2014)
 */
@SuppressWarnings("deprecation")
public class ICompilationUnitStub extends IJavaElementStub implements ICompilationUnit {

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IType findPrimaryType() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaElement getElementAt(int position) throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public ICompilationUnit getWorkingCopy(WorkingCopyOwner owner, IProgressMonitor monitor)
        throws JavaModelException {
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
    public String getSource() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public ISourceRange getSourceRange() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public ISourceRange getNameRange() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void codeComplete(int offset, ICodeCompletionRequestor requestor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void codeComplete(int offset, ICompletionRequestor requestor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void codeComplete(int offset, CompletionRequestor requestor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void codeComplete(int offset, CompletionRequestor requestor, IProgressMonitor monitor)
        throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void codeComplete(int offset, ICompletionRequestor requestor, WorkingCopyOwner owner)
        throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void codeComplete(int offset, CompletionRequestor requestor, WorkingCopyOwner owner)
        throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void codeComplete(int offset, CompletionRequestor requestor, WorkingCopyOwner owner,
        IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaElement[] codeSelect(int offset, int length) throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaElement[] codeSelect(int offset, int length, WorkingCopyOwner owner)
        throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void commit(boolean force, IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void destroy() {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaElement findSharedWorkingCopy(IBufferFactory bufferFactory) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaElement getOriginal(IJavaElement workingCopyElement) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaElement getOriginalElement() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaElement getSharedWorkingCopy(IProgressMonitor monitor, IBufferFactory factory,
        IProblemRequestor problemRequestor) throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaElement getWorkingCopy() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaElement getWorkingCopy(IProgressMonitor monitor, IBufferFactory factory,
        IProblemRequestor problemRequestor) throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean isBasedOn(IResource resource) {
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IMarker[] reconcile() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void reconcile(boolean forceProblemDetection, IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void copy(IJavaElement container, IJavaElement sibling, String rename, boolean replace,
        IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void delete(boolean force, IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void move(IJavaElement container, IJavaElement sibling, String rename, boolean replace,
        IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void rename(String name, boolean replace, IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public UndoEdit applyTextEdit(TextEdit edit, IProgressMonitor monitor) throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void becomeWorkingCopy(IProblemRequestor problemRequestor, IProgressMonitor monitor)
        throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void becomeWorkingCopy(IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void commitWorkingCopy(boolean force, IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IImportDeclaration createImport(String name, IJavaElement sibling, IProgressMonitor monitor)
        throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IImportDeclaration createImport(String name, IJavaElement sibling, int flags,
        IProgressMonitor monitor) throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IPackageDeclaration createPackageDeclaration(String name, IProgressMonitor monitor)
        throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IType createType(String contents, IJavaElement sibling, boolean force, IProgressMonitor monitor)
        throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void discardWorkingCopy() throws JavaModelException {

    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IJavaElement[] findElements(IJavaElement element) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public ICompilationUnit findWorkingCopy(WorkingCopyOwner owner) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IType[] getAllTypes() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IImportDeclaration getImport(String name) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IImportContainer getImportContainer() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IImportDeclaration[] getImports() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public ICompilationUnit getPrimary() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public WorkingCopyOwner getOwner() {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IPackageDeclaration getPackageDeclaration(String name) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IPackageDeclaration[] getPackageDeclarations() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IType getType(String name) {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public IType[] getTypes() throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public ICompilationUnit getWorkingCopy(IProgressMonitor monitor) throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public ICompilationUnit getWorkingCopy(WorkingCopyOwner owner, IProblemRequestor problemRequestor,
        IProgressMonitor monitor) throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean hasResourceChanged() {
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public boolean isWorkingCopy() {
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public CompilationUnit reconcile(int astLevel, boolean forceProblemDetection, WorkingCopyOwner owner,
        IProgressMonitor monitor) throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public CompilationUnit reconcile(int astLevel, boolean forceProblemDetection,
        boolean enableStatementsRecovery, WorkingCopyOwner owner, IProgressMonitor monitor)
        throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public CompilationUnit reconcile(int astLevel, int reconcileFlags, WorkingCopyOwner owner,
        IProgressMonitor monitor) throws JavaModelException {
        return null;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (06.04.2014)
     */
    @Override
    public void restore() throws JavaModelException {

    }

}

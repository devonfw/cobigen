package com.devonfw.cobigen.eclipse.wizard.common.model.stubs;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Stub for the {@link IJavaElement} in order to simulate resources in the generate wizard
 */
public class IJavaElementStub implements IParent, IJavaElement {

  /** Parent element */
  private IJavaElement parent;

  /** Children elements */
  private IJavaElement[] children;

  /** Path of this element */
  private IPath path;

  /** Name of the element */
  private String elementName;

  /** Element Type. See {@link IJavaElement} static fields */
  private int elementType;

  /**
   * Setzt das Feld 'parent'.
   *
   * @param parent Neuer Wert für parent
   */
  public void setParent(IJavaElement parent) {

    this.parent = parent;
  }

  /**
   * Setzt das Feld 'children'.
   *
   * @param children Neuer Wert für children
   */
  public void setChildren(IJavaElement[] children) {

    this.children = children;
  }

  /**
   * Setzt das Feld 'path'.
   *
   * @param path Neuer Wert für path
   */
  public void setPath(IPath path) {

    this.path = path;
  }

  /**
   * Setzt das Feld 'elementName'.
   *
   * @param elementName Neuer Wert für elementName
   */
  public void setElementName(String elementName) {

    this.elementName = elementName;
  }

  /**
   * Setzt das Feld 'elementType'.
   *
   * @param elementType Neuer Wert für elementType
   */
  public void setElementType(int elementType) {

    this.elementType = elementType;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object getAdapter(Class adapter) {

    return null;
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

    return this.elementName;
  }

  @Override
  public int getElementType() {

    return this.elementType;
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

    return this.parent.getJavaProject();
  }

  @Override
  public IOpenable getOpenable() {

    return null;
  }

  @Override
  public IJavaElement getParent() {

    return this.parent;
  }

  @Override
  public IPath getPath() {

    return this.path;
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
  public IJavaElement[] getChildren() throws JavaModelException {

    return this.children;
  }

  @Override
  public boolean hasChildren() throws JavaModelException {

    return this.children != null && this.children.length > 0;
  }

  @Override
  public String toString() {

    return getClass().getSimpleName() + "[" + this.elementName + "]";
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }
    if (obj instanceof IJavaElementStub) {
      if (getPath() == null ^ ((IJavaElementStub) obj).getPath() == null) {
        return false;
      } else if (getPath() == null) {
        return true;
      } else {
        return ((IJavaElementStub) obj).getPath().equals(getPath());
      }
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {

    int hash = 0;
    if (this.path != null) {
      hash ^= this.path.toString().hashCode();
    }
    return hash;
  }
}

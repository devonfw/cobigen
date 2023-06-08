package com.devonfw.cobigen.eclipse.wizard.common.widget;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

/**
 * This {@link CheckboxTreeViewer} enables to have a {@link CheckboxTreeViewer} without displaying the checkboxes. The
 * internal state for the checked behavior will be simulated internally for restore issues.
 */
public class SimulatedCheckboxTreeViewer extends CheckboxTreeViewer {

  /** Currently virtual checked elements */
  private Set<Object> checkedElements;

  /**
   * Creates a new {@link SimulatedCheckboxTreeViewer} which simulates the checked behavior without displaying
   * checkboxes
   *
   * @param parent parent {@link Composite} of the viewer
   */
  public SimulatedCheckboxTreeViewer(Composite parent) {

    super(new Tree(parent, SWT.BORDER));
  }

  @Override
  public void setCheckedElements(Object[] elements) {

    this.checkedElements = new HashSet<>(Arrays.asList(elements));
    fireCheckStateChanged(new CheckStateChangedEvent(this, elements, true));
  }

  @Override
  public Object[] getCheckedElements() {

    return this.checkedElements.toArray();
  }

  @Override
  public boolean setChecked(Object element, boolean state) {

    if (state) {
      if (isVisible(element)) {
        this.checkedElements.add(element);
        fireCheckStateChanged(new CheckStateChangedEvent(this, element, state));
      } else {
        this.checkedElements.remove(element);
      }
    } else {
      this.checkedElements.remove(element);
      fireCheckStateChanged(new CheckStateChangedEvent(this, element, state));
    }
    return true;
  }

  /**
   * Checks whether an element is currently visible in the tree
   *
   * @param element to be checked
   * @return <code>true</code> if the element is currently visible<br>
   *         <code>false</code> otherwise
   */
  private boolean isVisible(Object element) {

    Object[] objects = getVisibleExpandedElements();
    Set<Object> visibleObjects = new HashSet<>();
    visibleObjects.addAll(Arrays.asList(objects));
    for (Object o : objects) {
      visibleObjects.addAll(Arrays.asList(((ITreeContentProvider) getContentProvider()).getChildren(o)));
    }
    return visibleObjects.contains(element);
  }
}

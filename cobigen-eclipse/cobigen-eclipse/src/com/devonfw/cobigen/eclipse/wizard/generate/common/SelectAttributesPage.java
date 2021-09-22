package com.devonfw.cobigen.eclipse.wizard.generate.common;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.slf4j.MDC;

import com.devonfw.cobigen.eclipse.common.constants.InfrastructureConstants;
import com.devonfw.cobigen.eclipse.wizard.generate.model.SelectAttributesContentProvider;
import com.devonfw.cobigen.eclipse.wizard.generate.model.SelectAttributesLabelProvider;

/**
 * The {@link SelectAttributesPage} enables a specific generation of contents which should be shown in the overview and
 * detail pages
 */
public class SelectAttributesPage extends WizardPage {

  /** All attributes retrieved from generation model */
  private Map<String, String> attributes;

  /** {@link CheckboxTableViewer} displaying all attributes */
  private CheckboxTableViewer tableAttributes;

  /**
   * Creates a new page for selecting mandatory attributes
   *
   * @param attributes to be displayed
   */
  public SelectAttributesPage(Map<String, String> attributes) {

    super("Choose attributes displayed in the generated UI");
    this.attributes = attributes;
  }

  @Override
  public void createControl(Composite parent) {

    MDC.put(InfrastructureConstants.CORRELATION_ID, UUID.randomUUID().toString());

    Composite container = new Composite(parent, SWT.FILL);
    container.setLayout(new GridLayout());

    Label label = new Label(container, SWT.WRAP);
    label.setText("Every attribute selected will be displayed in the UI:");

    this.tableAttributes = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.V_SCROLL);
    this.tableAttributes.setContentProvider(new SelectAttributesContentProvider());
    this.tableAttributes.setLabelProvider(new SelectAttributesLabelProvider());
    this.tableAttributes.setInput(this.attributes);
    this.tableAttributes.setAllChecked(true);
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.grabExcessVerticalSpace = true;
    gd.grabExcessHorizontalSpace = true;
    this.tableAttributes.getTable().setLayoutData(gd);

    setControl(container);

    MDC.remove(InfrastructureConstants.CORRELATION_ID);
  }

  /**
   * @return all unchecked attributes names
   */
  public Set<String> getUncheckedAttributes() {

    Set<String> uncheckedAttributes = new HashSet<>();
    for (Entry<String, String> attr : this.attributes.entrySet()) {
      if (!this.tableAttributes.getChecked(attr)) {
        uncheckedAttributes.add(attr.getKey());
      }
    }
    return uncheckedAttributes;
  }

}

package com.devonfw.cobigen.eclipse.wizard.generate.model;

import java.util.Map.Entry;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * This {@link LabelProvider} provides the labels for {@link Entry}s such that the key and value will be displayed
 * separated by " :: "
 */
public class SelectAttributesLabelProvider extends LabelProvider {

  @Override
  public String getText(Object element) {

    if (element instanceof Entry<?, ?>) {
      return ((Entry<?, ?>) element).getKey().toString() + " :: " + ((Entry<?, ?>) element).getValue();
    }
    return super.getText(element);
  }
}

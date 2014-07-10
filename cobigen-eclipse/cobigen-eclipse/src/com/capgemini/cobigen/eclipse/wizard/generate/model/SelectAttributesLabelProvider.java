/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.wizard.generate.model;

import java.util.Map.Entry;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * This {@link LabelProvider} provides the labels for {@link Entry}s such that the key and value will be
 * displayed separated by " :: "
 * @author mbrunnli (12.03.2013)
 */
public class SelectAttributesLabelProvider extends LabelProvider {

    /**
     * {@inheritDoc}
     * @author mbrunnli (12.03.2013)
     */
    @Override
    public String getText(Object element) {
        if (element instanceof Entry<?, ?>) {
            return ((Entry<?, ?>) element).getKey().toString() + " :: " + ((Entry<?, ?>) element).getValue();
        }
        return super.getText(element);
    }
}

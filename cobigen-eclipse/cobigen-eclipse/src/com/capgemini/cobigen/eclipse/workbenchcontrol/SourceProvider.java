/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini.cobigen.eclipse.workbenchcontrol;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;

/**
 * SourceProvider which provides different system variables for accessing them via handlers in the plugin.xml
 * @author mbrunnli (15.02.2013)
 */
public class SourceProvider extends AbstractSourceProvider {

    /**
     * Source Variable stating if the currently selected resources are valid input POJOs for the generation
     * process
     */
    public static final String VALID_INPUT = "com.capgemini.cobigen.eclipseplugin.variables.validInputPojos";

    /**
     * Creates the SourceProvider and initiates all states with false
     * @author mbrunnli (15.02.2013)
     */
    public SourceProvider() {
        map.put(VALID_INPUT, false);
    }

    /**
     * Map of variable value mappings
     */
    private final HashMap<String, Boolean> map = new HashMap<String, Boolean>();

    /**
     * {@inheritDoc}
     * @author mbrunnli (15.02.2013)
     */
    @Override
    public void dispose() {
        map.clear();
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (15.02.2013)
     */
    @Override
    public Map<String, Boolean> getCurrentState() {
        return map;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (15.02.2013)
     */
    @Override
    public String[] getProvidedSourceNames() {
        return map.keySet().toArray(new String[0]);
    }

    /**
     * Sets the given variable (use one of the static fields of this class)
     * @param variable
     *            to be set
     * @param b
     *            new value
     * @author mbrunnli (15.02.2013)
     */
    public void setVariable(String variable, boolean b) {
        if (map.get(variable) != b) {
            map.put(variable, b);
            fireSourceChanged(0, map);
        }
    }

}

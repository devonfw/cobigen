/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.extension.to;

import com.capgemini.cobigen.config.entity.VariableAssignment;

/**
 * 
 * @author mbrunnli (08.04.2014)
 */
public class VariableAssignmentTo {

    /**
     * Type of this variable assignment
     */
    protected String type;

    /**
     * Variable name
     */
    protected String varName;

    /**
     * Concrete value (if set)
     */
    protected String value;

    /**
     * Constructor to create a {@link VariableAssignment} for a concrete string value
     * @param type
     *            Type of the variable assignment, interpreted by the plug-ins
     * @param varName
     *            variable name
     * @param value
     *            concrete string value
     * @author mbrunnli (15.04.2013)
     */
    public VariableAssignmentTo(String type, String varName, String value) {
        this.type = type;
        this.varName = varName;
        this.value = value;
    }

    /**
     * Returns the type, which should determine the variable resolution (if necessary)
     * @return the type
     * @author mbrunnli (14.04.2014)
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the variable name
     * @return variable name
     * @author mbrunnli (14.04.2014)
     */
    public String getVarName() {
        return varName;
    }

    /**
     * Returns the value of the variable assignment, which has to be interpreted by the matcher in order to
     * get the assigned value
     * @return the value for the matcher
     * @author mbrunnli (14.04.2014)
     */
    public String getValue() {
        return value;
    }

}

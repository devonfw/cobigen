package com.capgemini.cobigen.impl.config.entity;

import java.util.Objects;

/**
 * A {@link VariableAssignment} key value pair, which will be interpreted by any plug-in declaring the
 * {@link #type}
 *
 * @author mbrunnli (15.04.2013)
 */
public class VariableAssignment {

    /**
     * Type of this variable assignment
     */
    private String type;

    /**
     * Variable name
     */
    private String varName;

    /**
     * Concrete value (if set)
     */
    private String value;

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
    public VariableAssignment(String type, String varName, String value) {
        this.type = type;
        this.varName = varName;
        this.value = value;
    }

    /**
     * Returns the variable assignment type
     * @return the variable assignment type
     * @author mbrunnli (08.04.2014)
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the variable name
     * @return the variable name
     * @author mbrunnli (15.04.2013)
     */
    public String getVarName() {
        return varName;
    }

    /**
     * Returns the value
     * @return the value
     * @author mbrunnli (08.04.2014)
     */
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, varName, value);
    }
}

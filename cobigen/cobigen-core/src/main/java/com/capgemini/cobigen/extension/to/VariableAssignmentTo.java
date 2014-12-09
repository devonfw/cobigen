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

    /**
     * {@inheritDoc}
     * @author mbrunnli (31.10.2014)
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (getType() == null ? 0 : getType().hashCode());
        result = prime * result + (getVarName() == null ? 0 : getVarName().hashCode());
        result = prime * result + (getValue() == null ? 0 : getValue().hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (31.10.2014)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        if (obj instanceof VariableAssignmentTo) {
            boolean equal = true;
            VariableAssignmentTo otherVariableAssignment = (VariableAssignmentTo) obj;
            if (getType() != null) {
                equal = equal && getType().equals(otherVariableAssignment.getType());
            }
            if (!equal) {
                return false;
            }

            if (getVarName() != null) {
                equal = equal && getVarName().equals(otherVariableAssignment.getVarName());
            }
            if (!equal) {
                return false;
            }

            if (getValue() != null) {
                equal = equal && getValue().equals(otherVariableAssignment.getValue());
            }
            return equal;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (31.10.2014)
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName() + " {");
        result.append("type: " + getType());
        result.append(" var: " + getVarName());
        result.append(" value: " + getValue());
        result.append("}");
        return result.toString();
    }

}

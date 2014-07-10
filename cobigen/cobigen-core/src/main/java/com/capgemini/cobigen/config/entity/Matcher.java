/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.config.entity;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author mbrunnli (08.04.2014)
 */
public class Matcher {

    /**
     * Matcher type
     */
    private String type;

    /**
     * Matcher value to be matched against
     */
    private String value;

    /**
     * {@link VariableAssignment}s
     */
    private List<VariableAssignment> variableAssignments;

    /**
     * States whether this matcher is a container matcher
     */
    private boolean isContainerMatcher;

    /**
     * Creates a new container Matcher for a given type, with a given value to match against
     * @param type
     *            matcher type
     * @param value
     *            to match against
     * @author mbrunnli (08.04.2014)
     */
    public Matcher(String type, String value) {
        this.type = type;
        this.value = value;
        this.isContainerMatcher = true;
    }

    /**
     * Creates a new Matcher for a given type, with a given value to match against and the corresponding
     * variable assignments which should be resolvable
     * @param type
     *            matcher type
     * @param value
     *            to match against
     * @param variableAssignments
     *            of the matcher
     * @author mbrunnli (08.04.2014)
     */
    public Matcher(String type, String value, List<VariableAssignment> variableAssignments) {
        this.type = type;
        this.value = value;
        this.variableAssignments =
            variableAssignments == null ? new LinkedList<VariableAssignment>() : variableAssignments;
    }

    /**
     * Returns the matcher type
     * @return matcher type
     * @author mbrunnli (08.04.2014)
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the value the matcher should match against
     * @return the value the matcher should match against
     * @author mbrunnli (08.04.2014)
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the variable assignments of the matcher
     * @return the variable assignments of the matcher
     * @author mbrunnli (08.04.2014)
     */
    public List<VariableAssignment> getVariableAssignments() {
        return variableAssignments;
    }

    /**
     * Determines whether this matcher is a container matcher
     * @return <code>true</code> if this matcher is a container matcher<br>
     *         <code>false</code>, otherwise
     * @author mbrunnli (03.06.2014)
     */
    public boolean isContainerMatcher() {
        return isContainerMatcher;
    }

}

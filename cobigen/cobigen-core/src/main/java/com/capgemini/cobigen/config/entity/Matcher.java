/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.config.entity;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@link Matcher} type corresponds to the &lt;matcher&gt; xml node
 * @author mbrunnli (08.04.2014)
 */
public class Matcher extends AbstractMatcher {

    /**
     * {@link VariableAssignment}s
     */
    private List<VariableAssignment> variableAssignments;

    /**
     * Creates a new container Matcher for a given type, with a given value to match against
     * @param type
     *            matcher type
     * @param value
     *            to match against
     * @author mbrunnli (08.04.2014)
     */
    public Matcher(String type, String value) {
        super(type, value);
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
        super(type, value);
        this.variableAssignments =
            variableAssignments == null ? new LinkedList<VariableAssignment>() : variableAssignments;
    }

    /**
     * Returns the variable assignments of the matcher
     * @return the variable assignments of the matcher
     * @author mbrunnli (08.04.2014)
     */
    public List<VariableAssignment> getVariableAssignments() {
        return this.variableAssignments;
    }

}

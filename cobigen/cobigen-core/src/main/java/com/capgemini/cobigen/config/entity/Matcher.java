package com.capgemini.cobigen.config.entity;

import java.util.LinkedList;
import java.util.List;

import com.capgemini.AccumulationType;

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
     * Accumulation type
     */
    private AccumulationType accumulationType;

    /**
     * Creates a new Matcher for a given type, with a given value to match against and the corresponding
     * variable assignments which should be resolvable
     * @param type
     *            matcher type
     * @param value
     *            to match against
     * @param variableAssignments
     *            of the matcher
     * @param accumulationType
     *            of the matcher
     * @author mbrunnli (08.04.2014)
     */
    public Matcher(String type, String value, List<VariableAssignment> variableAssignments,
        AccumulationType accumulationType) {
        super(type, value);
        this.accumulationType = accumulationType;
        this.variableAssignments =
            variableAssignments == null ? new LinkedList<VariableAssignment>() : variableAssignments;
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
     * Returns the field 'accumulationType'
     * @return value of accumulationType
     * @author mbrunnli (22.02.2015)
     */
    public AccumulationType getAccumulationType() {
        return accumulationType;
    }

    /**
     * {@inheritDoc}
     * @author mbrunnli (Jun 17, 2015)
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[type='" + getType() + "'/value='" + getValue() + "'/accuType='"
            + accumulationType.name() + "']";
    }
}

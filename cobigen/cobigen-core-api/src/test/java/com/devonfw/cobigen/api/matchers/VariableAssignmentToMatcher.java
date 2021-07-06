package com.devonfw.cobigen.api.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.devonfw.cobigen.api.to.MatcherTo;
import com.devonfw.cobigen.api.to.VariableAssignmentTo;

/**
 * A hamcrest matcher for providing matching functionality for {@link VariableAssignmentTo}s
 * @author mbrunnli (13.10.2014)
 */
public class VariableAssignmentToMatcher extends BaseMatcher<VariableAssignmentTo> {

    /**
     * Matcher for the type of this variable assignment
     */
    protected Matcher<String> type;

    /**
     * Matcher for the variable's name
     */
    protected Matcher<String> varName;

    /**
     * Matcher for the value
     */
    private Matcher<String> value;

    /**
     * Creates a new {@link VariableAssignmentToMatcher} with the given attribute sub matchers
     * @param typeMatcher
     *            matcher for the type of the variable assignment, interpreted by the plug-ins
     * @param varNameMatcher
     *            matcher for the variable's name
     * @param valueMatcher
     *            matcher for the value
     * @author mbrunnli (15.04.2013)
     */
    public VariableAssignmentToMatcher(Matcher<String> typeMatcher, Matcher<String> varNameMatcher,
        Matcher<String> valueMatcher) {
        type = typeMatcher;
        varName = varNameMatcher;
        value = valueMatcher;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(MatcherTo.class.getSimpleName() + "(type='" + type + "', varName='" + varName
            + "', value='" + value + "')");
    }

    @Override
    public boolean matches(Object item) {
        if (item instanceof VariableAssignmentTo) {
            return type != null && type.matches(((VariableAssignmentTo) item).getType()) && varName != null
                && varName.matches(((VariableAssignmentTo) item).getVarName()) && value != null
                && value.matches(((VariableAssignmentTo) item).getValue());
        }
        return false;
    }

    @Override
    public void describeMismatch(Object item, Description mismatchDescription) {
        if (type == null || value == null || varName == null) {
            mismatchDescription
                .appendText("One of the parameter matcher has been null. Please use AnyOf matchers instead.");
            return;
        }

        VariableAssignmentTo varAssignTo = (VariableAssignmentTo) item;

        mismatchDescription.appendText("VariableAssignmentTo does not match!\nShould be VariableAssignmentTo(");
        type.describeTo(mismatchDescription);
        mismatchDescription.appendText(", ");
        varName.describeTo(mismatchDescription);
        mismatchDescription.appendText(", ");
        value.describeTo(mismatchDescription);
        mismatchDescription.appendText(")\nWas       VariableAssignmentTo('" + varAssignTo.getType() + "', '"
            + varAssignTo.getVarName() + "', '" + varAssignTo.getValue() + "')");
    }

}

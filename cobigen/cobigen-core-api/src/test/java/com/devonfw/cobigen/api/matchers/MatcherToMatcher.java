package com.devonfw.cobigen.api.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.devonfw.cobigen.api.to.MatcherTo;

/**
 * Hamcrest Matcher for {@link MatcherTo}s
 * @author mbrunnli (13.10.2014)
 */
public class MatcherToMatcher extends BaseMatcher<MatcherTo> {

    /**
     * The matchers type to be matched
     */
    private Matcher<String> type;

    /**
     * The value to match against to be matched
     */
    private Matcher<String> value;

    /**
     * The target object to be matched to be matched
     */
    private Matcher<Object> target;

    /**
     * Creates a matcher, which matches all {@link MatcherTo}s with the given attributes
     * @param type
     *            to be matched
     * @param value
     *            to be matched
     * @param target
     *            to be matched
     * @author mbrunnli (13.10.2014)
     */
    public MatcherToMatcher(Matcher<String> type, Matcher<String> value, Matcher<Object> target) {
        this.type = type;
        this.value = value;
        this.target = target;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(
            MatcherTo.class.getSimpleName() + "(type='" + type + "', value='" + value + "', target='" + target + "')");
    }

    @Override
    public boolean matches(Object item) {
        if (item instanceof MatcherTo) {
            return type != null && type.matches(((MatcherTo) item).getType()) && value != null
                && value.matches(((MatcherTo) item).getValue()) && target != null
                && target.matches(((MatcherTo) item).getTarget());
        }
        return false;
    }

    @Override
    public void describeMismatch(Object item, Description mismatchDescription) {
        if (type == null || value == null || target == null) {
            mismatchDescription
                .appendText("One of the parameter matcher has been null. Please use AnyOf matchers instead.");
            return;
        }

        MatcherTo matchedMatcherTo = (MatcherTo) item;

        mismatchDescription.appendText("MatcherTo does not match!\nShould be MatcherTo(");
        type.describeTo(mismatchDescription);
        mismatchDescription.appendText(", ");
        value.describeTo(mismatchDescription);
        mismatchDescription.appendText(", ");
        target.describeTo(mismatchDescription);
        mismatchDescription.appendText(")\nWas       MatcherTo('" + matchedMatcherTo.getType() + "', '"
            + matchedMatcherTo.getValue() + "', '" + matchedMatcherTo.getTarget() + "')");
    }
}

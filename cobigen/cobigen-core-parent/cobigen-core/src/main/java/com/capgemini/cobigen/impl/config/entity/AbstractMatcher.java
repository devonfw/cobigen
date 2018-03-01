package com.capgemini.cobigen.impl.config.entity;

import java.util.Objects;

/** Common declaration of all available matcher types */
public abstract class AbstractMatcher {

    /** Matcher type */
    private String type;

    /** Matcher value to be matched against */
    private String value;

    /**
     * Creates a new {@link Matcher} for a given type, with a given value to match against
     * @param type
     *            matcher type
     * @param value
     *            to match against
     */
    public AbstractMatcher(String type, String value) {
        this.type = type;
        this.value = value;
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

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}

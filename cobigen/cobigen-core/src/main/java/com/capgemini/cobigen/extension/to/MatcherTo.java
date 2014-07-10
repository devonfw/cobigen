/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.extension.to;

/**
 * The transfer object for matchers
 * @author mbrunnli (08.04.2014)
 */
public class MatcherTo {

    /**
     * The matchers type
     */
    protected String type;

    /**
     * The value to match against
     */
    protected String value;

    /**
     * The target object to be matched
     */
    protected Object target;

    /**
     * Creates a new matcher transfer object with the given properties
     * @param type
     *            the matchers type
     * @param value
     *            to match against
     * @param target
     *            object to be matched
     * @author mbrunnli (08.04.2014)
     */
    public MatcherTo(String type, String value, Object target) {
        this.type = type;
        this.value = value;
        this.target = target;
    }

    /**
     * Returns the type
     * @return the type
     * @author mbrunnli (08.04.2014)
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the value to match against
     * @return the value to match against
     * @author mbrunnli (08.04.2014)
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the target to be matched
     * @return the target to be matched
     * @author mbrunnli (08.04.2014)
     */
    public Object getTarget() {
        return target;
    }

}

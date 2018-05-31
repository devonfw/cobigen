package com.capgemini.cobigen.api.to;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (getType() == null ? 0 : getType().hashCode());
        result = prime * result + (getValue() == null ? 0 : getValue().hashCode());
        result = prime * result + (getTarget() == null ? 0 : getTarget().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        if (obj instanceof MatcherTo) {
            boolean equal = true;
            MatcherTo otherIncrement = (MatcherTo) obj;

            if (getType() != null) {
                equal = equal && getType().equals(otherIncrement.getType());
            }
            if (!equal) {
                return false;
            }

            if (getValue() != null) {
                equal = equal && getValue().equals(otherIncrement.getValue());
            }
            if (!equal) {
                return false;
            }

            if (getTarget() != null) {
                equal = equal && getTarget().equals(otherIncrement.getTarget());
            }
            return equal;
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[type='" + getType() + "'/value='" + getValue() + "']";
    }
}

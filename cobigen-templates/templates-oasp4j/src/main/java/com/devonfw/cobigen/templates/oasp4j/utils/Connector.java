package com.devonfw.cobigen.templates.oasp4j.utils;

/**
 * Connector is one association between classes in a class diagram. This class is used for storing that data,
 * for later developing templates.
 */
public class Connector {

    private String className;

    private String multiplicity;

    /**
     * @param className
     * @param multiplicity
     */
    public Connector(String className, String multiplicity) {
        this.className = className;
        this.multiplicity = multiplicity;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(String multiplicity) {
        this.multiplicity = multiplicity;
    }

}

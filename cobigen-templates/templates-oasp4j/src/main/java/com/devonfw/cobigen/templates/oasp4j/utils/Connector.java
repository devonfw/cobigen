package com.devonfw.cobigen.templates.oasp4j.utils;

/**
 * Connector is one association between classes in a class diagram. This class is used for storing that data,
 * for later developing templates.
 */
public class Connector {

    private String counterpartName = "";

    private String counterpartMultiplicity = "";

    private String className;

    private String multiplicity;

    final Boolean ISSOURCE;

    final Boolean ISTARGET;

    /**
     * @param className
     *            The name of the connector
     * @param multiplicity
     *            The multiplicity of the target of this connector
     * @param isSource
     *            True if the connector is the source, false if it is the target
     */
    public Connector(String className, String multiplicity, boolean isSource) {

        this.className = className;
        this.multiplicity = multiplicity;
        ISSOURCE = isSource;
        ISTARGET = !isSource;
    }

    public String getClassName() {

        return className;
    }

    public void setClassName(String className) {

        className = className;
    }

    public String getMultiplicity() {

        return multiplicity;
    }

    public void setMultiplicity(String multiplicity) {

        this.multiplicity = multiplicity;
    }

    public String getCounterpartName() {
        return counterpartName;
    }

    public void setCounterpartName(String counterpartName) {
        this.counterpartName = counterpartName;
    }

    public String getCounterpartMultiplicity() {
        return counterpartMultiplicity;
    }

    public void setCounterpartMultiplicity(String counterpartMuliplicity) {
        counterpartMultiplicity = counterpartMuliplicity;
    }

    @Override
    public String toString() {
        return ISSOURCE + " " + className + " " + multiplicity + " --> " + counterpartName + " "
            + counterpartMultiplicity + " " + ISTARGET;
    }
}

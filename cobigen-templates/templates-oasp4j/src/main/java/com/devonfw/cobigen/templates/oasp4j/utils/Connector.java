package com.devonfw.cobigen.templates.oasp4j.utils;

/**
 * Connector is one association between classes in a class diagram. This class is used for storing that data,
 * for later developing templates.
 */
public class Connector {

    private String className;

    private String multiplicity;

    /**
     * @param source
     *            The source of the connector
     * @param target
     *            The target of the connector
     * @param multiplicity
     *            The multiplicity of the target of this connector
     */
    public Connector(String source, String multiplicity) {
        className = source;
        this.multiplicity = multiplicity;
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

}

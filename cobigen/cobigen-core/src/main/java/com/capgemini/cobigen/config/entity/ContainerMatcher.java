package com.capgemini.cobigen.config.entity;

/**
 * The {@link ContainerMatcher} type corresponds to the &lt;containerMatcher&gt; xml node
 * @author mbrunnli (13.10.2014)
 */
public class ContainerMatcher extends AbstractMatcher {

    /**
     * Creates a new {@link ContainerMatcher} with the given type and the given value to match against
     * @param type
     *            of the matcher
     * @param value
     *            value to be processed by the matcher
     * @author mbrunnli (13.10.2014)
     */
    public ContainerMatcher(String type, String value) {
        super(type, value);
    }

}

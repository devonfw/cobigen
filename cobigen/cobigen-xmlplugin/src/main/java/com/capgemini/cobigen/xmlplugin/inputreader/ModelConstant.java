package com.capgemini.cobigen.xmlplugin.inputreader;

import java.util.List;
import java.util.Map;

/**
 * String constants of the xml object model for generation.
 *
 * @author fkreis (10.11.2014)
 */
public class ModelConstant {

    /**
     * Root element for xml model ({@link Map}&lt;{@link String}, {@link Object}&gt;).
     *
     * @Deprecated use the xml document's root name instead.
     */
    @Deprecated
    public static final String ROOT = "root";

    /**
     * Node for the text content of a xml node (PCDATA)
     */
    public static final String TEXT_CONTENT = "#";

    /**
     * prefix for a single attribute. An attribute will be represented by mapping from the attribute's name to
     * its value ({@link Map}&lt;{@link String}, {@link Object}&gt;)
     */
    public static final String SINGLE_ATTRIBUTE = "@";

    /**
     * A list of all attributes. Each of the SINGLE_ATTRIBUTEs will be provided here as a reference (
     * {@link List}&lt; {@link Map}&lt;{@link String}, {@link Object} &gt;&gt;).
     */
    public static final String ATTRIBUTES = "Attributes";

    /**
     * name of a single child node. An child node will be represented by mapping from the child's name to its
     * model ({@link Map}&lt;{@link String}, {@link Object}&gt;). If two ore more children have the same name
     * they will not provided here as SINGLE_CHILD, but in the list CHILDREN, like all die other children.
     */
    public static final String SINGLE_CHILD = "";

    /**
     * A list of all children. In addition to the SINGLE_CHILDren also the children which do not have an
     * unique name will be provided here ( {@link List}&lt; {@link Map}&lt;{@link String}, {@link Object}
     * &gt;&gt;).
     */
    public static final String CHILDREN = "Children";

}

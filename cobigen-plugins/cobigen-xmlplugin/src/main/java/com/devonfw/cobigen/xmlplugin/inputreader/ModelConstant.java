package com.devonfw.cobigen.xmlplugin.inputreader;

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
     * @deprecated use the xml document's root name instead.
     */
    @Deprecated
    public static final String ROOT = "root";

    /**
     * The node's name represented as {@link String}.
     */
    public static final String NODE_NAME = "_nodeName_";

    /**
     * Node for the concatenated text content of a xml node (PCDATA)
     */
    public static final String TEXT_CONTENT = "_text_";

    /**
     * A List of all children of type {@code TEXT_NODE}.
     */
    public static final String TEXT_NODES = "TextNodes";

    /**
     * prefix for a single attribute. An attribute will be represented by mapping from the attribute's name to
     * its value ({@link Map}&lt;{@link String}, {@link Object}&gt;)
     */
    public static final String SINGLE_ATTRIBUTE = "_at_";

    /**
     * A list of all attributes. Each of the SINGLE_ATTRIBUTEs will be provided here as a reference (
     * {@link List}&lt; {@link Map}&lt;{@link String}, {@link Object} &gt;&gt;).
     */
    public static final String ATTRIBUTES = "Attributes";

    /**
     * The attribute's name represented as {@link String}.
     */
    public static final String ATTRIBUTE_NAME = "_attName_";

    /**
     * The attribute's value represented as {@link String}.
     */
    public static final String ATTRIBUTE_VALUE = "_attValue_";

    /**
     * prefix for a single child node. An child node will be represented by mapping from the child's name to
     * its model ({@link Map}&lt;{@link String}, {@link Object}&gt;). If two ore more children have the same
     * name they will not provided here as SINGLE_CHILD, but in the list CHILDREN, like all die other
     * children.
     */
    public static final String SINGLE_CHILD = "";

    /**
     * A list of all children. In addition to the SINGLE_CHILDren also the children which do not have an
     * unique name will be provided here ( {@link List}&lt; {@link Map}&lt;{@link String}, {@link Object}
     * &gt;&gt;).
     */
    public static final String CHILDREN = "Children";

    /** Model key constant "doc" */
    static final String ROOT_DOC = "doc";

    /** Model key constant for "elemDoc" representing the partial document in a container */
    public static final String ROOT_ELEMDOC = "elemDoc";

}

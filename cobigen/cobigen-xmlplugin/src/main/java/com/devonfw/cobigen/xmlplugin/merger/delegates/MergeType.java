package com.capgemini.cobigen.xmlplugin.merger.delegates;

import com.github.maybeec.lexeme.ConflictHandlingType;

/**
 * Maps the ConflictHandlingTypes and the already used merge types
 * @author sholzer (Dec 22, 2015)
 */
public enum MergeType {

    /**
     * In case of a conflict the patch document is preferred
     */
    PATCHOVERWRITE("xmlmerge_override", ConflictHandlingType.PATCHOVERWRITE),
    /**
     * In case of a conflict the base document is preferred
     */
    BASEOVERWRITE("xmlmerge", ConflictHandlingType.BASEOVERWRITE),
    /**
     * In case of a conflict the base document is preferred. Attributes and text nodes will be attached where
     * possible
     */
    BASEATTACHOROVERWRITE("xmlmerge_attachTexts", ConflictHandlingType.BASEATTACHOROVERWRITE),
    /**
     * In case of a conflict the patch document is preferred. Attributes and text nodes will be attached where
     * possible
     */
    PATCHATTACHOROVERWRITE("xmlmerge_override_attachTexts", ConflictHandlingType.PATCHATTACHOROVERWRITE);

    /**
     * The ConflictHandlingType
     */
    public ConflictHandlingType type;

    /**
     * The name of the merge mode
     */
    public String value;

    /**
     *
     * @param value
     *            the name of the merge mode
     * @param type
     *            the ConflictHandlingType
     * @author sholzer (Dec 22, 2015)
     */
    private MergeType(String value, ConflictHandlingType type) {
        this.type = type;
        this.value = value;
    }

    /**
     * Returns the field 'type'
     * @return value of type
     * @author sholzer (Dec 22, 2015)
     */
    public ConflictHandlingType getType() {
        return type;
    }

    /**
     * Returns the field 'value'
     * @return value of value
     * @author sholzer (Dec 22, 2015)
     */
    public String getValue() {
        return value;
    }

}

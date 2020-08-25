package com.devonfw.cobigen.xmlplugin.merger.delegates;

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
    PATCHATTACHOROVERWRITE("xmlmerge_override_attachTexts", ConflictHandlingType.PATCHATTACHOROVERWRITE),
    /**
     * In case of a conflict the patch document is preferred. Validation will be enabled.
     */
    PATCHOVERWRITEVALIDATE("xmlmerge_override_validate", ConflictHandlingType.PATCHOVERWRITEVALIDATE),
    /**
     * In case of a conflict the base document is preferred. Validation will be enabled.
     */
    BASEOVERWRITEVALIDATE("xmlmerge_validate", ConflictHandlingType.BASEOVERWRITEVALIDATE),
    /**
     * In case of a conflict the base document is preferred. Attributes and text nodes will be attached where
     * possible. Validation will be enabled.
     */
    BASEATTACHOROVERWRITEVALIDATE("xmlmerge_attachTexts_validate", ConflictHandlingType.BASEATTACHOROVERWRITEVALIDATE),
    /**
     * In case of a conflict the patch document is preferred. Attributes and text nodes will be attached where
     * possible. Validation will be enabled.
     */
    PATCHATTACHOROVERWRITEVALIDATE("xmlmerge_override_attachTexts_validate",
        ConflictHandlingType.PATCHATTACHOROVERWRITEVALIDATE);

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

package com.devonfw.cobigen.textmerger.anchorextension;

/**
 *
 */
public class Anchor {
    /**
     * The merge strategy of the anchor
     */
    private MergeStrategy mergeStrat;

    /**
     * The documentpart of the anchor
     */
    private String docPart;

    /**
     * The front of the anchor, ideally should be comment declaration
     */
    private String front;

    /**
     * True if mergestrategy contains newline, false if not
     */
    private final boolean hasNewline;

    /**
     * True if the mergeStrategy has a newline before it, false if after, false if newline is not supported
     */
    private final boolean newlineBefore;

    /**
     * @param front
     *            Most likely comment declaration, but in general everything before the anchor itself
     * @param mergeStrat
     *            The merge strategy of the anchor
     * @param docPart
     *            The documentpart of the anchor
     * @param hasNewline
     *            Whether the mergestrategy has newline
     * @param newlineBefore
     *            Whether the mergestrategy has newline before or after it
     */
    public Anchor(String front, String docPart, MergeStrategy mergeStrat, boolean hasNewline, boolean newlineBefore) {
        if (docPart == null) {
            docPart = "";
        }
        if (mergeStrat == null) {
            mergeStrat = MergeStrategy.ERROR;
        }
        if (mergeStrat.supportsNewLine()) {
            this.hasNewline = hasNewline;
            this.newlineBefore = newlineBefore;
        } else {
            this.hasNewline = false;
            this.newlineBefore = false;
        }
        this.front = front;
        this.mergeStrat = mergeStrat;
        this.docPart = docPart;
    }

    /**
     * @return the merge strategy of this anchor
     */
    public MergeStrategy getMergeStrat() {
        return mergeStrat;
    }

    /**
     * @return the documentpart of this anchor
     */
    public String getDocPart() {
        return docPart;
    }

    /**
     * @return The full text of this anchor
     */
    public String getAnchor() {
        return front + "anchor:" + docPart + ":" + getNewlineName() + ":anchorend";
    }

    @Override
    public String toString() {
        return getAnchor();
    }

    /**
     * @return Simply the name of the mergestrategy if it doesn't support newline, otherwise the mergestrategy
     *         with newline before or after, depending on the anchor
     */
    public String getNewlineName() {
        if (hasNewline) {
            if (newlineBefore) {
                return "newline_" + mergeStrat.getName();
            } else {
                return mergeStrat.getName() + "_newline";
            }
        } else {
            return mergeStrat.getName();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Anchor)) {
            return false;
        } else if (getAnchor().equals(((Anchor) obj).getAnchor())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 17 * front.hashCode() + mergeStrat.hashCode() + docPart.hashCode();
    }
}

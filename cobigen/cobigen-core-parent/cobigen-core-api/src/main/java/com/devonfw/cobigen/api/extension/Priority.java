package com.devonfw.cobigen.api.extension;

/**
 * Priorities an input reader is ranked with in case
 * {@link InputReader#isMostLikelyReadable(java.nio.file.Path)} is returning true for multiple plug-ins
 * available.
 */
public enum Priority {

    /** Standard devonfw plug-ins + meta-language readers (i.e. XML) */
    LOW((byte) 3),

    /** For example language specific readers like specific XML languages */
    MEDIUM((byte) 2),

    /** Highest priority for custom use cases */
    HIGH((byte) 1);

    /** The rank */
    private byte rank;

    /**
     * @param rank
     *            the rank
     */
    private Priority(byte rank) {
        this.rank = rank;
    }

    /**
     * @return the rank for sorting the priorities
     */
    public byte getRank() {
        return rank;
    }
}

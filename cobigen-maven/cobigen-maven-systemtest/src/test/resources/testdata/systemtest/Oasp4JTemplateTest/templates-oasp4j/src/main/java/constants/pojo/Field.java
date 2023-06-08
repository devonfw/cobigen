package constants.pojo;

/**
 * Contains the used keys for the pojo field Map&lt;String, Object>
 */
public enum Field {

    /**
     * Name of the field
     */
    NAME("name"),
    /**
     * Type of the field
     */
    TYPE("type"),
    /**
     * Canonical Type of the field
     */
    CANONICAL_TYPE("canonicalType"),
    /**
     * The Javadoc of the field
     */
    JAVA_DOC("javaDoc"),
    /**
     * Annotations
     */
    ANNOTATIONS("annotations");

    /**
     * key value
     */
    @SuppressWarnings("unused")
    private String value;

    /**
     * @param value
     *            of the key
     */
    Field(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}

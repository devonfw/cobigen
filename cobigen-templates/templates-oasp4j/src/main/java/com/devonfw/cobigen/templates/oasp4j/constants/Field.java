package com.devonfw.cobigen.templates.oasp4j.constants;

/**
 * Contains the used keys for the pojo field mapping
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
    private String value;

    public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

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
    
    public static void main(String arg[]) {
    	Field.NAME.setValue("hello");
    	System.out.println("name= "+ Field.NAME.getValue() );
    }

}

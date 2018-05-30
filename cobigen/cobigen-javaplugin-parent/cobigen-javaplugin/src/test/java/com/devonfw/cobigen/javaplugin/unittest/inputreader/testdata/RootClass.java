package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

/**
 *
 * @author mbrunnli (14.11.2014)
 */
public class RootClass extends SuperClass1 {

    private int integer;

    private String value;

    /**
     * Returns the field 'value'
     * @return value of value
     * @author mbrunnli (14.11.2014)
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the field 'value'.
     * @param value
     *            new value of value
     * @author mbrunnli (14.11.2014)
     */
    public void setValue(String value) {
        this.value = value;
    }

}

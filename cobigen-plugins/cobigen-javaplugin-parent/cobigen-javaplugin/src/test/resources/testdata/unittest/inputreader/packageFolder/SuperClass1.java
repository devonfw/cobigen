package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

/**
 *
 * @author mbrunnli (14.11.2014)
 */
public abstract class SuperClass1 extends SuperClass2 {

    private String superClass1Field;

    protected int packageVisibleInteger;

    private byte setterVisibleByte;

    /**
     * Returns the field 'setterVisibleByte'
     * @return value of setterVisibleByte
     * @author mbrunnli (14.11.2014)
     */
    public byte getSetterVisibleByte() {
        return setterVisibleByte;
    }

    /**
     * Sets the field 'setterVisibleByte'.
     * @param setterVisibleByte
     *            new value of setterVisibleByte
     * @author mbrunnli (14.11.2014)
     */
    public void setSetterVisibleByte(byte setterVisibleByte) {
        this.setterVisibleByte = setterVisibleByte;
    }

    public void setNoProperty(String noProperty) {

    }

    /**
     *
     * @author mbrunnli (17.11.2014)
     */
    private String getNoProperty() {
        return null;
    }

}

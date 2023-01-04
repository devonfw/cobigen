package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

public abstract class SuperClass1 extends SuperClass2 {

    private String superClass1Field;

    protected int packageVisibleInteger;

    private byte setterVisibleByte;

    /**
     * Returns the field 'setterVisibleByte'
     * @return value of setterVisibleByte
     */
    public byte getSetterVisibleByte() {
        return setterVisibleByte;
    }

    /**
     * Sets the field 'setterVisibleByte'.
     * @param setterVisibleByte
     *            new value of setterVisibleByte
     */
    public void setSetterVisibleByte(byte setterVisibleByte) {
        this.setterVisibleByte = setterVisibleByte;
    }

    public void setNoProperty(String noProperty) {

    }

    private String getNoProperty() {
        return null;
    }

}

package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

import jakarta.xml.bind.annotation.XmlAnyElement;

public class TestClassWithAnnotations {

    private boolean boolvalue;

    /**
     * Returns the field 'boolvalue'
     * @return value of boolvalue
     */
    @XmlAnyElement(lax = true)
    public boolean isBoolvalue() {
        return boolvalue;
    }

    /**
     * Sets the field 'boolvalue'.
     * @param boolvalue
     *            new value of boolvalue
     */
    public void setBoolvalue(boolean boolvalue) {
        this.boolvalue = boolvalue;
    }

}

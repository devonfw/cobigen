package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

import jakarta.xml.bind.annotation.XmlAnyElement;

public class TestClassWithAnnotations {

    private boolean boolvalue;

    /**
     * Returns the field 'boolvalue'
     * @return value of boolvalue
     * @author mbrunnli (05.12.2014)
     */
    @XmlAnyElement(lax = true)
    public boolean isBoolvalue() {
        return boolvalue;
    }

    /**
     * Sets the field 'boolvalue'.
     * @param boolvalue
     *            new value of boolvalue
     * @author mbrunnli (05.12.2014)
     */
    public void setBoolvalue(boolean boolvalue) {
        this.boolvalue = boolvalue;
    }

}

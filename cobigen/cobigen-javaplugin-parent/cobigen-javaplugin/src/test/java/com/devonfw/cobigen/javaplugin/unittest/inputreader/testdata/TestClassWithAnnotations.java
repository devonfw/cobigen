package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

import java.beans.BeanProperty;

public class TestClassWithAnnotations {

    private boolean boolvalue;

    /**
     * Returns the field 'boolvalue'
     * @return value of boolvalue
     */
    @BeanProperty(bound = true)
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

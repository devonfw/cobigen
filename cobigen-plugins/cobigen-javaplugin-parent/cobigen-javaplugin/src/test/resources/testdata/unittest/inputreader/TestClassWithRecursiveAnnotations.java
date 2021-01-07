package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;

public class TestClassWithRecursiveAnnotations {

    private boolean boolvalue;

    /**
     * Returns the field 'boolvalue'
     * @return value of boolvalue
     * @author mbrunnli (05.12.2014)
     */
    @Action(fault = { @FaultAction(className = NullPointerException.class),
        @FaultAction(className = IndexOutOfBoundsException.class) })
    public boolean isBoolvalue() {
        return boolvalue;
    }

    /**
     * Sets the field 'boolvalue'.
     * @param boolvalue
     *            new value of boolvalue
     * @author mbrunnli (05.12.2014)
     */
    @Action(fault =  @FaultAction(className = NullPointerException.class))
    public void setBoolvalue(boolean boolvalue) {
        this.boolvalue = boolvalue;
    }

}

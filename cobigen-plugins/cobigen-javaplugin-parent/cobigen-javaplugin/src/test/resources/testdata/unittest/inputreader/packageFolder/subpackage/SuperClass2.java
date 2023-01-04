package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.subpackage;

import java.util.List;

import com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata.RootClass;

public class SuperClass2 {

    private List<RootClass> genericAccessible;

    /**
     * Returns the field 'genericAccessible'
     * @return value of genericAccessible
     */
    public List<RootClass> getGenericAccessible() {
        return genericAccessible;
    }

    /**
     * Sets the field 'genericAccessible'.
     * @param genericAccessible
     *            new value of genericAccessible
     */
    public void setGenericAccessible(List<RootClass> genericAccessible) {
        this.genericAccessible = genericAccessible;
    }

}

package com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata;

import java.util.List;

public class TestClass extends AbstractTestClass implements TestInterface1, TestInterface2 {

    private List<String> customList;

    public List<String> getCustomList() {

        return customList;
    }

    public void setCustomList(List<String> customList) {

        this.customList = customList;
    }

    /**
     * {@inheritDoc}
     * @author fkreis (25.09.2014)
     */
    @Override
    public void interface1Method() {
        return;
    }

}

package com.capgemini.cobigen.javaplugin.inputreader.testdata;

import java.util.List;

public class TestClass extends AbstractTestClass implements TestInterface1, TestInterface2{

    private List<String> customList;

    public List<String> getCustomList() {

        return customList;
    }

    public void setCustomList(List<String> customList) {

        this.customList = customList;
    }

}

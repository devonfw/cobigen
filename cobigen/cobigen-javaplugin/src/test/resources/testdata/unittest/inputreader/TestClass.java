package com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata;

import java.io.FileNotFoundException;
import java.util.List;

import javax.annotation.Generated;
import javax.xml.ws.Action;

public class TestClass extends AbstractTestClass implements TestInterface1, TestInterface2{

    /**
     * Example JavaDoc
     */
    @Deprecated
    private List<String> customList;

    @Generated
    public List<String> getCustomList() {

        return customList;
    }

    @SafeVarargs
    public boolean isCustomList(){
        return true;
    }

    @Action
    public void setCustomList(List<String> customList) {

        this.customList = customList;
    }

}

package com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class TestClass extends AbstractTestClass implements TestInterface1, TestInterface2 {

    /**
     * Example JavaDoc
     */
    @MyFieldAnnotation
    private List<String> customList;

    @MyGetterAnnotation
    public List<String> getCustomList() {

        return customList;
    }

    @MyIsAnnotation
    public boolean isCustomList() {
        return false;
    }

    @MySetterAnnotation
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

// simple annotation types which are still available at runtime
@Retention(RetentionPolicy.RUNTIME)
@interface MyFieldAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MyGetterAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MySetterAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MyIsAnnotation {
}

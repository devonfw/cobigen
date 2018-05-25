package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class TestClass extends AbstractTestClass implements TestInterface1, TestInterface2 {

    /**
     * Example JavaDoc
     */
    @MyFieldAnnotation(b = (byte) 0, s = (short) 1, i = 2, l = 3, f = 4, d = 5, c = 'c', bool = true,
        str = "TestString")
    private List<String> customList;

    @MyGetterAnnotation
    public List<String> getCustomList() {

        return customList;
    }

    @MyIsAnnotation
    public boolean isCustomList() {
        return true;
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

// Field Annotation see own file, because its a public annotation

@Retention(RetentionPolicy.RUNTIME)
@interface MyGetterAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MySetterAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MyIsAnnotation {
}

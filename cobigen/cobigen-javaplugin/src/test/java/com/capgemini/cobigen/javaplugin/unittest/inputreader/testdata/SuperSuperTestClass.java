package com.capgemini.cobigen.javaplugin.unittest.inputreader.testdata;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SuperSuperTestClass {

    /**
     * Example JavaDoc
     */
    @MySuperSuperTypeFieldAnnotation
    private String superSuperString;

    @MySuperSuperTypeGetterAnnotation
    public String getSuperSuperString() {

        return superSuperString;
    }

    @MySuperSuperTypeIsAnnotation
    public String isSuperSuperString() {
        return superSuperString;
    }

    @MySuperSuperTypeSetterAnnotation
    public void setSuperSuperString(String superSuperString) {

        this.superSuperString = superSuperString;
    }

}

// simple annotation types which are still available at runtime
@Retention(RetentionPolicy.RUNTIME)
@interface MySuperSuperTypeFieldAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MySuperSuperTypeGetterAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MySuperSuperTypeSetterAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MySuperSuperTypeIsAnnotation {
}

package com.devonfw.cobigen.javaplugin.unittest.inputreader.testdata;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SuperSuperTestClass {

    /**
     * Example JavaDoc
     */
    @MySuperSuperTypeFieldAnnotation(b = (byte) 0, s = (short) 1, i = 2, l = 3, f = 4, d = 5, c = 'c', bool = true,
        str = "TestString")
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
// the punblic @interface MySuperSuperTypeFieldAnnotation has its own file now

@Retention(RetentionPolicy.RUNTIME)
@interface MySuperSuperTypeGetterAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MySuperSuperTypeSetterAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MySuperSuperTypeIsAnnotation {
}

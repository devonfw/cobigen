package com.devonfw;

public class FooBar
{

    enum BaseEnum {
        PATCH_VALUE
    }

    enum PatchEnum {
        PATCH_VALUE
    }

    private class InnerBaseClass {

        private int innerBaseField = 1;
        private int innerPatchField = 0;

        enum InnerBaseEnum {
            INNER_PATCH_VALUE
        }


        String innerBaseMethod() {
            return null;
        }

        String innerPatchMethod(String string) {
            return null;
        }
    }

}

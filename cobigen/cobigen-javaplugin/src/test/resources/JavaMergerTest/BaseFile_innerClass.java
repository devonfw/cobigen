/*******************************************************************************
 * Copyright © Capgemini 2013. All rights reserved.
 ******************************************************************************/
package com.capgemini;

public class FooBar
{
    
    enum BaseEnum {
        BASE_VALUE
    }
    
    private class InnerBaseClass {
        
        private int innerBaseField = 0;
        
        enum InnerBaseEnum {
            INNER_BASE_VALUE
        }
        

        void innerBaseMethod() {
            
        }
    }
    
}

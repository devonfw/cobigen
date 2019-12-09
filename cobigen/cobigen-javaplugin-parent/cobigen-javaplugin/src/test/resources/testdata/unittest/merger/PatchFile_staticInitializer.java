package com.devonfw;

/**
 * 
 */
public class A {
    
    private static int i;
    static {
        i = 1;
    }
    
    private static int j;
    
    static {
        
        j=1;
    }
    
    {
            System.out.println("Object Initialization Block"); 
    }
    
    public void patchFileMethod(){
        
    }

}

package com.devonfw;

@Produces("application/xml")
@Consumes("application/json")
public class test{
    
    @GET
    public void test(@NotNull @max(100) int test){
        
    }
    
    @GET
    public void test2(){
        
    }
}
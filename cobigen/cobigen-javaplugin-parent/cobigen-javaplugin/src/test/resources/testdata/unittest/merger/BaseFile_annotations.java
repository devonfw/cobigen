package com.devonfw;

@Produces("application/json")
@Consumes("application/json")
public class test{
    
    @GET
    public void test(@max(200) int test){
        
    }
    
    @POST
    public void test2(){
        
    }
}
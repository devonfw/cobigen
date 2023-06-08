package com.devonfw.cobigen.javaplugin.integrationtest;

import com.devonfw.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;
import javax.ws.rs.Get;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;

public class ModelCreationTest extends AbstractIntegrationTest {

    private List<String> testField;

    @GET
    @Path("/foo/{id}/")
    public FooEto findFoo(@PathParam("id") long id) {}
}

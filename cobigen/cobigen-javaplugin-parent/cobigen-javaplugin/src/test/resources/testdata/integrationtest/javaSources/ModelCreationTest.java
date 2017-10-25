package com.capgemini.cobigen.javaplugin.integrationtest;

import com.capgemini.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;
import javax.ws.rs.Get;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;

/**
 *
 * @author mbrunnli (22.01.2015)
 */
public class ModelCreationTest extends AbstractIntegrationTest {

    private List<String> testField;

    @GET
    @Path("/foo/{id}/")
    public FooEto findFoo(@PathParam("id") long id) {}
}

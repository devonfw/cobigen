package com.capgemini.cobigen.maven.systemtest;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

import com.capgemini.cobigen.maven.GenerateMojo;

/**
 * Test suite for {@link GenerateMojo}
 * @author mbrunnli (16.02.2015)
 */
public class GenerateMojoTest extends AbstractMojoTestCase {

    /**
     *
     * @throws Exception
     *             test fails
     * @author mbrunnli (16.02.2015)
     */
    @Test
    public void testMojoPackageInputRetrieval() throws Exception {
        File testPom = new File("src/test/resources/testdata/systemtest/GenerateMojoTest/pom.xml");
        GenerateMojo mojo = (GenerateMojo) lookupMojo("generate", testPom);

        assertThat(mojo, notNullValue());
    }
}

package com.capgemini.cobigen.maven.systemtest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Ignore;
import org.junit.Test;

import com.capgemini.cobigen.maven.GenerateMojo;

/**
 * Test suite for {@link GenerateMojo}
 * @author mbrunnli (16.02.2015)
 */
@Ignore("TODO ...")
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

        Method method = mojo.getClass().getDeclaredMethod("collectInputs");
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Object> inputObjects = (List<Object>) method.invoke(mojo);

        // TODO solve project==null problem:
        // http://stackoverflow.com/questions/15512404/unit-testing-maven-mojo-components-and-parameters-are-null

        assertThat(inputObjects.size(), equalTo(1));
    }
}

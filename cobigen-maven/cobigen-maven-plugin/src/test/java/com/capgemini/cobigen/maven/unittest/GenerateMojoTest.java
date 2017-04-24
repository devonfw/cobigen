package com.capgemini.cobigen.maven.unittest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import com.capgemini.cobigen.maven.GenerateMojo;
import com.capgemini.cobigen.maven.common.AbstractMavenTest;

/** Test suite for {@link GenerateMojo} */
public class GenerateMojoTest extends AbstractMavenTest {

    /**
     * Tests the correct package input retrieval for source code.
     * @throws Exception
     *             test fails
     */
    @Test
    public void testMojoPackageInputRetrieval() throws Exception {
        File testPom = new File("src/test/resources/testdata/unittest/GenerateMojoTest/pom.xml");
        GenerateMojo mojo = getGenerateMojo(testPom);
        assertThat(mojo, notNullValue());

        Method method = mojo.getClass().getDeclaredMethod("collectInputs");
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Object> inputObjects = (List<Object>) method.invoke(mojo);
        assertThat(inputObjects.size(), equalTo(1));
    }

    /**
     * Tests the test scope class path resolution
     * @throws Exception
     *             test fails
     */
    @Test
    public void testResolveTestScopeClasspathResources() throws Exception {
        File testPom = new File("src/test/resources/testdata/unittest/GenerateMojoTest/pom.xml");
        GenerateMojo mojo = getGenerateMojo(testPom);

        Method method = mojo.getClass().getDeclaredMethod("getProjectClassLoader");
        method.setAccessible(true);
        ClassLoader classLoader = (ClassLoader) method.invoke(mojo);

        // Assert: should not throw a ClassNotFoundException as this class is included in the cobigen-core
        // test scope
        classLoader.loadClass("org.custommonkey.xmlunit.XMLTestCase");

    }
}

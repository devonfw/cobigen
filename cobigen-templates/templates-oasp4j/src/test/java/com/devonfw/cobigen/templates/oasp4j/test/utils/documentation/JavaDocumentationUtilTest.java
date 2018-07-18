package com.devonfw.cobigen.templates.oasp4j.test.utils.documentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.devonfw.cobigen.templates.oasp4j.test.utils.resources.TestClass;
import com.devonfw.cobigen.templates.oasp4j.utils.documentation.JavaDocumentationUtil;

/**
 *
 */
public class JavaDocumentationUtilTest {

    private static Class<?> clazz;

    @BeforeClass
    public static void beforeAll() {

        clazz = new TestClass().getClass();
    }

    /**
     * tests if {\@link} tags are properly stripped
     */
    @Test
    public void testGetJavaDocWithoutLink() {
        assertEquals(new JavaDocumentationUtil().getJavaDocWithoutLink("{@link id}"), "id");
        assertEquals(new JavaDocumentationUtil().getJavaDocWithoutLink("{@sink id}"), "{@sink id}");
        assertEquals(new JavaDocumentationUtil().getJavaDocWithoutLink("id"), "id");
    }

    /**
     * tests if a non-existing application.properties file causes getting a rootpath to throw an exception
     * @throws IOException
     */
    @Test(expected = IOException.class)
    public void hasRootPathTest() throws IOException {
        // assume
        try (InputStream stream = clazz.getClassLoader().getResourceAsStream("application.properties")) {
            Assume.assumeTrue("application.properties exists in classpath: "
                + clazz.getClassLoader().getResource("application.properties").getPath(), stream == null);
        }
        assertFalse(new JavaDocumentationUtil().extractRootPath(clazz).equals("This is not a root path!"));
        assertEquals(new JavaDocumentationUtil().extractRootPath(clazz), "http://localhost:8080/");
    }
}

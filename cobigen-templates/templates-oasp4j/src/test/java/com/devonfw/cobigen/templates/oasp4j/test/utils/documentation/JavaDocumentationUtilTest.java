package com.devonfw.cobigen.templates.oasp4j.test.utils.documentation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;

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
        assertThat(new JavaDocumentationUtil().getJavaDocWithoutLink("{@link id}")).isEqualTo("id");
        assertThat(new JavaDocumentationUtil().getJavaDocWithoutLink("{@sink id}")).isEqualTo("{@sink id}");
        assertThat(new JavaDocumentationUtil().getJavaDocWithoutLink("id")).isEqualTo("id");
    }

    /**
     * tests if a non-existing application.properties file causes getting a rootpath to throw an exception
     * @throws IOException
     */
    @Test(expected = IOException.class)
    public void getPathTest() throws IOException {
        // assume
        try (InputStream stream = clazz.getClassLoader().getResourceAsStream("application.properties")) {
            Assume.assumeTrue("application.properties exists in classpath: "
                + clazz.getClassLoader().getResource("application.properties").getPath(), stream == null);
        }
        Map<String, Object> pojo = new HashMap<>();

        assertThat(new JavaDocumentationUtil().getPath(pojo)).isEqualTo("http://localhost:8080/");
    }

    /**
     *
     */
    @Test
    public void testGetRequestType() {
        Map<String, Object> annotationsJavax = new HashMap<>();
        GET get = new GET() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }
        };
        annotationsJavax.put("javax_ws_rs_GET", get);
        assertThat(new JavaDocumentationUtil().getRequestType(annotationsJavax)).isEqualTo("GET");

        Map<String, Object> annotationsSpring = new HashMap<>();
        Map<String, Object> annotationValues = new HashMap<>();
        annotationValues.put("method", "requestmethod.get");
        annotationsSpring.put("org_springframework_web_bind_annotation_RequestMapping", annotationValues);
        assertThat(new JavaDocumentationUtil().getRequestType(annotationsSpring)).isEqualTo("GET");
    }
}

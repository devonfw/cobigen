package com.devonfw.cobigen.templates.devon4j.test.utils.documentation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;

import org.junit.Test;

import com.devonfw.cobigen.templates.devon4j.utils.documentation.JavaDocumentationUtil;

/**
 *
 */
public class JavaDocumentationUtilTest {

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
   * Tests that port is read from application.properties and if not set defaults to localhost
   *
   * @throws IOException test fails
   */
  @Test
  public void getPathTest() throws IOException {

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

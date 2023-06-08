package com.devonfw.cobigen.templates.devon4j.test.utils.documentation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;

import org.junit.Test;

import com.devonfw.cobigen.templates.devon4j.utils.documentation.OpenApiDocumentationUtil;

/**
 *
 */
public class OpenApiDocumentationUtilTest {

  @Test
  public void testGetParam() {

    HashMap<String, Object> queryParam = new HashMap<>();
    queryParam.put("inQuery", true);
    queryParam.put("inPath", false);
    queryParam.put("type", "int");

    HashMap<String, Object> pathParam = new HashMap<>();
    pathParam.put("inQuery", false);
    pathParam.put("inPath", true);
    pathParam.put("type", "String");

    assertThat(new OpenApiDocumentationUtil().getParam(queryParam)).isEqualTo("?int");
    assertThat(new OpenApiDocumentationUtil().getParam(pathParam)).isEqualTo("{String}");
  }

  @Test
  public void testGetConstraintList() {

    HashMap<String, Object> sampleParam = new HashMap<>();
    HashMap<String, Object> constraints = new HashMap<>();
    constraints.put("notNull", true);
    constraints.put("max", 200);
    sampleParam.put("constraints", constraints);

    assertThat(new OpenApiDocumentationUtil().getConstraintList(sampleParam)).isEqualTo(//
        "[red]#__Required__# +" //
            + System.lineSeparator() + "max = 200 +" //
            + System.lineSeparator());//
  }

}

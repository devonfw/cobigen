package com.devonfw.cobigen.tempeng.freemarker.unittest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.api.extension.TextTemplate;
import com.devonfw.cobigen.tempeng.freemarker.FreeMarkerTemplateEngine;

/** Test suite for {@link FreeMarkerTemplateEngine} */
public class FreeMarkerTemplateEngineTest {

  /** Root path of the test resources for this test suite */
  private static final String testFileRootPath = "src/test/resources/unittest/FreeMarkerTemplateEngineTest/";

  /**
   * Tests a basic FreeMarker generation
   */
  @Test
  public void testBasicGeneration() {

    // arrange
    final File templateFolder = new File(testFileRootPath + "basicGeneration/").getAbsoluteFile();
    TextTemplate template = new TextTemplate() {
      @Override
      public String getRelativeTemplatePath() {

        return "template.ftl";
      }

      @Override
      public Path getAbsoluteTemplatePath() {

        return templateFolder.toPath().resolve("template.ftl");
      }
    };
    HashMap<String, Object> model = new HashMap<>();
    List<Object> fields = new ArrayList<>();
    HashMap<Object, Object> fieldAttr = new HashMap<>();
    fieldAttr.put("type", "A");
    fields.add(fieldAttr);
    fieldAttr = new HashMap<>();
    fieldAttr.put("type", "B");
    fields.add(fieldAttr);
    fieldAttr = new HashMap<>();
    fieldAttr.put("type", "C");
    fields.add(fieldAttr);
    HashMap<String, Object> fieldsAccessor = new HashMap<>();
    fieldsAccessor.put("fields", fields);
    model.put("pojo", fieldsAccessor);

    // act
    StringWriter out = new StringWriter();
    FreeMarkerTemplateEngine templateEngine = new FreeMarkerTemplateEngine();
    templateEngine.setTemplateFolder(templateFolder.toPath());
    templateEngine.process(template, model, out, "UTF-8");

    // assert
    assertThat(out).hasToString("A,B,C,");
  }
}

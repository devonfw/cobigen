package com.devonfw.cobigen.tempeng.agnostic.unittest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.devonfw.cobigen.api.extension.TextTemplate;
import com.devonfw.cobigen.tempeng.agnostic.AgnosticTemplateEngine;

/**
 * Test of {@link AgnosticTemplateEngine}.
 */
public class AgnosticTemplateEngineTest {

  /**
   * Test subject
   */
  private AgnosticTemplateEngine engine;

  /**
   * @throws java.lang.Exception if something unexpected happens
   */
  @Before
  public void setUpBefore() throws Exception {

    this.engine = new AgnosticTemplateEngine();
  }

  /**
   * Tests a basic velocity generation. Test design used from freemarker plugin
   */
  @Test
  public void testProcess() {

    // arrange
    final Path templateFolder = Paths.get("src/test/java/").toAbsolutePath();
    TextTemplate template = new TextTemplate() {
      @Override
      public String getRelativeTemplatePath() {

        return "x_rootpackage_x/x_component_x/logic/UcFindX_EntityName_X.java";
      }

      @Override
      public Path getAbsoluteTemplatePath() {

        return templateFolder.resolve(getRelativeTemplatePath());
      }
    };
    HashMap<String, Object> model = new HashMap<>();
    model.put("rootpackage", "com.customer.app");
    model.put("component", "mycomponent");
    model.put("entityName", "MyExample");

    // act
    StringWriter out = new StringWriter();
    this.engine.process(template, model, out, "UTF-8");

    // assert
    assertThat(out.toString()).isEqualTo("package com.customer.app.mycomponent.logic;\n" //
        + "\n" //
        + "import java.util.Optional;\n" //
        + "\n" //
        + "import javax.inject.Named;\n" //
        + "import javax.transaction.Transactional;\n" //
        + "\n" //
        + "import org.slf4j.Logger;\n" //
        + "import org.slf4j.LoggerFactory;\n" //
        + "\n" //
        + "import com.customer.app.mycomponent.common.MyExample;\n" //
        + "import com.customer.app.mycomponent.common.MyExampleEto;\n" //
        + "import com.customer.app.mycomponent.dataaccess.MyExampleEntity;\n" //
        + "\n" //
        + "/**\n" //
        + " * Use-case to find instances of {@link MyExample}.\n" //
        + " */\n" //
        + "@Named\n" //
        + "@Transactional\n" //
        + "public class UcFindMyExample extends AbstractUcMyExample {\n" //
        + "\n" //
        + "  /** Logger instance. */\n" //
        + "  private static final Logger LOG = LoggerFactory.getLogger(UcFindMyExample.class);\n" //
        + "\n" //
        + "  /**\n" //
        + "   * @param id the {@link MyExampleEntity#getId() primary key} of the requested {@link MyExampleEto}.\n" //
        + "   * @return the {@link MyExampleEto} or {@code null} if no such ETO exists.\n" //
        + "   */\n" //
        + "  public MyExampleEto findMyExample(long id) {\n" //
        + "\n" //
        + "    LOG.debug(\"Get MyExample with id {} from database.\", id);\n" //
        + "    Optional<MyExampleEntity> entity = getRepository().findById(id);\n" //
        + "    if (entity.isPresent()) {\n" //
        + "      return getBeanMapper().toEto(entity.get());\n" //
        + "    } else {\n" //
        + "      return null;\n" //
        + "    }\n" //
        + "  }\n" //
        + "\n" //
        + "}\n");
  }

}

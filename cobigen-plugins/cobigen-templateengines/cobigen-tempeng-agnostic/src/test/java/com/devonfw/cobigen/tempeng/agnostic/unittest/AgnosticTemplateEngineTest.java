package com.devonfw.cobigen.tempeng.agnostic.unittest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.customer.app.mycomponent.dataaccess.FooBarEntity;
import com.customer.app.mycomponent.dataaccess.MyExampleEntity;
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
   * Tests a basic agnostic generation.
   */
  @Test
  public void testGenerateUcFind() {

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

  /**
   * Tests a basic agnostic generation.
   */
  @Test
  public void testGenerateEto() {

    // arrange
    final Path templateFolder = Paths.get("src/test/java/").toAbsolutePath();
    TextTemplate template = new TextTemplate() {
      @Override
      public String getRelativeTemplatePath() {

        return "x_rootpackage_x/x_component_x/common/X_EntityName_XEto.java";
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
    model.put("classObject", MyExampleEntity.class);

    // act
    StringWriter out = new StringWriter();
    this.engine.process(template, model, out, "UTF-8");

    // assert
    assertThat(out.toString()).isEqualTo("package com.customer.app.mycomponent.common;\n" //
        + "\n" //
        + "import com.customer.app.general.common.AbstractEto;\n" //
        + "import java.time.LocalDate;\n" //
        + "\n" //
        + "/**\n" //
        + " * Implementation of {@link MyExample} as {@link AbstractEto ETO}.\n" //
        + " */\n" //
        + "public class MyExampleEto extends AbstractEto implements MyExample {\n" //
        + "\n" //
        + "  private String name;\n" //
        + "\n" //
        + "  private LocalDate birthday;\n" //
        + "\n" //
        + "  /**\n" //
        + "   * The constructor.\n" //
        + "   */\n" //
        + "  public MyExampleEto() {\n" //
        + "\n" //
        + "    super();\n" //
        + "  }\n" //
        + "\n" //
        + "  @Override\n" //
        + "  public String getName() {\n" //
        + "    return this.name;\n" //
        + "  }\n" //
        + "\n" //
        + "  @Override\n" //
        + "  public void setName(String name) {\n" //
        + "    this.name = name;\n" //
        + "  }\n" //
        + "\n" //
        + "  @Override\n" //
        + "  public LocalDate getBirthday() {\n" //
        + "    return this.birthday;\n" //
        + "  }\n" //
        + "\n" //
        + "  @Override\n" //
        + "  public void setBirthday(LocalDate birthday) {\n" //
        + "    this.birthday = birthday;\n" //
        + "  }\n" //
        + "\n" //
        + "}\n");
  }

  /**
   * Tests a basic agnostic generation.
   */
  @Test
  public void testGenerateEtoWithParentEntity() {

    // arrange
    final Path templateFolder = Paths.get("src/test/java/").toAbsolutePath();
    TextTemplate template = new TextTemplate() {
      @Override
      public String getRelativeTemplatePath() {

        return "x_rootpackage_x/x_component_x/common/X_EntityName_XEto.java";
      }

      @Override
      public Path getAbsoluteTemplatePath() {

        return templateFolder.resolve(getRelativeTemplatePath());
      }
    };
    HashMap<String, Object> model = new HashMap<>();
    model.put("rootpackage", "com.customer.app");
    model.put("component", "mycomponent");
    model.put("entityName", "FooBar");
    model.put("classObject", FooBarEntity.class);

    // act
    StringWriter out = new StringWriter();
    this.engine.process(template, model, out, "UTF-8");

    // assert
    assertThat(out.toString()).isEqualTo("package com.customer.app.mycomponent.common;\n" //
        + "\n" //
        + "import com.customer.app.general.common.AbstractEto;\n" //
        + "import com.customer.app.mycomponent.common.MyExampleEto;\n" //
        + "\n" //
        + "/**\n" //
        + " * Implementation of {@link FooBar} as {@link AbstractEto ETO}.\n" //
        + " */\n" //
        + "public class FooBarEto extends MyExampleEto implements FooBar {\n" //
        + "\n" //
        + "  private String foo;\n" //
        + "\n" //
        + "  /**\n" //
        + "   * The constructor.\n" //
        + "   */\n" //
        + "  public FooBarEto() {\n" //
        + "\n" //
        + "    super();\n" //
        + "  }\n" //
        + "\n" //
        + "  @Override\n" //
        + "  public String getFoo() {\n" //
        + "    return this.foo;\n" //
        + "  }\n" //
        + "\n" //
        + "  @Override\n" //
        + "  public void setFoo(String foo) {\n" //
        + "    this.foo = foo;\n" //
        + "  }\n" //
        + "\n" //
        + "}\n");
  }

}

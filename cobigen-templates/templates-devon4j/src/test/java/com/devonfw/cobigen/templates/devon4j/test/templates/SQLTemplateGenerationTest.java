package com.devonfw.cobigen.templates.devon4j.test.templates;

import com.devonfw.cobigen.templates.devon4j.test.templates.testclasses.SQLTestEntity;
import com.devonfw.cobigen.templates.devon4j.utils.SQLUtil;
import org.junit.Test;

public class SQLTemplateGenerationTest extends AbstractJavaTemplateTest {

  @Test
  public void generateSQLTest() {

    this.defaultInit("src/main/templates/sql_java_app/templates/V0000__Create_${variables.entityName}Entity.sql.ftl",
        SQLTestEntity.class, new Class<?>[] { SQLUtil.class });
    String output = this.process();
  }
}

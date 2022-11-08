package com.devonfw.cobigen.templates.devon4j.test.templates;

import com.devonfw.cobigen.templates.devon4j.test.templates.testclasses.SQLTestEntity;
import com.devonfw.cobigen.templates.devon4j.utils.SQLUtil;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

public class SQLTemplateGenerationTest extends AbstractJavaTemplateTest {
  @Test
  public void generateSQLTest() {
    String output = this.process(SQLTestEntity.class);
  }

  @Override
  public Class<?>[] getUtils() {
    return new Class<?>[] { SQLUtil.class };
  }

  @Override
  public String getTemplatePath() {
    return  "src/main/templates/sql_java_app/templates/V0000__Create_${variables.entityName}Entity.sql.ftl";
  }
}

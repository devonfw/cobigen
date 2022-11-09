package com.devonfw.cobigen.templates.devon4j.test.templates;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.devonfw.cobigen.templates.devon4j.test.templates.testclasses.SQLTestEntity;
import com.devonfw.cobigen.templates.devon4j.test.templates.testclasses.SQLTestEntityDataTypes;
import com.devonfw.cobigen.templates.devon4j.utils.SQLUtil;

public class SQLTemplateGenerationTest extends AbstractJavaTemplateTest {
  @Test
  public void generateSQLTest() {

    String output = process(SQLTestEntity.class);
  }

  @Override
  public Class<?>[] getUtils() {

    return new Class<?>[] { SQLUtil.class };
  }

  @Override
  public String getTemplatePath() {

    return "src/main/templates/sql_java_app/templates/V0000__Create_${variables.entityName}Entity.sql.ftl";
  }

  /**
   * Test the correct generation of data types
   */
  @Test
  public void testDatatypeMapping() {

    String ouptut = process(SQLTestEntityDataTypes.class);
    assertThat(ouptut).contains("_timestamp2 TIMESTAMP").contains("_blob2 BLOB").contains("_bit BIT,")
        .contains("_date DATE").contains("_tinyint TINYINT").contains("_integer2 INTEGER").contains("_bigint BIGINT")
        .contains("_varchar3 VARCHAR").contains("_integer1 INTEGER").contains("_varchar4 VARCHAR")
        .contains("_clob CLOB").contains("_blob BLOB").contains("_varchar VARCHAR").contains("_char2 CHAR(1)")
        .contains(" _smallint SMALLINT").contains(" _char CHAR(1)").contains("_timestamp TIMESTAMP")
        .contains("_time TIME").contains("_numeric NUMERIC").contains("_varchar2 VARCHAR");

  }
}

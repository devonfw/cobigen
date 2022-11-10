package com.devonfw.cobigen.templates.devon4j.test.templates;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.devonfw.cobigen.templates.devon4j.test.templates.testclasses.SQLTestEntity;
import com.devonfw.cobigen.templates.devon4j.test.templates.testclasses.SQLTestEntityDataTypes;
import com.devonfw.cobigen.templates.devon4j.test.templates.testclasses.SQLTestEntityForeignKeys;
import com.devonfw.cobigen.templates.devon4j.utils.SQLUtil;

/**
 * Test class for SQL template generation
 *
 */
public class SQLTemplateGenerationTest extends AbstractJavaTemplateTest {

  @Override
  public Class<?>[] getUtils() {

    return new Class<?>[] { SQLUtil.class };
  }

  @Override
  public String getTemplatePath() {

    return "src/main/templates/sql_java_app/templates/V0000__Create_${variables.entityName}Entity.sql.ftl";
  }

  /**
   * Tests the correct generation of the enumerated type, the primary key, and name overriding
   */
  @Test
  public void testSQLEntity() {

    String output = process(SQLTestEntity.class);
    assertThat(output).contains("CREATE TABLE SQLTEST").contains("ENUM_TEST_FIELD_NAME_OVERRIDE VARCHAR(420)")
        .contains("MY_ID_FIELD BIGINT AUTO_INCREMENT PRIMARY KEY");
  }

  /**
   * Tests the correct generation of data types
   */
  @Test
  public void testDatatypeMapping() {

    String ouptut = process(SQLTestEntityDataTypes.class);
    assertThat(ouptut).contains("timestamp2 TIMESTAMP").contains("blob2 BLOB").contains("bit BIT,")
        .contains("date DATE").contains("tinyint TINYINT").contains("integer2 INTEGER").contains("bigint BIGINT")
        .contains("varchar3 VARCHAR").contains("integer1 INTEGER").contains("varchar4 VARCHAR").contains("clob CLOB")
        .contains("blob BLOB").contains("varchar VARCHAR").contains("char2 CHAR(1)").contains("smallint SMALLINT")
        .contains("char1 CHAR(1)").contains("timestamp TIMESTAMP").contains("time TIME").contains("numeric NUMERIC")
        .contains("varchar2 VARCHAR").contains("CREATE TABLE SQLDataTypeTest").contains("varchar5 VARCHAR");

  }

  /**
   * Tests the correct generation of foreign key statements
   */
  @Test
  public void testForeignKeyStatements() {

    String output = process(SQLTestEntityForeignKeys.class);
    assertThat(output).contains("test_id BIGINT, FOREIGN KEY (test_id) REFERENCES SQLTEST(id)");
  }
}

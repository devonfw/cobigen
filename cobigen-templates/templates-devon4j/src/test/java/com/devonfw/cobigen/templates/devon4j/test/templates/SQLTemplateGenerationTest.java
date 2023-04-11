package com.devonfw.cobigen.templates.devon4j.test.templates;

import static org.assertj.core.api.Assertions.*;

import com.devonfw.cobigen.templates.devon4j.test.templates.testclasses.SQLTestJoinTableEntity;
import org.junit.Test;

import com.devonfw.cobigen.templates.devon4j.test.templates.testclasses.SQLTestEntity;
import com.devonfw.cobigen.templates.devon4j.test.templates.testclasses.SQLTestDataTypesEntity;
import com.devonfw.cobigen.templates.devon4j.test.templates.testclasses.SQLTestForeignKeysEntity;
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
  public void testEnumType() {

    String output = process(SQLTestEntity.class);
    assertThat(output).contains("CREATE TABLE SQLTEST").contains("ENUM_TEST_FIELD_NAME_OVERRIDE VARCHAR(420)")
        .contains("MY_ID_FIELD BIGINT AUTO_INCREMENT PRIMARY KEY");
  }

  /**
   * Tests the correct generation of data types
   */
  @Test
  public void testDatatypeMapping() {

    String ouptut = process(SQLTestDataTypesEntity.class);
    assertThat(ouptut).contains("timestamp2 TIMESTAMP").contains("bit BIT,").contains("date DATE")
        .contains("tinyint TINYINT").contains("integer2 INTEGER").contains("bigint BIGINT")
        .contains("varchar3 VARCHAR(255)").contains("integer1 INTEGER").contains("varchar4 VARCHAR(255)")
        .contains("blob BLOB").contains("varchar VARCHAR(255)").contains("char2 CHAR(1)").contains("smallint SMALLINT")
        .contains("char1 CHAR(1)").contains("timestamp TIMESTAMP").contains("time TIME").contains("numeric NUMERIC")
        .contains("varchar2 VARCHAR(255)").contains("CREATE TABLE SQLDataTypeTest").contains("varchar5 VARCHAR(255)");

  }

  /**
   * Tests the correct generation of foreign key statements
   */
  @Test
  public void testForeignKeyStatements() {

    String output = process(SQLTestForeignKeysEntity.class);
    assertThat(output).contains("test_id BIGINT, FOREIGN KEY (test_id) REFERENCES SQLTEST(MY_ID_FIELD)");
  }

  /**
   * Tests successful generation of a second CREATE TABLE statement from the @JoinTable annotation
   */
  @Test
  public void testJoinTableGeneration() {
    String output = process(SQLTestJoinTableEntity.class);
    assertThat(output).contains("CREATE TABLE MY_AWESOME_JOINTABLE")
            .contains("REF_ENTITY_ID BIGINT UNIQUE, FOREIGN KEY (REF_ENTITY_ID) REFERENCES REFERENCE(OVERRIDE_ID)")
            .contains("SQLTESTJOINTABLE_ID BIGINT, FOREIGN KEY (SQLTESTJOINTABLE_ID) REFERENCES SQLTESTJOINTABLE(ID)");
  }
}

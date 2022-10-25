package com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest;

import javax.persistence.Column;
import javax.persistence.Id;

import com.devonfw.cobigen.templates.devon4j.test.utils.SQLUtilTest.SQLAnnotationTest;
import com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.entities.TestAnotherSimpleEntity;
import com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.entities.TestNotSoSimpleEntity;
import com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.entities.TestSimpleEntity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * This class is a test class for {@link SQLAnnotationTest}
 *
 */
public class TestSqlTypeAnnotations {

  // Specific Statement variations
  @Id
  Long id;

  String testSimpleString;

  @Size
  String testAtSize;

  @Size
  Integer testSizeMissing;

  Integer testSimpleInteger;

  @Column(name = "TEST_AT_COLUMN_NULLABLE_AT_NOTNULL", length = 50, nullable = true)
  @NotNull
  String testAtColumnNullableAtNotNull;

  @Column(name = "TEST_AT_COLUMN_NULLABLE", length = 50, nullable = true)
  String testAtColumnNullable;

  @Column(name = "TEST_AT_COLUMN_AT_NOTNULL", length = 50, nullable = false)
  @NotNull
  String testAtColumnNotNullableAtNotNull;

  @Column(name = "TEST_AT_COLUMN", length = 50, nullable = false)
  String testAtColumnNotNullable;

  @NotNull
  String testAtNotNull;

  @Size
  @NotNull
  String testAtSizeAtNotNull;

  @Column(name = "TEST_AT_COLUMN", length = 50, nullable = false)
  @Size
  @NotNull
  TestSimpleEntity testEntityAtColumnNotNullableAtSizeAtNotNull;

  TestSimpleEntity testEntityAtTable;

  TestSimpleEntity testAnonymousEntityAtTable = new TestSimpleEntity("Test", 19) {
    @Override
    public String testMethod() {

      return "This method will is overwritten";
    }

  };

  TestAnotherSimpleEntity testEntityAtTableNameDefault;

  TestAnotherSimpleEntity testAnonymousEntityAtTableNameDefault = new TestAnotherSimpleEntity("Test", 19) {
    @Override
    public String testMethod() {

      return "This method will is overwritten";
    }

  };

  TestNotSoSimpleEntity testEntityAtTableNull;

  TestNotSoSimpleEntity testAnonymousEntityAtTableNull = new TestNotSoSimpleEntity("Test", 19) {
    @Override
    public String testMethod() {

      return "This method will is overwritten";
    }

  };

  @Column(name = "FIELD_AT_COLUMN", length = 50, nullable = false)
  Integer testGetColumnNameFieldAtColumn;

  @Column(length = 50, nullable = false)
  Integer testGetColumnNameFieldAtColumnBlank;

  Integer testGetColumnNameFieldAtColumnMissing;

  @Column(name = "METHOD_AT_COLUMN", length = 50, nullable = false)
  public Integer getTestGetColumnNameMethodAtColumn() {

    return this.testGetColumnNameFieldAtColumn;
  }

  @Column(length = 50, nullable = false)
  public Integer getTestGetColumnNameMethodAtColumnBlank() {

    return this.testGetColumnNameFieldAtColumnBlank;
  }

  public Integer getTestGetColumnNameMethodAtColumnMissing() {

    return this.testGetColumnNameFieldAtColumnMissing;
  }
}

package com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest;

import javax.persistence.Column;
import javax.persistence.Id;

import com.devonfw.cobigen.templates.devon4j.test.utils.SQLUtilTest;
import com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.entities.TestSimpleEntity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * This class is a test class for {@link SQLUtilTest}
 *
 */
public class TestSqlTypeAnnotations {

  // Specific Statement variations
  @Id
  Long id;

  @Column(name = "TEST_AT_COLUMN_NULLABLE", length = 50, nullable = false)
  String testAtColumnNullable;

  @Column(name = "TEST_AT_COLUMN_NULLABLE_MISSING", length = 50)
  String testAtColumnNullableMissing;

  @Column(name = "TEST_AT_NULLABLE", length = 50)
  @NotNull
  String testAtColumnNotNull;

  @Column(name = "TEST_AT_NULLABLE_MISSING", length = 50)
  String testAtColumnNotNullMissing;

  // Entities
  @Column(name = "TEST_SIMPLE_ENTITY_AT_SIZE", length = 50)
  @Size
  TestSimpleEntity testSimpleEntityAtSize;

  @Column(name = "TEST_SIMPLE_ENTITY_AT_SIZE_MISSING", length = 50)
  TestSimpleEntity testSimpleEntityAtSizeMissing;

}

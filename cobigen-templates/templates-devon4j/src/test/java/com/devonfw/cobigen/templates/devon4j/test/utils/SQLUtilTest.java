package com.devonfw.cobigen.templates.devon4j.test.utils;

import java.lang.reflect.Field;

import org.junit.BeforeClass;
import org.junit.Test;

import com.devonfw.cobigen.templates.devon4j.test.utils.resources.sqltest.TestSqlType;
import com.devonfw.cobigen.templates.devon4j.utils.SQLUtil;

/**
 * Test class for {@link SQLUtil}
 */
public class SQLUtilTest {

  private static Class<?> testSqlType;

  /**
   * Get all Classes for testing
   */
  @BeforeClass
  public static void beforeAll() {

    testSqlType = new TestSqlType().getClass();
  }

  /**
   * Tests if {@link SQLUtil#getSimpleSQLtype(Field)} returns the correct string based on a field type
   */
  @Test
  public void testGetSimpleSqlType() {

    Field[] fields = testSqlType.getDeclaredFields();

  }
}
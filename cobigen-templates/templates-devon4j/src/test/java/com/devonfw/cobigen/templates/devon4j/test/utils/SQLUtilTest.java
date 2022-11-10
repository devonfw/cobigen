package com.devonfw.cobigen.templates.devon4j.test.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.devonfw.cobigen.templates.devon4j.utils.SQLUtil;

/**
 *
 * Test class for {@link SQLUtil}
 *
 */
public class SQLUtilTest {

  @Test
  public void testClassTypeMapping() {

    // Test fails
    assertThat(SQLUtil.mapType("Class<?>")).isEqualTo("VARCHAR");
  }

  @Test
  public void testByteArray() {

    // Test fails
    assertThat(SQLUtil.mapType("byte[]")).isEqualTo("BLOB");
  }

  @Test
  public void testTimestamp() {

    // Test fails
    assertThat(SQLUtil.mapType("Timestamp")).isEqualTo("TIMESTAMP");
  }

  @Test
  public void testTimeZone() {

    // Test fails
    assertThat(SQLUtil.mapType("TimeZone")).isEqualTo("VARCHAR");
  }

  @Test
  public void testCalendar() {

    assertThat(SQLUtil.mapType("Calendar")).isEqualTo("TIMESTAMP");
  }

}

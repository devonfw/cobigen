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

  /**
   * Tests type mappings between simple Java types and corresponding SQL types
   */
  @Test
  public void testTypeMappings() {
    assertThat(SQLUtil.mapType("Class<?>")).isEqualTo("VARCHAR");
    assertThat(SQLUtil.mapType("byte[]")).isEqualTo("BLOB");
    assertThat(SQLUtil.mapType("Timestamp")).isEqualTo("TIMESTAMP");
    assertThat(SQLUtil.mapType("TimeZone")).isEqualTo("VARCHAR");
    assertThat(SQLUtil.mapType("Calendar")).isEqualTo("TIMESTAMP");
  }
}

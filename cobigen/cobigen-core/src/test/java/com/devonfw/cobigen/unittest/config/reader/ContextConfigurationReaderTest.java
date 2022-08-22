package com.devonfw.cobigen.unittest.config.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.nio.file.Paths;

import org.junit.Test;

import com.devonfw.cobigen.api.exception.ConfigurationConflictException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.impl.config.constant.WikiConstants;
import com.devonfw.cobigen.impl.config.reader.ContextConfigurationReader;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

import junit.framework.TestCase;

/**
 * This {@link TestCase} tests the {@link ContextConfigurationReader}
 */
public class ContextConfigurationReaderTest extends AbstractUnitTest {

  /**
   * Root path to all resources used in this test case
   */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/config/reader/ContextConfigurationReaderTest/";

  /**
   * Tests whether an invalid configuration results in an {@link InvalidConfigurationException}
   *
   * @throws InvalidConfigurationException expected
   */
  @Test(expected = InvalidConfigurationException.class)
  public void testErrorOnInvalidConfiguration() throws InvalidConfigurationException {

    // ContextConfigurationReaderFactory.getReader(Paths.get(new File(testFileRootPath + "faulty").toURI()));
  }

  /**
   * Tests whether an {@link ConfigurationConflictException} will be thrown when both a v2.1 and v2.2 context.xml are
   * present (new templates with old custom templates). Also tests if the thrown error message contains a link to the
   * wiki.
   *
   * @throws ConfigurationConflictException if a conflict occurred
   *
   */
  @Test
  public void testConflictConfiguration() throws ConfigurationConflictException {

    Throwable bothPresent = assertThrows(ConfigurationConflictException.class, () -> {
      new ContextConfigurationReader(Paths.get(testFileRootPath + "conflicted_old_templates"));
    });

    assertThat(bothPresent instanceof ConfigurationConflictException);
    assertThat(bothPresent.getMessage()).contains(WikiConstants.WIKI_UPDATE_OLD_CONFIG);
  }

  /**
   * Tests whether a valid v2.1 configuration can be read from src/main/templates folder
   *
   * Backward Compatibility test, remove when monolithic context.xml is deprecated.
   *
   * @throws Exception test fails
   */
  @Test
  public void testContextLoadedFromOldConfiguration() throws Exception {

    CobiGenFactory.create(new File(testFileRootPath + "valid_source_folder").toURI(), true);
  }

  /**
   * Tests whether a valid configuration can be read from a zip file.
   *
   * @throws Exception test fails
   */
  @Test
  public void testReadConfigurationFromZip() throws Exception {

    CobiGenFactory.create(new File(testFileRootPath + "valid.zip").toURI(), true);
  }

}
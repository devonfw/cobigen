package com.devonfw.cobigen.propertyplugin;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Testing of PropertyMerger of its override property
 */
public class PropertyMergerTest extends TestCase {

  /**
   * Root path to all resources used in this test case
   */
  private static String testFileRootPath = "src/test/resources/";

  /**
   * This test checks if the overridden has occurred successfully or not
   *
   * @throws Exception test fails
   */
  @Test
  public void testPropertyMergeOverride() throws Exception {

    File base = new File(testFileRootPath + "test.properties");
    PropertyMerger pMerger = new PropertyMerger("", true);
    String mergedPropFile = pMerger.merge(base,
        IOUtils.toString(new FileReader(new File(testFileRootPath + "Name.ftl"))), "UTF-8");
    assertThat(mergedPropFile).contains("NachNameOverride");
    assertThat(mergedPropFile).doesNotContain("nachNameOriginal");
    assertThat(mergedPropFile).containsSequence("lastName", "firstName");
  }

  /**
   * This test checks if the overridden has occurred successfully or not
   *
   * @throws Exception test fails.
   */
  @Test
  public void testPropertyMergeWithoutOverride() throws Exception {

    File base = new File(testFileRootPath + "test.properties");
    PropertyMerger pMerger = new PropertyMerger("", false);
    String mergedPropFile = pMerger.merge(base,
        IOUtils.toString(new FileReader(new File(testFileRootPath + "Name.ftl"))), "UTF-8");
    assertThat(mergedPropFile).doesNotContain("NachNameOverride");
    assertThat(mergedPropFile).contains("nachNameOriginal");
    assertThat(mergedPropFile).contains("firstName");
    assertThat(mergedPropFile).contains("lastName");
  }

}

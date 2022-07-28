package com.devonfw.cobigen.impl.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the collectors (search) methods of context.xml and templates.xml in a specific path
 *
 */
public class FileSystemUtilTest {

  private static final String W = null;

  /**
   * Test methods collectAllContextXML and collectAllTemplatesXML
   */
  @Test
  public void testCollectAllContextAndTemplatesXMLs() {

    Path xmlsPath = Paths.get("src/test/resources/testdata/unittest/config/util/collectors");

    List<Path> allContexts = FileSystemUtil.collectAllContextXML(xmlsPath);
    List<Path> alltemplates = FileSystemUtil.collectAllTemplatesXML(xmlsPath);

    Assert.assertEquals(allContexts.get(0),
        Paths.get("src/test/resources/testdata/unittest/config/util/collectors/contextXMLs/context.xml"));
    Assert.assertEquals(alltemplates.get(0),
        Paths.get("src/test/resources/testdata/unittest/config/util/collectors/templatesXMLs/templates.xml"));

  }
}

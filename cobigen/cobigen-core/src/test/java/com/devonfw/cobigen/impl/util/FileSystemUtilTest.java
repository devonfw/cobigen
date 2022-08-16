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

  /**
   * Test method testCollectAllFilesByName
   */
  @Test
  public void testCollectAllFilesByName() {

    Path xmlsPath = Paths.get("src/test/resources/testdata/unittest/config/util/collectors");

    List<Path> allContexts = FileSystemUtil.collectAllFilesByName(xmlsPath, "context.xml");
    List<Path> alltemplates = FileSystemUtil.collectAllFilesByName(xmlsPath, "templates.xml");

    Assert.assertEquals(allContexts.get(0),
        Paths.get("src/test/resources/testdata/unittest/config/util/collectors/contextXMLs/context.xml"));
    Assert.assertEquals(alltemplates.get(0),
        Paths.get("src/test/resources/testdata/unittest/config/util/collectors/templatesXMLs/templates.xml"));

  }
}

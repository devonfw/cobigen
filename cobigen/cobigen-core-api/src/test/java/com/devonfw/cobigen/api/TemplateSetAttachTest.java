package com.devonfw.cobigen.api;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.devonfw.cobigen.api.util.SystemUtil;

public class TemplateSetAttachTest {

  @Test

  public void testmaveninstallattachTemplateSet() throws Exception {

    Path path = Paths.get(testdataRoot, "TestInvalidMark.txt");
    String targetCharset = "UTF-8";
    SystemUtil.determineLineDelimiter(path, targetCharset);
  }
}

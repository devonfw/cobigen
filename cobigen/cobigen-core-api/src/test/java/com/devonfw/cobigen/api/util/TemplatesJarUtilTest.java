package com.devonfw.cobigen.api.util;

import static org.junit.Assert.fail;

import java.nio.file.Paths;

import org.junit.Test;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.impl.CobiGenFactory;

/**
 * TODO ekrieger This type ...
 *
 */
public class TemplatesJarUtilTest {

  @Test
  public void test() {

    // teste maven coordinaten
    // teste existens of jar
    // teste Folder
    // teste maven download with wrong coodrinaten
    CobiGen cobigen = CobiGenFactory.create(Paths.get("C:\\Users\\ekrieger\\Desktop\\Templates-Test").toUri(), true);
    fail("Not yet implemented");
  }

}

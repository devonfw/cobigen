package com.devonfw.cobigen.unittest.config.reader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.impl.config.reader.TemplateSetConfigurationManager;

public class TemplateSetConfigurationManagerTest {

  @Test
  public void testVersionConflictResolution() throws Exception {
    
    Path configRoot = Paths.get("src/test/resources/testdata/systemtest/ClassLoadTemplateSetTest/conflicted/template-sets/adapted");
    
    TemplateSetConfigurationManager manager = new TemplateSetConfigurationManager();
    List<Path> templateSetFiles = manager.loadTemplateSetFilesAdapted(configRoot);
    
    // Should only load one template set (the newest version)
    assertThat(templateSetFiles).hasSize(1);
    
    // Should be template-set2 (version 1.1) which is newer than template-set1 (version 1)
    assertThat(templateSetFiles.get(0).toString()).contains("template-set2");
  }
}
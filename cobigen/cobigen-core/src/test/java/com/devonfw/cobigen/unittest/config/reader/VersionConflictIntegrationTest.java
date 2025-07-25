package com.devonfw.cobigen.unittest.config.reader;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.impl.config.reader.TemplateSetConfigurationManager;

public class VersionConflictIntegrationTest {

  @Test
  public void testVersionConflictResolution() throws Exception {
    
    // Test that version conflict resolution is working correctly
    Path configRoot = Paths.get("src/test/resources/testdata/systemtest/ClassLoadTemplateSetTest/conflicted/template-sets/adapted");
    
    TemplateSetConfigurationManager manager = new TemplateSetConfigurationManager();
    List<Path> templateSetFiles = manager.loadTemplateSetFilesAdapted(configRoot);
    
    // Should only load one template set (the newest version)
    assertThat(templateSetFiles).hasSize(1);
    
    // Should be template-set2 (version 1.1) which is newer than template-set1 (version 1)
    assertThat(templateSetFiles.get(0).toString()).contains("template-set2");
    
    // Verify the path contains the newer version
    assertThat(templateSetFiles.get(0).toString()).contains("template-set2");
    assertThat(templateSetFiles.get(0).toString()).doesNotContain("template-set1");
  }
  
  @Test
  public void testNoTemplateConflict() throws Exception {
    
    // Test that when there's no conflict, all template sets are loaded
    Path configRoot = Paths.get("src/test/resources/testdata/systemtest/ClassLoadTemplateSetTest/template-sets/adapted");
    
    TemplateSetConfigurationManager manager = new TemplateSetConfigurationManager();
    List<Path> templateSetFiles = manager.loadTemplateSetFilesAdapted(configRoot);
    
    // Should load the single template set since there's no conflict
    assertThat(templateSetFiles).hasSize(1);
    assertThat(templateSetFiles.get(0).toString()).contains("template-set1");
  }
}
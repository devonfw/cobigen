package com.devonfw.cobigen.unittest.config.upgrade;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.impl.config.upgrade.TemplateSetUpgrader;
import com.devonfw.cobigen.unittest.config.common.AbstractUnitTest;

/**
 * Test suite for {@link TemplateSetUpgrader}
 */
public class TemplateSetUpgraderTest extends AbstractUnitTest {

  /** Root path to all resources used in this test case */
  private static String testFileRootPath = "src/test/resources/testdata/unittest/config/upgrade/TemplateSetUpgraderTest/";

  private Path templateLocation;

  /** JUnit Rule to create and automatically cleanup temporarily files/folders */
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Before
  public void prepare() throws IOException {

    Path playground = this.tempFolder.newFolder("playground").toPath();
    FileUtils.copyDirectory(new File(testFileRootPath), playground.toFile());
    this.templateLocation = playground.resolve(ConfigurationConstants.TEMPLATES_FOLDER);
  }

  @Test
  public void testTemplateSetUprade() throws Exception {

    TemplateSetUpgrader templateSetUpgrader = new TemplateSetUpgrader(this.templateLocation);
    templateSetUpgrader.upradeTemplatesToTemplateSets();

    Path templateSetsPath = this.templateLocation.getParent().resolve(ConfigurationConstants.TEMPLATE_SETS_FOLDER);
    Path templateSetsAdapted = templateSetsPath.resolve(ConfigurationConstants.ADAPTED_FOLDER);
    assertThat(templateSetsPath).exists();
    assertThat(templateSetsAdapted).exists();
    // context.xml ist am richtigen odrdner
    // context.xml ist korrekt gesplitted
    // testen ob die utils funktionieren
    // dependencies testen, (ob alle vorhanden sind)


  }
}

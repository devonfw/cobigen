package com.devonfw.cobigen.systemtest;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.junit.Test;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.systemtest.common.AbstractApiTest;

/**
 * TODO
 *
 */
public class TemplatesInstallationTest extends AbstractApiTest {

  /**
   * Tests that sources get overwritten if merge strategy override is configured.
   *
   * @throws Exception test fails.
   */
  @Test
  public void testInstallTemplatesAtStartupOLD() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      File templateSets = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATES_FOLDER);
      File target = new File(folder, ".cobigen");
      System.out.println(CobiGenPaths.getCobiGenHomePath());
      BufferedWriter writer = new BufferedWriter(new FileWriter(target));
      writer.write("template-sets.installed=com.devonfw.cobigen:templates-devon4j:2021.12.005");
      writer.close();
      CobiGen cobigen = CobiGenFactory.create(templateSets.toURI(), true);
      System.out.println("Test");
    });
  }

  /**
   * Tests that sources get overwritten if merge strategy override is configured.
   *
   * @throws Exception test fails.
   */
  @Test
  public void testInstallTemplatesAtStartupNEW() throws Exception {

    File folder = this.tmpFolder.newFolder("TemplateSetsInstalledTest");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, folder.getAbsolutePath()).execute(() -> {
      File templateSets = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER);
      File downloaded = this.tmpFolder.newFolder("TemplateSetsInstalledTest",
          ConfigurationConstants.TEMPLATE_SETS_FOLDER, "downloaded");
      File target = new File(folder, ".cobigen");
      System.out.println(CobiGenPaths.getCobiGenHomePath());
      BufferedWriter writer = new BufferedWriter(new FileWriter(target));
      writer.write("template-sets.installed=com.devonfw.cobigen:templates-devon4j:2021.12.006");
      writer.close();
      CobiGen cobigen = CobiGenFactory.create(templateSets.toURI());
    });
    // // File templates = this.tmpFolder.newFolder("TemplateSetsInstalledTest", "template-sets");
    // // File downloaded = this.tmpFolder.newFolder("TemplateSetsInstalledTest", "template-sets", "downloaded");
    // File templates = this.tmpFolder.newFolder("TemplateSetsInstalledTest", "templates");
    // File downloaded = this.tmpFolder.newFolder("TemplateSetsInstalledTest", "template-sets", "CobiGen_Templates");
    // System.out.println(templates.toString());

    // // Files.copy(new File(testFileRootPath + "templateSetsInstalled/.cobigen").toPath(), target.toPath(), null);
    // // Files.createFile(target.toPath(), null);

    // // MockedStatic<CobiGenPaths> paths = Mockito.mockStatic(CobiGenPaths.class);
    // // paths.when(CobiGenPaths::getCobiGenHomePath).thenReturn(templates.toPath());
    // // paths.when(CobiGenPaths::getTemplatesFolderPath).thenReturn(templates.toPath());
    // // // paths.when(() -> CobiGenPaths.getTemplateSetsFolderPath(folder.toPath()).thenReturn(null));

    // List<TemplateTo> templates = cobigen.getMatchingTemplates(input);
    // assertThat(templates).hasSize(1);
    //
    // GenerationReportTo report = cobigen.generate(input, templates.get(0), Paths.get(folder.toURI()));
    //
    // assertThat(report).isSuccessful();
    // assertThat(target).hasContent("overwritten");
  }

}

package com.devonfw.cobigen.maven.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;

import org.junit.Test;

import com.devonfw.cobigen.maven.systemtest.config.constant.MavenMetadata;
import com.devonfw.cobigen.maven.test.AbstractMavenTest;

/**
 * TODO alsaad This type ...
 *
 */
public class TemplatePluginLoadtest extends AbstractMavenTest {

  public static final String TEST_RESOURCES_ROOT = "src/test/resources/testdata/systemtest/Devon4JTemplateTest/";

  @Test
  public void testPluginsFromPom() throws Exception {

    File testProject = new File(TEST_RESOURCES_ROOT + "TestProject/");
    File templatesProject = new File(TEST_RESOURCES_ROOT, "TestTemplatesProjectForPom");
    File testProjectRoot = runMavenInvoker(testProject, templatesProject, MavenMetadata.LOCAL_REPO);

    assertThat(testProjectRoot.list()).containsOnly("pom.xml", "src", "target");
    long numFilesInSrc = Files.walk(testProjectRoot.toPath().resolve("src")).filter(Files::isRegularFile).count();
    // 1 from tos
    assertThat(numFilesInSrc).isEqualTo(1);
  }

}

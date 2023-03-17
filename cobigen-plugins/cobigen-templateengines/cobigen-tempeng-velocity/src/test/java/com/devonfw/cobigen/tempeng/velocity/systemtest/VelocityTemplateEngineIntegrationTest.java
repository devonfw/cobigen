package com.devonfw.cobigen.tempeng.velocity.systemtest;

import static com.devonfw.cobigen.api.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.tempeng.velocity.systemtest.testobjects.Input;

/** Test suite integrating cobigen-core with the velocity template engine. */
public class VelocityTemplateEngineIntegrationTest {

  /** JUnit rule to create new temporary files/folder */
  @Rule
  public TemporaryFolder tempFolderRule = new TemporaryFolder();

  /**
   * Tests a basic generation integrated with cobigen-core
   *
   * @throws Exception test fails
   */
  @Test
  public void testBasicGeneration() throws Exception {

    CobiGen cobigen = CobiGenFactory.create(new File("src/test/resources/systemtest").toURI(), true);
    Object input = cobigen.read(
        new File("src/test/java/com/devonfw/cobigen/tempeng/velocity/systemtest/testobjects/Input.java").toPath(),
        Charset.forName("UTF-8"), getClass().getClassLoader());
    List<IncrementTo> increments = cobigen.getMatchingIncrements(input);

    assertThat(increments).hasSize(1);
    assertThat(increments.get(0).getTemplates()).hasSize(1);

    File targetFolder = this.tempFolderRule.newFolder("cobigen-");
    GenerationReportTo report = cobigen.generate(Input.class, increments.get(0), targetFolder.toPath());
    assertThat(report).isSuccessful();

    assertThat(targetFolder.toPath().resolve("velocityTest.txt")).exists().hasContent("String,int,");
  }

}

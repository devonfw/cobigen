package com.devonfw.cobigen.javaplugin.integrationtest;

import static com.devonfw.cobigen.api.assertj.CobiGenAsserts.assertThat;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;

public class GeneratedAnnotationTest extends AbstractIntegrationTest {

  /*
   * Test to check whether the entire generation process runs with new modifications in JavaMerger and merges the
   * generated annotation
   *
   * @throws Exception
   *
   */
  @Test
  public void testGenerateAddedGeneratedAnnotations() throws Exception {

    File tmpProject = this.tmpFolder.newFolder("playground", "project");

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, tmpProject.toPath().toString()).execute(() -> {
      CobiGen cobiGen = CobiGenFactory.create(this.cobigenConfigFolder.toURI());
      File tmpFolderCobiGen = this.tmpFolder.newFolder("cobigen_output");
      String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");

      Object input = cobiGen.read(
          new File("src/test/resources/testdata/integrationtest/javaSources/EmployeeEntity.java").toPath(),
          Charset.forName("UTF-8"));
      List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);
      for (TemplateTo template : templates) {
        if (template.getId().equals("generated.java")) {
          // generate method with an additional boolean parameter set to true to avoid conflicts with existing tests
          GenerationReportTo report = cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()),
              false);
          assertAnnotationReportWithGeneratedAnnotation(report, tmpFolderCobiGen, LINE_SEPARATOR);
          break;
        }
      }
    });
  }

  /*
   * Test to check whether the entire generation process can be enabled by .cobigen properties, to add the generated
   * annotation
   *
   * @throws Exception
   */
  @Test
  public void testGenerateAddedGeneratedAnnotationsFromProperties() throws Exception {

    File tmpProject = this.tmpFolder.newFolder("playground2", "project");

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, tmpProject.toPath().toString()).execute(() -> {
      Path cobigenHome = CobiGenPaths.getCobiGenHomePath();
      Path propertiesPath = cobigenHome.resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
      try (PrintStream out = new PrintStream(new FileOutputStream(propertiesPath.toString()))) {
        // out.print(ConfigurationConstants.ADD_GENERATED_ANNOTATION + "=true");
      }
      CobiGen cobiGen = CobiGenFactory.create(this.cobigenConfigFolder.toURI());
      File tmpFolderCobiGen = this.tmpFolder.newFolder("cobigen_output");
      String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");

      Object input = cobiGen.read(
          new File("src/test/resources/testdata/integrationtest/javaSources/EmployeeEntity.java").toPath(),
          Charset.forName("UTF-8"));
      List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);
      for (TemplateTo template : templates) {
        if (template.getId().equals("generated.java")) {
          // generate method which reads from the .cobigen file and add generated annotation
          GenerationReportTo report = cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()),
              false);
          assertAnnotationReportWithGeneratedAnnotation(report, tmpFolderCobiGen, LINE_SEPARATOR);
          break;
        }
      }
    });
  }

  /*
   * Test to check whether the entire generation process can be disabled by .cobigen properties, to not add the
   * generated annotation
   *
   * @throws Exception
   */
  /**
   * @throws Exception
   */
  @Test
  public void testGenerateNoAddedGeneratedAnnotationsFromProperties() throws Exception {

    File tmpProject = this.tmpFolder.newFolder("playground3", "project");

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, tmpProject.toPath().toString()).execute(() -> {
      Path cobigenHome = CobiGenPaths.getCobiGenHomePath();
      Path propertiesPath = cobigenHome.resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
      try (PrintStream out = new PrintStream(new FileOutputStream(propertiesPath.toString()))) {
        out.print(ConfigurationConstants.ADD_GENERATED_ANNOTATION + "=false");
      }
      CobiGen cobiGen = CobiGenFactory.create(this.cobigenConfigFolder.toURI());
      File tmpFolderCobiGen = this.tmpFolder.newFolder("cobigen_output");
      String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");

      Object input = cobiGen.read(
          new File("src/test/resources/testdata/integrationtest/javaSources/EmployeeEntity.java").toPath(),
          Charset.forName("UTF-8"));
      List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);
      for (TemplateTo template : templates) {
        if (template.getId().equals("generated.java")) {
          // generate method which reads from the .cobigen file and add generated annotation
          GenerationReportTo report = cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()),
              false);
          assertAnnotationReportWithoutGeneratedAnnotation(report, tmpFolderCobiGen, LINE_SEPARATOR);
          break;
        }
      }
    });
  }

  /*
   * method to assert the output file contains the generated annotation
   *
   * @param report
   *
   * @param tmpFolderCobiGen
   *
   * @param LINE_SEPARATOR
   *
   * @throws IOException
   */

  private void assertAnnotationReportWithGeneratedAnnotation(GenerationReportTo report, File tmpFolderCobiGen,
      String LINE_SEPARATOR) throws IOException {

    assertThat(report).isSuccessful();
    Path expectedFile = tmpFolderCobiGen.toPath().resolve("generated.java");
    assertThat(expectedFile).exists();
    String content = new String(Files.readAllBytes(Paths.get(expectedFile.toUri())));
    assertThat(content.trim())
        .isEqualToIgnoringWhitespace("package com.example.domain.myapp.employeemanagement.common.api;" + LINE_SEPARATOR
            + LINE_SEPARATOR + "import com.example.domain.myapp.general.common.api.ApplicationEntity;" + LINE_SEPARATOR
            + "import javax.annotation.Generated;" + LINE_SEPARATOR + LINE_SEPARATOR
            + "public interface Employee extends ApplicationEntity {" + LINE_SEPARATOR + LINE_SEPARATOR
            + "@Generated(value={\"com.devon.CobiGen\"}," + LINE_SEPARATOR + "date=\"" + LocalDate.now() + "\")"
            + LINE_SEPARATOR + "private string field;" + LINE_SEPARATOR + LINE_SEPARATOR
            + "@Generated(value={\"com.devon.CobiGen\"}," + LINE_SEPARATOR + "date=\"" + LocalDate.now() + "\")"
            + LINE_SEPARATOR + "public Employee() {}" + LINE_SEPARATOR + LINE_SEPARATOR
            + "@Generated(value={\"com.devon.CobiGen\"}," + LINE_SEPARATOR + "date=\"" + LocalDate.now() + "\")"
            + LINE_SEPARATOR + "public boolean equals(Object obj);" + LINE_SEPARATOR + LINE_SEPARATOR + "}");
  }

  /*
   * method to assert the output file
   *
   * @param report
   *
   * @param tmpFolderCobiGen
   *
   * @param LINE_SEPARATOR
   *
   * @throws IOException
   */

  private void assertAnnotationReportWithoutGeneratedAnnotation(GenerationReportTo report, File tmpFolderCobiGen,
      String LINE_SEPARATOR) throws IOException {

    assertThat(report).isSuccessful();
    Path expectedFile = tmpFolderCobiGen.toPath().resolve("generated.java");
    assertThat(expectedFile).exists();
    String content = new String(Files.readAllBytes(Paths.get(expectedFile.toUri())));
    assertThat(content.trim())
        .isEqualToIgnoringWhitespace("package com.example.domain.myapp.employeemanagement.common.api;" + LINE_SEPARATOR
            + LINE_SEPARATOR + "import com.example.domain.myapp.general.common.api.ApplicationEntity;" + LINE_SEPARATOR
            + LINE_SEPARATOR + "public interface Employee extends ApplicationEntity {" + LINE_SEPARATOR + LINE_SEPARATOR
            + "  public Employee() {}" + LINE_SEPARATOR + LINE_SEPARATOR + "  private string field;" + LINE_SEPARATOR
            + LINE_SEPARATOR + "  public boolean equals(Object obj);" + LINE_SEPARATOR + LINE_SEPARATOR + "}");
  }
}

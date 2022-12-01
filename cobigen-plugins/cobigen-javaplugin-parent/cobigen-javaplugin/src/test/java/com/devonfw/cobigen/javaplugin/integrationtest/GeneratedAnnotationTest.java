package com.devonfw.cobigen.javaplugin.integrationtest;

import static com.devonfw.cobigen.api.assertj.CobiGenAsserts.assertThat;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.constants.ConfigurationConstants;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.api.util.CobiGenPaths;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;

public class GeneratedAnnotationTest extends AbstractIntegrationTest {

  public static final String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");

  /**
   * Reads from .cobigen file which is not present in home folder and would dynamically be created will have
   * add-generated-annotation set to true during runtime
   *
   * @throws Exception
   */

  @Test
  public void generationTestWithoutPropertiesFile() throws Exception {

    File tmpProject = this.tmpFolder.newFolder("playground", "project");
    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, tmpProject.toPath().toString()).execute(() -> {
      CobiGen cobiGen = CobiGenFactory.create(this.cobigenConfigFolder.toURI());
      File tmpFolderCobiGen = this.tmpFolder.newFolder("cobigen_output");

      Object input = cobiGen.read(
          new File("src/test/resources/testdata/integrationtest/javaSources/EmployeeEntity.java").toPath(),
          Charset.forName("UTF-8"));
      List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);
      for (TemplateTo template : templates) {
        if (template.getId().equals("generated.java")) {
          // generate method with an additional boolean parameter set to true to avoid conflicts with existing tests
          GenerationReportTo report = cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()),
              false);
          String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");
          assertThat(report).isSuccessful();
          Path expectedFile = tmpFolderCobiGen.toPath().resolve("generated.java");
          assertThat(expectedFile).exists();
          String content = new String(Files.readAllBytes(Paths.get(expectedFile.toUri())));
          assertThat(content.trim()).isEqualToIgnoringWhitespace(
              "package com.example.domain.myapp.employeemanagement.common.api;" + LINE_SEPARATOR + LINE_SEPARATOR
                  + "import com.example.domain.myapp.general.common.api.ApplicationEntity;" + LINE_SEPARATOR
                  + "import javax.annotation.Generated;" + LINE_SEPARATOR + LINE_SEPARATOR
                  + "public interface Employee extends ApplicationEntity {" + LINE_SEPARATOR + LINE_SEPARATOR
                  + "@Generated(value={\"com.devon.CobiGen\"}," + LINE_SEPARATOR + "date=\"" + LocalDate.now() + "\")"
                  + LINE_SEPARATOR + "private string field;" + LINE_SEPARATOR + LINE_SEPARATOR
                  + "@Generated(value={\"com.devon.CobiGen\"}," + LINE_SEPARATOR + "date=\"" + LocalDate.now() + "\")"
                  + LINE_SEPARATOR + "public Employee() {}" + LINE_SEPARATOR + LINE_SEPARATOR
                  + "@Generated(value={\"com.devon.CobiGen\"}," + LINE_SEPARATOR + "date=\"" + LocalDate.now() + "\")"
                  + LINE_SEPARATOR + "public boolean equals(Object obj);" + LINE_SEPARATOR + LINE_SEPARATOR + "}");
          break;
        }
      }
    });
  }

  /**
   * Reads from .cobigen file residing in cobigen home folder and has add-generated-annotation value set to true
   *
   * @throws Exception
   */
  @Test
  public void addGeneratedAnnotationSetToTrue() throws Exception {

    File tmpProject = this.tmpFolder.newFolder("playground2", "project");

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, tmpProject.toPath().toString()).execute(() -> {
      // setting up virtual .cobigen file in cobigen home folder
      Path cobigenHome = CobiGenPaths.getCobiGenHomePath();
      Path dotCobigenFilePath = cobigenHome.resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
      Properties props = new Properties();
      props.setProperty("add-generated-annotation", "true");
      props.store(new FileOutputStream(dotCobigenFilePath.toString()), null);
      // cobigen generation process
      CobiGen cobiGen = CobiGenFactory.create(this.cobigenConfigFolder.toURI());
      File tmpFolderCobiGen = this.tmpFolder.newFolder("cobigen_output");
      Object input = cobiGen.read(
          new File("src/test/resources/testdata/integrationtest/javaSources/EmployeeEntity.java").toPath(),
          Charset.forName("UTF-8"));
      List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);
      for (TemplateTo template : templates) {
        if (template.getId().equals("generated.java")) {
          GenerationReportTo report = cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()),
              false);
          String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");
          assertThat(report).isSuccessful();
          Path expectedFile = tmpFolderCobiGen.toPath().resolve("generated.java");
          assertThat(expectedFile).exists();
          String content = new String(Files.readAllBytes(Paths.get(expectedFile.toUri())));
          assertThat(content.trim()).isEqualToIgnoringWhitespace(
              "package com.example.domain.myapp.employeemanagement.common.api;" + LINE_SEPARATOR + LINE_SEPARATOR
                  + "import com.example.domain.myapp.general.common.api.ApplicationEntity;" + LINE_SEPARATOR
                  + "import javax.annotation.Generated;" + LINE_SEPARATOR + LINE_SEPARATOR
                  + "public interface Employee extends ApplicationEntity {" + LINE_SEPARATOR + LINE_SEPARATOR
                  + "@Generated(value={\"com.devon.CobiGen\"}," + LINE_SEPARATOR + "date=\"" + LocalDate.now() + "\")"
                  + LINE_SEPARATOR + "private string field;" + LINE_SEPARATOR + LINE_SEPARATOR
                  + "@Generated(value={\"com.devon.CobiGen\"}," + LINE_SEPARATOR + "date=\"" + LocalDate.now() + "\")"
                  + LINE_SEPARATOR + "public Employee() {}" + LINE_SEPARATOR + LINE_SEPARATOR
                  + "@Generated(value={\"com.devon.CobiGen\"}," + LINE_SEPARATOR + "date=\"" + LocalDate.now() + "\")"
                  + LINE_SEPARATOR + "public boolean equals(Object obj);" + LINE_SEPARATOR + LINE_SEPARATOR + "}");
          break;
        }
      }
    });
  }

  /**
   * Reads from the .cobigen file and add-generated-annotation value set to false
   *
   * @throws Exception
   */

  @Test
  public void addGeneratedAnnotationSetToFalse() throws Exception {

    File tmpProject = this.tmpFolder.newFolder("playground3", "project");

    withEnvironmentVariable(ConfigurationConstants.CONFIG_ENV_HOME, tmpProject.toPath().toString()).execute(() -> {
      // Read from Cobigen file
      Path cobigenHome = CobiGenPaths.getCobiGenHomePath();
      Path dotCobigenFilePath = cobigenHome.resolve(ConfigurationConstants.COBIGEN_CONFIG_FILE);
      Properties props = new Properties();
      props.setProperty("add-generated-annotation", "false");
      props.store(new FileOutputStream(dotCobigenFilePath.toString()), null);
      CobiGen cobiGen = CobiGenFactory.create(this.cobigenConfigFolder.toURI());
      File tmpFolderCobiGen = this.tmpFolder.newFolder("cobigen_output");

      Object input = cobiGen.read(
          new File("src/test/resources/testdata/integrationtest/javaSources/EmployeeEntity.java").toPath(),
          Charset.forName("UTF-8"));
      List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);
      for (TemplateTo template : templates) {
        if (template.getId().equals("generated.java")) {
          // generate method which reads from the .cobigen file and add generated annotation
          GenerationReportTo report = cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()),
              false);
          String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");
          assertThat(report).isSuccessful();
          Path expectedFile = tmpFolderCobiGen.toPath().resolve("generated.java");
          assertThat(expectedFile).exists();
          String content = new String(Files.readAllBytes(Paths.get(expectedFile.toUri())));
          assertThat(content.trim()).isEqualToIgnoringWhitespace(
              "package com.example.domain.myapp.employeemanagement.common.api;" + LINE_SEPARATOR + LINE_SEPARATOR
                  + "import com.example.domain.myapp.general.common.api.ApplicationEntity;" + LINE_SEPARATOR
                  + LINE_SEPARATOR + "public interface Employee extends ApplicationEntity {" + LINE_SEPARATOR
                  + LINE_SEPARATOR + "  public Employee() {}" + LINE_SEPARATOR + LINE_SEPARATOR
                  + "  private string field;" + LINE_SEPARATOR + LINE_SEPARATOR + "  public boolean equals(Object obj);"
                  + LINE_SEPARATOR + LINE_SEPARATOR + "}");
          break;
        }
      }
    });
  }

}

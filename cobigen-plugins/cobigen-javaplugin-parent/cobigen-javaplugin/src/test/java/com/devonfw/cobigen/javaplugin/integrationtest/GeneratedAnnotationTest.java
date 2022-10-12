package com.devonfw.cobigen.javaplugin.integrationtest;

import static com.devonfw.cobigen.api.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;

public class GeneratedAnnotationTest extends AbstractIntegrationTest {

  /*
   * Test to check weather the entire generation process runs with new modifications in JavaMerger and merges the
   * generated annotation
   */
  @Test
  public void testGenerateAddedGeneratedAnnotations() throws Exception {

    CobiGen cobiGen = CobiGenFactory.create(this.cobigenConfigFolder.toURI());
    File tmpFolderCobiGen = this.tmpFolder.newFolder("cobigen_output");
    String LINE_SEPARATOR = java.lang.System.getProperty("line.separator");

    Object input = cobiGen.read(
        new File("src/test/resources/testdata/integrationtest/javaSources/EmployeeEntity.java").toPath(),
        Charset.forName("UTF-8"));
    List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);

    for (TemplateTo template : templates) {
      if (template.getId().equals("generated.java")) {
        GenerationReportTo report = cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()),
            false);
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
  }

}

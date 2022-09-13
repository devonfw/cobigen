package com.devonfw.cobigen.javaplugin.integrationtest;

import static com.devonfw.cobigen.api.assertj.CobiGenAsserts.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;

public class GeneratedAnnotationTest extends AbstractIntegrationTest {

  @Test
  public void testGenerateAddedGeneratedAnnotaions() throws Exception {

    CobiGen cobiGen = CobiGenFactory.create(this.cobigenConfigFolder.toURI(), true);
    File tmpFolderCobiGen = this.tmpFolder.newFolder("cobigen_output");

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
        Path expectedOutputToBeGenerated = Paths
            .get("src/test/resources/testdata/integrationtest/javaGenerated/ExpectedGeneratedOutput.java");
        assertThat(expectedFile).exists();
        assertThat(expectedFile).hasContent("@Generated");
        assertThat(expectedFile).isEqualByComparingTo(expectedOutputToBeGenerated);
        break;
      }
    }
  }

}

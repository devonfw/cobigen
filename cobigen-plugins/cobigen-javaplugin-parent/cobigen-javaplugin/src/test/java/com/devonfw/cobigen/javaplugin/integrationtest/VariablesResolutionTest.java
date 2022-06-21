package com.devonfw.cobigen.javaplugin.integrationtest;

import static com.devonfw.cobigen.api.assertj.CobiGenAsserts.assertThat;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.TemplateTo;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.javaplugin.integrationtest.common.AbstractIntegrationTest;

import junit.framework.AssertionFailedError;

/**
 * Test suite for variable resolution.
 */
public class VariablesResolutionTest extends AbstractIntegrationTest {

  /**
   * Tests that the path resolution is performed successfully in case of including path variables derived from variable
   * assignments retrieved by regex groups, which have been resolved to null. This bug has been introduced by changing
   * the model building from DOM to Bean model. The latter required to explicitly not to set <code>null</code> as a
   * value for variable resolution. Basically, this is odd, but we have to comply with backward compatibility and the
   * issue that we cannot encode unary-operators like ?? in a file path sufficiently.
   *
   * @throws Exception test fails
   */
  @Test
  public void testSuccessfulPathResolution_variableEqNull() throws Exception {

    CobiGen cobiGen = CobiGenFactory.create(this.cobigenConfigFolder.toURI(), true);
    File tmpFolderCobiGen = this.tmpFolder.newFolder("cobigen_output");

    Object input = cobiGen.read(
        new File("src/test/resources/testdata/integrationtest/javaSources/SampleEntity.java").toPath(),
        Charset.forName("UTF-8"));
    List<TemplateTo> templates = cobiGen.getMatchingTemplates(input);

    boolean methodTemplateFound = false;
    for (TemplateTo template : templates) {
      if (template.getId().equals("${variables.entityName}.java")) {
        GenerationReportTo report = cobiGen.generate(input, template, Paths.get(tmpFolderCobiGen.getAbsolutePath()),
            false);
        assertThat(report).isSuccessful();
        methodTemplateFound = true;
        break;
      }
    }

    if (!methodTemplateFound) {
      throw new AssertionFailedError("Test template not found");
    }
  }
}

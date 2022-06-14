package com.devonfw.cobigen.systemtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.api.CobiGen;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.IncrementTo;
import com.devonfw.cobigen.impl.CobiGenFactory;
import com.devonfw.cobigen.systemtest.common.AbstractApiTest;
import com.devonfw.cobigen.systemtest.util.PluginMockFactory;

/**
 * This test suit focuses on the transactional behavior of generation. In specific, that temporary files will be
 * generated first and just be applied to the final target sources if generation has been successfully.
 */
public class TransactionalGenerationTest extends AbstractApiTest {

  /** Root path to all resources used in this test case */
  private static String testFileRootPath = apiTestsRootPath + "TransactionalGenerationTest/";

  /**
   * Tests, whether no partial generation will be applied to the target if generation fails in between.
   *
   * @throws Throwable test fails
   */
  @Test
  public void testNoPartialApplicationOfGeneration() throws Throwable {

    // arrange
    Object generationInput = PluginMockFactory.createSimpleJavaConfigurationMock();
    File targetRoot = this.tmpFolder.newFolder();

    CobiGen cobigen = CobiGenFactory.create(new File(testFileRootPath + "templates").toURI(), true);
    List<IncrementTo> matchingIncrements = cobigen.getMatchingIncrements(generationInput);

    // act
    GenerationReportTo report = cobigen.generate(generationInput, matchingIncrements, targetRoot.toPath());

    // assert
    assertThat(report.isSuccessful()).isFalse();
    assertThat(new File(targetRoot, "valid.txt")).doesNotExist();
    assertThat(new File(targetRoot, "invalid.txt")).doesNotExist();
  }

}

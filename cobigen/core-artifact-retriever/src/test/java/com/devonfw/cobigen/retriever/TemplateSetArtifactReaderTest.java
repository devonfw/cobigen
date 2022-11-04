package com.devonfw.cobigen.retriever;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.devonfw.cobigen.retriever.reader.TemplateSetArtifactReader;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSetIncrement;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSetTag;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSetConfiguration;

/**
 * Test for MavenTemplateSetConfiguration
 *
 */
public class MavenTemplateSetConfigurationTest {

  /** Test data root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenReaderTest";

  /** Repositories on which the tests are performed on */
  private static List<TemplateSetTag> tagsList;

  /** Mirrors on which the tests are performed on */
  private static List<TemplateSetIncrement> incrementsList;

  /**
   * Used to initialize data needed for the tests
   */
  @BeforeClass
  public static void setUpClass() {

    tagsList = new ArrayList<>();
    incrementsList = new ArrayList<>();

    TemplateSetConfiguration model;

    model = TemplateSetArtifactReader
        .generateMavenTemplateSetConfiguration(Paths.get(testdataRoot).resolve("template-set.xml"));

    tagsList.addAll(model.getTags().getTagsList());
    incrementsList.addAll(model.getIncrements().getIncrementList());
  }

  /**
   * Test, whether the tags names of the tag elements are correctly mapped to a java class
   */
  @Test
  public void testGenerateMavenTemplateSetConfigurationTagsName() {

    String tagsName1 = tagsList.get(0).getName();
    String tagsName2 = tagsList.get(1).getName();

    assertThat(tagsName1).isEqualTo("Tag1");
    assertThat(tagsName2).isEqualTo("Tag2");
  }

  /**
   * Test, whether the description of the increments elements names are correctly mapped to a java class
   */
  @Test
  public void testGenerateMavenTemplateSetIncrementsDescription() {

    String description1 = incrementsList.get(0).getDescription();
    String description2 = incrementsList.get(1).getDescription();

    assertThat(description1).isEqualTo("Description of increment 1");
    assertThat(description2).isEqualTo("Description of increment 2");
  }

}

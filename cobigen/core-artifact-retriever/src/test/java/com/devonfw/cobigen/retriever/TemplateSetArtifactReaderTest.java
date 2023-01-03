package com.devonfw.cobigen.retriever;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.devonfw.cobigen.retriever.reader.TemplateSetArtifactReader;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSet;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSetIncrement;
import com.devonfw.cobigen.retriever.reader.to.model.TemplateSetTag;

/**
 * Test for MavenTemplateSetConfiguration
 *
 */
public class TemplateSetArtifactReaderTest {

  /** Test data root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/TemplateSetArtifactReaderTest";

  /**
   * Test, whether the tags names of the tag elements are correctly mapped to a java class
   */
  @Test
  public void testGenerateMavenTemplateSetConfigurationTagsName() {

    Path templateSetFile = Paths.get(testdataRoot).resolve("template-set.xml");

    TemplateSetArtifactReader artifactReader = new TemplateSetArtifactReader();

    TemplateSet templateSet = artifactReader.retrieveTemplateSet(templateSetFile);

    List<TemplateSetTag> tagsList = new ArrayList<>();
    tagsList.addAll(templateSet.getTemplateSetConfiguration().getContextConfiguration().getTags().getTagsList());

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

    Path templateSetFile = Paths.get(testdataRoot).resolve("template-set.xml");
    TemplateSetArtifactReader artifactReader = new TemplateSetArtifactReader();

    TemplateSet templateSet = artifactReader.retrieveTemplateSet(templateSetFile);

    List<TemplateSetIncrement> incrementsList = new ArrayList<>();
    incrementsList.addAll(
        templateSet.getTemplateSetConfiguration().getTemplatesConfiguration().getIncrements().getIncrementList());
    String description1 = incrementsList.get(0).getDescription();
    String description2 = incrementsList.get(1).getDescription();

    assertThat(description1).isEqualTo("Description of increment 1");
    assertThat(description2).isEqualTo("Description of increment 2");
  }

  /**
   * Test if the version of the template set can be retrieved from the file name
   */
  @Test
  public void testRetrieveTemplateSetVersionFromFilename() {

    Path templateSetFile = Paths.get(testdataRoot).resolve("crud-java-server-app-2021.08.001-template-set.xml");
    TemplateSetArtifactReader artifactReader = new TemplateSetArtifactReader();

    TemplateSet templateSet = artifactReader.retrieveTemplateSet(templateSetFile);
    assertThat(templateSet.getTemplateSetVersion()).isEqualTo("2021.08.001");
  }

}
